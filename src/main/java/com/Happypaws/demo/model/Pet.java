package com.Happypaws.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mascota")
    private Long idMascota;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false)
    private String especie;

    @Size(max = 80)
    private String raza;

    @Min(0)
    private Integer edad;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Appointment> citas = new ArrayList<>();

    @Transient
    public String getClienteNombre() {
        return cliente != null ? cliente.getRazonSocial() : "";
    }

    public Pet() {}

    public Pet(Long id, String nombre, String especie, String raza, Integer edad, Cliente cliente, List<Appointment> citas) {
        this.idMascota = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.cliente = cliente;
        this.citas = citas != null ? citas : new ArrayList<>();
    }


    public Long getIdMascota() {
        return this.idMascota;
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

    public Cliente getCliente() {
        return this.cliente;
    }

    public List<Appointment> getCitas() {
        return this.citas;
    }

    public void setId(Long id) {
        this.idMascota = id;
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

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setCitas(List<Appointment> citas) {
        this.citas = citas;
    }


    public void setIdMascota(Long id) { this.idMascota = id; }

    /** Compatibilidad de API: el identificador persistido es idMascota. */
    public Long getId() { return idMascota; }
}

