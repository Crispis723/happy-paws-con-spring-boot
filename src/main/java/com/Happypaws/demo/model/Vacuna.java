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
 * Registro de una vacuna aplicada a una mascota. Es un registro propio
 * (no forma parte de HistorialMascota) porque su ciclo de vida es distinto:
 * se consulta principalmente para saber "¿qué le falta aplicar y cuándo?",
 * no como parte de la narrativa de una consulta puntual.
 */
@Entity
@Table(name = "vacunas")
public class Vacuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vacuna")
    private Long idVacuna;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false)
    private Pet mascota;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotNull
    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "proxima_dosis")
    private LocalDate proximaDosis;

    @Size(max = 50)
    @Column(length = 50)
    private String lote;

    @Size(max = 150)
    @Column(length = 150)
    private String veterinario;

    public Vacuna() {
    }

    public Long getId() {
        return idVacuna;
    }

    public void setId(Long id) {
        this.idVacuna = id;
    }

    public Pet getMascota() {
        return mascota;
    }

    public void setMascota(Pet mascota) {
        this.mascota = mascota;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDate getProximaDosis() {
        return proximaDosis;
    }

    public void setProximaDosis(LocalDate proximaDosis) {
        this.proximaDosis = proximaDosis;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }
}
