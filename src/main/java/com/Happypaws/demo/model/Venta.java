package com.Happypaws.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long idVenta;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true)
    private String numero;

    @NotNull
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @NotBlank
    @Size(max = 30)
    @Column(name = "forma_pago", nullable = false)
    private String formaPago;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false)
    private String estado = "registrada";

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal igv = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("idDetalleVenta ASC")
    private List<VentaDetalle> detalles = new ArrayList<>();

    @Transient
    public String getClienteNombre() {
        return cliente != null ? cliente.getRazonSocial() : "";
    }

    /** Reemplaza la lista de detalles manteniendo la relación bidireccional. */
    public void setDetalles(List<VentaDetalle> nuevosDetalles) {
        this.detalles.clear();
        if (nuevosDetalles != null) {
            for (VentaDetalle detalle : nuevosDetalles) {
                detalle.setVenta(this);
                this.detalles.add(detalle);
            }
        }
    }

    public List<VentaDetalle> getDetalles() {
        return detalles;
    }

    public Venta() {
    }

    public Venta(Long id, String numero, LocalDate fecha, Cliente cliente, String formaPago, String estado, BigDecimal total) {
        this.idVenta = id;
        this.numero = numero;
        this.fecha = fecha;
        this.cliente = cliente;
        this.formaPago = formaPago;
        this.estado = estado;
        this.total = total;
    }

    public Long getIdVenta() {
        return this.idVenta;
    }

    public String getNumero() {
        return this.numero;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public Cliente getCliente() {
        return this.cliente;
    }

    public String getFormaPago() {
        return this.formaPago;
    }

    public String getEstado() {
        return this.estado;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIgv() {
        return this.igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public void setId(Long id) {
        this.idVenta = id;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }


    public void setIdVenta(Long id) { this.idVenta = id; }

    /** Compatibilidad de API: el identificador persistido es idVenta. */
    public Long getId() { return idVenta; }
}