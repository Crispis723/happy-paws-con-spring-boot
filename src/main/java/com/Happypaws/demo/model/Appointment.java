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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Long idCita;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false)
    private Pet mascota;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario")
    private User veterinario;

    @NotNull
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String motivo;

    @Transient
    public String getVeterinarioNombre() {
        return veterinario != null ? veterinario.getName() : "";
    }

    public Appointment() {}

    public Appointment(Long id, Pet mascota, Cliente cliente, User veterinario, LocalDateTime fechaHora, String motivo) {
        this.idCita = id;
        this.mascota = mascota;
        this.cliente = cliente;
        this.veterinario = veterinario;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
    }


    public Long getIdCita() {
        return this.idCita;
    }

    public Pet getMascota() {
        return this.mascota;
    }

    public Cliente getCliente() {
        return this.cliente;
    }

    public User getVeterinario() {
        return this.veterinario;
    }

    public LocalDateTime getFechaHora() {
        return this.fechaHora;
    }

    public String getMotivo() {
        return this.motivo;
    }

    public void setId(Long id) {
        this.idCita = id;
    }

    public void setMascota(Pet mascota) {
        this.mascota = mascota;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setVeterinario(User veterinario) {
        this.veterinario = veterinario;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }


    public void setIdCita(Long id) { this.idCita = id; }

    /** Compatibilidad de API: el identificador persistido es idCita. */
    public Long getId() { return idCita; }
}
