package com.Happypaws.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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


    public Long getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getEspecie() {
        return this.especie;
    }

    public String getRaza() {
        return this.raza;
    }

    public Integer getEdad() {
        return this.edad;
    }

    public Long getClienteId() {
        return this.clienteId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

}