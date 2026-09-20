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
@Table(name = "compras")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Long idCompra;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true)
    private String numero;

    @NotNull
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @NotBlank
    @Size(max = 20)
    @Column(name = "comprobante_tipo_codigo", nullable = false)
    private String comprobanteTipoCodigo;

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

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("idDetalleCompra ASC")
    private List<CompraDetalle> detalles = new ArrayList<>();

    @Transient
    public String getProveedorNombre() {
        return proveedor != null ? proveedor.getRazonSocial() : "";
    }

    /** Reemplaza la lista de detalles manteniendo la relación bidireccional. */
    public void setDetalles(List<CompraDetalle> nuevosDetalles) {
        this.detalles.clear();
        if (nuevosDetalles != null) {
            for (CompraDetalle detalle : nuevosDetalles) {
                detalle.setCompra(this);
                this.detalles.add(detalle);
            }
        }
    }

    public List<CompraDetalle> getDetalles() {
        return detalles;
    }

    public Compra() {
    }

    public Compra(Long id, String numero, LocalDate fecha, Proveedor proveedor, String comprobanteTipoCodigo, String formaPago, String estado, BigDecimal total) {
        this.idCompra = id;
        this.numero = numero;
        this.fecha = fecha;
        this.proveedor = proveedor;
        this.comprobanteTipoCodigo = comprobanteTipoCodigo;
        this.formaPago = formaPago;
        this.estado = estado;
        this.total = total;
    }

    public Long getIdCompra() {
        return this.idCompra;
    }

    public String getNumero() {
        return this.numero;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public Proveedor getProveedor() {
        return this.proveedor;
    }

    public String getComprobanteTipoCodigo() {
        return this.comprobanteTipoCodigo;
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
        this.idCompra = id;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public void setComprobanteTipoCodigo(String comprobanteTipoCodigo) {
        this.comprobanteTipoCodigo = comprobanteTipoCodigo;
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


    public void setIdCompra(Long id) { this.idCompra = id; }

    /** Compatibilidad de API: el identificador persistido es idCompra. */
    public Long getId() { return idCompra; }
}