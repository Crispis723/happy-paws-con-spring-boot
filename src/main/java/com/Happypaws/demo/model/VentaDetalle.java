package com.Happypaws.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Línea de detalle de una venta: un producto, su cantidad y el precio
 * unitario tomado del producto en el momento del registro.
 */
@Entity
@Table(name = "venta_detalles")
public class VentaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_venta")
    private Long idDetalleVenta;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    @JsonIgnore
    private Venta venta;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    /** Columna generada por la base de datos (cantidad * precio_unitario); solo lectura. */
    @Column(nullable = false, precision = 12, scale = 2, insertable = false, updatable = false)
    private BigDecimal subtotal;

    public VentaDetalle() {
    }

    public VentaDetalle(Producto producto, Integer cantidad, BigDecimal precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    @Transient
    public BigDecimal getSubtotalCalculado() {
        if (cantidad == null || precioUnitario == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
    }

    @Transient
    public BigDecimal getPorcentajeIgv() {
        if (producto == null || producto.getAfectacionTipo() == null || producto.getAfectacionTipo().getPorcentaje() == null) {
            return BigDecimal.ZERO;
        }
        return producto.getAfectacionTipo().getPorcentaje();
    }

    @Transient
    public BigDecimal getIgvCalculado() {
        return getSubtotalCalculado().multiply(getPorcentajeIgv())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public Long getIdDetalleVenta() {
        return idDetalleVenta;
    }

    public void setIdDetalleVenta(Long idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal != null ? subtotal : getSubtotalCalculado();
    }

    /** Compatibilidad de API. */
    public Long getId() {
        return idDetalleVenta;
    }
}
