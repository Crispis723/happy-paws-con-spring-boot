package com.Happypaws.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * DTO para crear y actualizar citas veterinarias
 * Incluye validaciones completas de campos
 */
public class AppointmentDTO {

    private Long id;

    @NotNull(message = "La mascota es requerida")
    @Positive(message = "El ID de la mascota debe ser un número positivo")
    private Long petId;

    @NotNull(message = "El cliente es requerido")
    @Positive(message = "El ID del cliente debe ser un número positivo")
    private Long clienteId;

    @NotNull(message = "El veterinario es requerido")
    @Positive(message = "El ID del veterinario debe ser un número positivo")
    private Long veterinarioId;

    @NotNull(message = "La fecha y hora es requerida")
    @Future(message = "La fecha y hora debe ser en el futuro (mínimo 15 minutos a partir de ahora)")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El motivo no puede estar vacío")
    @Size(min = 5, max = 255, message = "El motivo debe tener entre 5 y 255 caracteres")
    private String motivo;

    // Getters y Setters
    public Long getId() {
        return this.id;
    }

    public Long getPetId() {
        return this.petId;
    }

    public Long getClienteId() {
        return this.clienteId;
    }

    public Long getVeterinarioId() {
        return this.veterinarioId;
    }

    public LocalDateTime getFechaHora() {
        return this.fechaHora;
    }

    public String getMotivo() {
        return this.motivo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public void setVeterinarioId(Long veterinarioId) {
        this.veterinarioId = veterinarioId;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

}