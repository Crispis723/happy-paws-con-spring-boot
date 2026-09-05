package com.Happypaws.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
public class AppointmentDTO {

    private Long id;

    @NotNull
    private Long petId;

    @NotNull
    private Long clienteId;

    @NotNull
    private Long veterinarioId;

    @NotNull
    private LocalDateTime fechaHora;

    @NotBlank
    @Size(max = 255)
    private String motivo;


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