package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Producto;
import com.Happypaws.demo.model.Venta;
import com.Happypaws.demo.model.VentaDetalle;
import com.Happypaws.demo.repository.ProductoRepository;
import com.Happypaws.demo.repository.VentaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentaService {

    private final VentaRepository repository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository repository, ProductoRepository productoRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
    }

    public List<Venta> listar() {
        return repository.findAll();
    }

    public List<Venta> listarPorClienteId(Long clienteId) {
        return repository.findByClienteIdCliente(clienteId);
    }

    public Optional<Venta> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Venta guardar(Venta venta) {
        return repository.save(venta);
    }

    public Venta actualizar(Venta venta) {
        if (venta.getIdVenta() == null || !repository.existsById(venta.getIdVenta())) {
            throw new ResourceNotFoundException("Venta no encontrada");
        }
        return repository.save(venta);
    }

    /**
     * Elimina una venta y devuelve al stock las cantidades que había
     * descontado.
     */
    @Transactional
    public void eliminar(Long id) {
        Venta venta = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

        for (VentaDetalle detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }

        repository.deleteById(id);
    }

    /**
     * Registra (o actualiza) una venta junto con sus líneas de detalle.
     * El precio unitario y el porcentaje de IGV se toman siempre del producto
     * almacenado en la base de datos (nunca de lo enviado por el formulario),
     * de modo que el subtotal, el IGV y el total resultan de un cálculo
     * confiable en el servidor. Si un mismo producto se agrega más de una
     * vez, las cantidades se combinan en una sola línea.
     *
     * @param venta        datos de cabecera de la venta (cliente, fecha, etc.)
     * @param productoIds  ids de los productos seleccionados, en el mismo
     *                     orden que {@code cantidades}
     * @param cantidades   cantidades correspondientes a cada producto
     */
    @Transactional
    public Venta guardarConDetalle(Venta venta, List<Long> productoIds, List<Integer> cantidades) {
        if (productoIds == null || productoIds.isEmpty()) {
            throw new IllegalArgumentException("Debes agregar al menos un producto a la venta");
        }
        if (cantidades == null || cantidades.size() != productoIds.size()) {
            throw new IllegalArgumentException("Las cantidades no coinciden con los productos seleccionados");
        }

        boolean esActualizacion = venta.getIdVenta() != null;

        if (esActualizacion && !repository.existsById(venta.getIdVenta())) {
            throw new ResourceNotFoundException("Venta no encontrada");
        }

        // Cantidades ya descontadas del stock en un registro previo de esta
        // misma venta (vacío si es una venta nueva).
        Map<Long, Integer> cantidadesAnteriores = esActualizacion
                ? cantidadesPorProducto(repository.findById(venta.getIdVenta())
                        .map(Venta::getDetalles)
                        .orElse(List.of()))
                : Map.of();

        // Combina cantidades cuando el mismo producto fue agregado más de una vez.
        Map<Long, Integer> cantidadPorProducto = new LinkedHashMap<>();
        for (int i = 0; i < productoIds.size(); i++) {
            Long idProducto = productoIds.get(i);
            Integer cantidad = cantidades.get(i);
            if (idProducto == null || cantidad == null || cantidad <= 0) {
                continue;
            }
            cantidadPorProducto.merge(idProducto, cantidad, Integer::sum);
        }

        if (cantidadPorProducto.isEmpty()) {
            throw new IllegalArgumentException("Debes agregar al menos un producto con cantidad válida");
        }

        List<VentaDetalle> nuevosDetalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal igv = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cantidadPorProducto.entrySet()) {
            Producto producto = productoRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado (id " + entry.getKey() + ")"));

            BigDecimal precioUnitario = producto.getPrecioUnitario() != null ? producto.getPrecioUnitario() : BigDecimal.ZERO;
            BigDecimal cantidad = BigDecimal.valueOf(entry.getValue());
            BigDecimal subtotalLinea = precioUnitario.multiply(cantidad).setScale(2, RoundingMode.HALF_UP);

            BigDecimal porcentajeIgv = (producto.getAfectacionTipo() != null && producto.getAfectacionTipo().getPorcentaje() != null)
                    ? producto.getAfectacionTipo().getPorcentaje()
                    : BigDecimal.ZERO;
            BigDecimal igvLinea = subtotalLinea.multiply(porcentajeIgv)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            subtotal = subtotal.add(subtotalLinea);
            igv = igv.add(igvLinea);

            nuevosDetalles.add(new VentaDetalle(producto, entry.getValue(), precioUnitario));

            // El stock solo debe reflejar la DIFERENCIA respecto a lo que ya
            // se había descontado antes (0 si es una venta nueva), para no
            // duplicar el efecto al editar una venta existente.
            int cantidadAnterior = cantidadesAnteriores.getOrDefault(entry.getKey(), 0);
            int delta = entry.getValue() - cantidadAnterior;

            if (delta > 0 && producto.getStock() < delta) {
                throw new IllegalArgumentException(
                        "Stock insuficiente para \"" + producto.getNombre()
                                + "\" (disponible: " + producto.getStock()
                                + ", requerido adicional: " + delta + ")");
            }

            if (delta != 0) {
                producto.setStock(producto.getStock() - delta);
                productoRepository.save(producto);
            }
        }

        // Productos que estaban en la venta original y fueron quitados:
        // se devuelve el stock que se les había descontado.
        for (Map.Entry<Long, Integer> anterior : cantidadesAnteriores.entrySet()) {
            if (!cantidadPorProducto.containsKey(anterior.getKey())) {
                Producto producto = productoRepository.findById(anterior.getKey())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Producto no encontrado (id " + anterior.getKey() + ")"));
                producto.setStock(producto.getStock() + anterior.getValue());
                productoRepository.save(producto);
            }
        }

        venta.setDetalles(nuevosDetalles);
        venta.setSubtotal(subtotal);
        venta.setIgv(igv);
        venta.setTotal(subtotal.add(igv).setScale(2, RoundingMode.HALF_UP));

        return repository.save(venta);
    }

    private Map<Long, Integer> cantidadesPorProducto(List<VentaDetalle> detalles) {
        Map<Long, Integer> resultado = new LinkedHashMap<>();
        for (VentaDetalle detalle : detalles) {
            resultado.merge(detalle.getProducto().getIdProducto(), detalle.getCantidad(), Integer::sum);
        }
        return resultado;
    }
}