package com.Happypaws.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 50, message = "El código no puede superar los 50 caracteres")
    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @NotNull(message = "Debe seleccionar una unidad")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad", nullable = false)
    private Unidad unidad;

    @NotNull(message = "Debe seleccionar un tipo de afectación")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_afectacion_tipo", nullable = false)
    private AfectacionTipo afectacionTipo;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "El precio unitario no puede ser negativo"
    )
    @Column(
        name = "precio_unitario",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal precioUnitario;

    @NotNull(message = "El stock es obligatorio")
    @Min(
        value = 0,
        message = "El stock no puede ser negativo"
    )
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Size(
        max = 255,
        message = "El nombre de la imagen no puede superar los 255 caracteres"
    )
    @Column(name = "imagen", length = 255)
    private String imagen;

    public Producto() {
    }

    public Producto(
            Long idProducto,
            String codigo,
            String nombre,
            String descripcion,
            Unidad unidad,
            AfectacionTipo afectacionTipo,
            BigDecimal precioUnitario,
            Integer stock,
            String imagen) {

        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.unidad = unidad;
        this.afectacionTipo = afectacionTipo;
        this.precioUnitario = precioUnitario;
        this.stock = stock;
        this.imagen = imagen;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Unidad getUnidad() {
        return unidad;
    }

    public void setUnidad(Unidad unidad) {
        this.unidad = unidad;
    }

    public AfectacionTipo getAfectacionTipo() {
        return afectacionTipo;
    }

    public void setAfectacionTipo(AfectacionTipo afectacionTipo) {
        this.afectacionTipo = afectacionTipo;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Long getId() {
        return idProducto;
    }

    public void setId(Long id) {
        this.idProducto = id;
    }
}
