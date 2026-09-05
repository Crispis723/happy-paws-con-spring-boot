package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Compra;
import com.Happypaws.demo.model.CompraDetalle;
import com.Happypaws.demo.model.Producto;
import com.Happypaws.demo.repository.CompraRepository;
import com.Happypaws.demo.repository.ProductoRepository;
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
public class CompraService {

    private final CompraRepository repository;
    private final ProductoRepository productoRepository;

    public CompraService(CompraRepository repository, ProductoRepository productoRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
    }

    public List<Compra> listar() {
        return repository.findAll();
    }

    public Optional<Compra> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Compra guardar(Compra compra) {
        return repository.save(compra);
    }

    public Compra actualizar(Compra compra) {
        if (compra.getIdCompra() == null || !repository.existsById(compra.getIdCompra())) {
            throw new ResourceNotFoundException("Compra no encontrada");
        }
        return repository.save(compra);
    }

    @Transactional
    public Compra guardarConDetalle(Compra compra, List<Long> productoIds, List<Integer> cantidades) {
        if (productoIds == null || productoIds.isEmpty()) {
            throw new IllegalArgumentException("Debes agregar al menos un producto a la compra");
        }
        if (cantidades == null || cantidades.size() != productoIds.size()) {
            throw new IllegalArgumentException("Las cantidades no coinciden con los productos seleccionados");
        }

        boolean esActualizacion = compra.getIdCompra() != null;

        if (esActualizacion && !repository.existsById(compra.getIdCompra())) {
            throw new ResourceNotFoundException("Compra no encontrada");
        }

        // Cantidades ya aplicadas al stock en un registro previo de esta
        // misma compra (vacío si es una compra nueva).
        Map<Long, Integer> cantidadesAnteriores = esActualizacion
                ? cantidadesPorProducto(repository.findById(compra.getIdCompra())
                        .map(Compra::getDetalles)
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

        List<CompraDetalle> nuevosDetalles = new ArrayList<>();
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

            nuevosDetalles.add(new CompraDetalle(producto, entry.getValue(), precioUnitario));

            // El stock solo debe reflejar la DIFERENCIA respecto a lo que ya
            // se había aplicado antes (0 si es una compra nueva), para no
            // duplicar el efecto al editar una compra existente.
            int cantidadAnterior = cantidadesAnteriores.getOrDefault(entry.getKey(), 0);
            int delta = entry.getValue() - cantidadAnterior;
            if (delta != 0) {
                producto.setStock(producto.getStock() + delta);
                productoRepository.save(producto);
            }
        }

        // Productos que estaban en la compra original y fueron quitados:
        // se revierte el stock que se les había sumado.
        for (Map.Entry<Long, Integer> anterior : cantidadesAnteriores.entrySet()) {
            if (!cantidadPorProducto.containsKey(anterior.getKey())) {
                Producto producto = productoRepository.findById(anterior.getKey())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Producto no encontrado (id " + anterior.getKey() + ")"));
                producto.setStock(producto.getStock() - anterior.getValue());
                productoRepository.save(producto);
            }
        }

        compra.setDetalles(nuevosDetalles);
        compra.setSubtotal(subtotal);
        compra.setIgv(igv);
        compra.setTotal(subtotal.add(igv).setScale(2, RoundingMode.HALF_UP));

        return repository.save(compra);
    }

    /**
     * Elimina una compra y revierte el stock que había sumado.
     */
    @Transactional
    public void eliminar(Long id) {
        Compra compra = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada"));

        for (CompraDetalle detalle : compra.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);
        }

        repository.deleteById(id);
    }

    private Map<Long, Integer> cantidadesPorProducto(List<CompraDetalle> detalles) {
        Map<Long, Integer> resultado = new LinkedHashMap<>();
        for (CompraDetalle detalle : detalles) {
            resultado.merge(detalle.getProducto().getIdProducto(), detalle.getCantidad(), Integer::sum);
        }
        return resultado;
    }
}
