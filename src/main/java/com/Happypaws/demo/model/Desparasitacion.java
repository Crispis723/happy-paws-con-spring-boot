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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Registro de una desparasitación (interna o externa) aplicada a una
 * mascota. Registro propio, con el mismo criterio que {@link Vacuna}.
 */
@Entity
@Table(name = "desparasitaciones")
public class Desparasitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_desparasitacion")
    private Long idDesparasitacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false)
    private Pet mascota;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String producto;

    @NotNull
    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "proxima_fecha")
    private LocalDate proximaFecha;

    @Size(max = 50)
    @Column(length = 50)
    private String dosis;

    public Desparasitacion() {
    }

    public Long getId() {
        return idDesparasitacion;
    }

    public void setId(Long id) {
        this.idDesparasitacion = id;
    }

    public Pet getMascota() {
        return mascota;
    }

    public void setMascota(Pet mascota) {
        this.mascota = mascota;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDate getProximaFecha() {
        return proximaFecha;
    }

    public void setProximaFecha(LocalDate proximaFecha) {
        this.proximaFecha = proximaFecha;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }
}
