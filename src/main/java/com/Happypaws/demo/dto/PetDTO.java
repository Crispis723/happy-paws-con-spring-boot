package com.Happypaws.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetDTO {

    private Long id;

    @NotBlank
    @Size(max = 120)
    private String nombre;

    @NotBlank
    @Size(max = 80)
    private String especie;

    @Size(max = 80)
    private String raza;

    @Min(0)
    private Integer edad;

    @NotNull
    private Long clienteId;
}