package com.Happypaws.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "afectacion_tipos")
public class AfectacionTipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_afectacion_tipo")
    private Long idAfectacionTipo;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20, message = "El código no puede superar los 20 caracteres")
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @NotBlank(message = "La letra es obligatoria")
    @Size(max = 1, message = "La letra debe tener un solo carácter")
    @Column(name = "letra", nullable = false, length = 1)
    private String letra;

    @NotNull(message = "El porcentaje es obligatorio")
    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "El porcentaje no puede ser negativo"
    )
    @Column(
        name = "porcentaje",
        nullable = false,
        precision = 5,
        scale = 2
    )
    private BigDecimal porcentaje;

    // =========================================================
    // CONSTRUCTOR VACÍO
    // =========================================================

    public AfectacionTipo() {
    }

    // =========================================================
    // CONSTRUCTOR COMPLETO
    // =========================================================

    public AfectacionTipo(
            Long idAfectacionTipo,
            String codigo,
            String nombre,
            String descripcion,
            String letra,
            BigDecimal porcentaje) {

        this.idAfectacionTipo = idAfectacionTipo;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.letra = letra;
        this.porcentaje = porcentaje;
    }

    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================

    public Long getIdAfectacionTipo() {
        return idAfectacionTipo;
    }

    public void setIdAfectacionTipo(Long idAfectacionTipo) {
        this.idAfectacionTipo = idAfectacionTipo;
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

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    // =========================================================
    // COMPATIBILIDAD
    // =========================================================

    public Long getId() {
        return idAfectacionTipo;
    }

    public void setId(Long id) {
        this.idAfectacionTipo = id;
    }
}
