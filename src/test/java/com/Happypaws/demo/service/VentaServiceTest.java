package com.Happypaws.demo.service;

import com.Happypaws.demo.model.AfectacionTipo;
import com.Happypaws.demo.model.Producto;
import com.Happypaws.demo.model.Venta;
import com.Happypaws.demo.model.VentaDetalle;
import com.Happypaws.demo.repository.ProductoRepository;
import com.Happypaws.demo.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Verifica que registrar, editar y eliminar una venta mueve el stock del
 * producto correctamente, y que no se permite vender más de lo disponible.
 */
@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private VentaService ventaService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        AfectacionTipo gravado = new AfectacionTipo();
        gravado.setPorcentaje(new BigDecimal("18.00"));

        producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Alimento para gato");
        producto.setPrecioUnitario(new BigDecimal("10.00"));
        producto.setStock(20);
        producto.setAfectacionTipo(gravado);
    }

    @Test
    void ventaNueva_decrementaElStockEnLaCantidadVendida() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        Venta venta = new Venta();

        ventaService.guardarConDetalle(venta, List.of(1L), List.of(6));

        assertThat(producto.getStock()).isEqualTo(14); // 20 - 6
    }

    @Test
    void ventaQueSuperaElStockDisponible_lanzaExcepcionYNoModificaNada() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Venta venta = new Venta();

        assertThatThrownBy(() -> ventaService.guardarConDetalle(venta, List.of(1L), List.of(999)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock insuficiente");

        assertThat(producto.getStock()).isEqualTo(20); // sin cambios
    }

    @Test
    void editarVenta_soloAplicaLaDiferenciaDeStock_noDuplicaElEfecto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        // Venta ya existente en BD con 6 unidades (ya descontadas del stock
        // cuando se creó originalmente: 20 - 6 = 14, que es el stock actual).
        producto.setStock(14);

        Venta ventaExistente = new Venta();
        ventaExistente.setIdVenta(77L);
        ventaExistente.setDetalles(List.of(new VentaDetalle(producto, 6, new BigDecimal("10.00"))));

        when(ventaRepository.existsById(77L)).thenReturn(true);
        when(ventaRepository.findById(77L)).thenReturn(Optional.of(ventaExistente));

        Venta ventaEditada = new Venta();
        ventaEditada.setIdVenta(77L);

        // Se edita para que ahora sean 9 unidades: el stock solo debe bajar
        // en 3 (la diferencia), no en 9 de nuevo.
        ventaService.guardarConDetalle(ventaEditada, List.of(1L), List.of(9));

        assertThat(producto.getStock()).isEqualTo(11); // 14 - (9 - 6)
    }

    @Test
    void eliminarVenta_devuelveAlStockLaCantidadVendida() {
        producto.setStock(14); // ya se le habían descontado 6

        Venta venta = new Venta();
        venta.setIdVenta(77L);
        venta.setDetalles(List.of(new VentaDetalle(producto, 6, new BigDecimal("10.00"))));

        when(ventaRepository.findById(77L)).thenReturn(Optional.of(venta));

        ventaService.eliminar(77L);

        assertThat(producto.getStock()).isEqualTo(20); // 14 + 6 devueltos
    }

    @Test
    void ventaSinProductos_lanzaExcepcion() {
        Venta venta = new Venta();

        assertThatThrownBy(() -> ventaService.guardarConDetalle(venta, List.of(), List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("al menos un producto");
    }
}
