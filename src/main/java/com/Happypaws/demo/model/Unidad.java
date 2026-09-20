package com.Happypaws.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "units")
public class Unidad {

    // =========================================================
    // ID
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad")
    private Long idUnidad;


    // =========================================================
    // CÓDIGO
    // =========================================================

    @NotBlank(message = "El código es obligatorio")
    @Size(
        max = 20,
        message = "El código no puede superar los 20 caracteres"
    )
    @Column(
        name = "codigo",
        nullable = false,
        unique = true,
        length = 20
    )
    private String codigo;


    // =========================================================
    // DESCRIPCIÓN
    // =========================================================

    @NotBlank(message = "La descripción es obligatoria")
    @Size(
        max = 150,
        message = "La descripción no puede superar los 150 caracteres"
    )
    @Column(
        name = "descripcion",
        nullable = false,
        length = 150
    )
    private String descripcion;


    // =========================================================
    // CONSTRUCTOR VACÍO
    // =========================================================

    public Unidad() {
    }


    // =========================================================
    // CONSTRUCTOR COMPLETO
    // =========================================================

    public Unidad(
            Long idUnidad,
            String codigo,
            String descripcion) {

        this.idUnidad = idUnidad;
        this.codigo = codigo;
        this.descripcion = descripcion;
    }


    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================

    public Long getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(Long idUnidad) {
        this.idUnidad = idUnidad;
    }


    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    // =========================================================
    // COMPATIBILIDAD
    // =========================================================

    /*
     * Se mantienen estos métodos porque alguna parte del proyecto
     * podría estar utilizando getId() / setId().
     *
     * La columna real de la BD es:
     * id_unidad
     */

    public Long getId() {
        return idUnidad;
    }

    public void setId(Long id) {
        this.idUnidad = id;
    }
}
