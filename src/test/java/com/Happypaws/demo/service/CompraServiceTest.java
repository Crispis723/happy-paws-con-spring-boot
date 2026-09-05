package com.Happypaws.demo.service;

import com.Happypaws.demo.model.AfectacionTipo;
import com.Happypaws.demo.model.Compra;
import com.Happypaws.demo.model.CompraDetalle;
import com.Happypaws.demo.model.Producto;
import com.Happypaws.demo.repository.CompraRepository;
import com.Happypaws.demo.repository.ProductoRepository;
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
 * Verifica que registrar, editar y eliminar una compra mueve el stock del
 * producto correctamente, incluyendo el caso de edición (que no debe
 * duplicar el efecto de una compra ya aplicada).
 */
@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private CompraService compraService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        AfectacionTipo gravado = new AfectacionTipo();
        gravado.setPorcentaje(new BigDecimal("18.00"));

        producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Alimento para perro");
        producto.setPrecioUnitario(new BigDecimal("10.00"));
        producto.setStock(5);
        producto.setAfectacionTipo(gravado);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
        when(compraRepository.save(any(Compra.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void compraNueva_incrementaElStockEnLaCantidadComprada() {
        Compra compra = new Compra();

        compraService.guardarConDetalle(compra, List.of(1L), List.of(10));

        assertThat(producto.getStock()).isEqualTo(15); // 5 iniciales + 10 comprados
    }

    @Test
    void compraNueva_calculaSubtotalIgvYTotalCorrectamente() {
        Compra compra = new Compra();

        Compra guardada = compraService.guardarConDetalle(compra, List.of(1L), List.of(10));

        assertThat(guardada.getSubtotal()).isEqualByComparingTo("100.00"); // 10 x 10.00
        assertThat(guardada.getIgv()).isEqualByComparingTo("18.00");       // 18% de 100
        assertThat(guardada.getTotal()).isEqualByComparingTo("118.00");
    }

    @Test
    void editarCompra_soloAplicaLaDiferenciaDeStock_noDuplicaElEfecto() {
        // Compra ya existente en BD con 10 unidades del producto (que ya
        // se sumaron al stock cuando se creó originalmente).
        Compra compraExistente = new Compra();
        compraExistente.setIdCompra(99L);
        compraExistente.setDetalles(List.of(new CompraDetalle(producto, 10, new BigDecimal("10.00"))));

        when(compraRepository.existsById(99L)).thenReturn(true);
        when(compraRepository.findById(99L)).thenReturn(Optional.of(compraExistente));

        Compra compraEditada = new Compra();
        compraEditada.setIdCompra(99L);

        // Se edita para que ahora sean 15 unidades: el stock solo debe
        // subir en 5 (la diferencia), no en 15 de nuevo.
        compraService.guardarConDetalle(compraEditada, List.of(1L), List.of(15));

        assertThat(producto.getStock()).isEqualTo(10); // 5 iniciales + (15 - 10)
    }

    @Test
    void editarCompra_alQuitarUnProducto_revierteElStockQueLeHabiaSumado() {
        Producto otroProducto = new Producto();
        otroProducto.setIdProducto(2L);
        otroProducto.setNombre("Shampoo antipulgas");
        otroProducto.setPrecioUnitario(new BigDecimal("20.00"));
        otroProducto.setStock(8);
        AfectacionTipo exonerado = new AfectacionTipo();
        exonerado.setPorcentaje(BigDecimal.ZERO);
        otroProducto.setAfectacionTipo(exonerado);
        when(productoRepository.findById(2L)).thenReturn(Optional.of(otroProducto));

        // Compra original: 10 del producto 1 y 3 del producto 2.
        Compra compraExistente = new Compra();
        compraExistente.setIdCompra(99L);
        compraExistente.setDetalles(List.of(
                new CompraDetalle(producto, 10, new BigDecimal("10.00")),
                new CompraDetalle(otroProducto, 3, new BigDecimal("20.00"))
        ));

        when(compraRepository.existsById(99L)).thenReturn(true);
        when(compraRepository.findById(99L)).thenReturn(Optional.of(compraExistente));

        // Se edita dejando solo el producto 1, sin cambios en su cantidad.
        Compra compraEditada = new Compra();
        compraEditada.setIdCompra(99L);

        compraService.guardarConDetalle(compraEditada, List.of(1L), List.of(10));

        assertThat(producto.getStock()).isEqualTo(15);      // 5 + 10, sin cambios
        assertThat(otroProducto.getStock()).isEqualTo(5);   // 8 - 3 revertidos
    }

    @Test
    void eliminarCompra_revierteElStockQueHabiaSumado() {
        Compra compra = new Compra();
        compra.setIdCompra(99L);
        compra.setDetalles(List.of(new CompraDetalle(producto, 10, new BigDecimal("10.00"))));

        when(compraRepository.findById(99L)).thenReturn(Optional.of(compra));

        compraService.eliminar(99L);

        assertThat(producto.getStock()).isEqualTo(-5); // 5 - 10 (se revierte la compra)
    }

    @Test
    void compraSinProductos_lanzaExcepcion() {
        Compra compra = new Compra();

        assertThatThrownBy(() -> compraService.guardarConDetalle(compra, List.of(), List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("al menos un producto");
    }
}
