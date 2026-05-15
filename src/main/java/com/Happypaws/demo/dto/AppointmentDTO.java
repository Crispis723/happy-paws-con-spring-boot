package com.Happypaws.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentDTO {

    private Long id;

    @NotNull
    private Long petId;

    @NotNull
    private Long clienteId;

    @NotNull
    private Long veterinarioId;

    @NotNull
    private LocalDate fecha;

    @NotBlank
    @Size(max = 255)
    private String motivo;
}