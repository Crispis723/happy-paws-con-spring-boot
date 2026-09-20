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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
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

    @Size(max = 80)
    @Column(name = "chip")
    private String chip;

    /**
     * Edad manual en años, para cuando no se conoce la fecha de nacimiento
     * exacta (por ejemplo, mascotas adoptadas). Si {@link #fechaNacimiento}
     * está informada, la ficha muestra la edad calculada a partir de esta
     * en vez de este valor (ver {@link #getEdadCalculada()}).
     */
    @Min(0)
    private Integer edad;

    // =========================================================
    // DATOS BÁSICOS ADICIONALES
    // =========================================================

    @Size(max = 20)
    private String sexo;

    @PastOrPresent
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Size(max = 60)
    private String color;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "peso_actual", precision = 6, scale = 2)
    private BigDecimal pesoActual;

    @Column(name = "foto", length = 255)
    private String foto;

    // =========================================================
    // DATOS MÉDICOS
    // =========================================================

    @Column(nullable = false)
    private Boolean esterilizado = false;

    @Size(max = 500)
    @Column(length = 500)
    private String alergias;

    @Size(max = 500)
    @Column(name = "enfermedades_conocidas", length = 500)
    private String enfermedadesConocidas;

    @Size(max = 500)
    @Column(name = "medicamentos_actuales", length = 500)
    private String medicamentosActuales;

    @Size(max = 500)
    @Column(length = 500)
    private String antecedentes;

    @Size(max = 500)
    @Column(length = 500)
    private String observaciones;

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

    /**
     * Edad calculada a partir de {@link #fechaNacimiento} cuando está
     * disponible; si no, cae al valor manual de {@link #edad}. Usada en la
     * ficha clínica para no depender de que alguien actualice "edad" a mano
     * cada año.
     */
    @Transient
    public Integer getEdadCalculada() {
        if (fechaNacimiento != null) {
            return Period.between(fechaNacimiento, LocalDate.now()).getYears();
        }
        return edad;
    }

    public Pet() {}

    public Pet(Long id, String nombre, String especie, String raza, String chip, Integer edad, Cliente cliente, List<Appointment> citas) {
        this.idMascota = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.chip = chip;
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

    public String getChip() {
        return this.chip;
    }

    public Integer getEdad() {
        return this.edad;
    }

    public String getSexo() {
        return this.sexo;
    }

    public LocalDate getFechaNacimiento() {
        return this.fechaNacimiento;
    }

    public String getColor() {
        return this.color;
    }

    public BigDecimal getPesoActual() {
        return this.pesoActual;
    }

    public String getFoto() {
        return this.foto;
    }

    public Boolean getEsterilizado() {
        return this.esterilizado;
    }

    public String getAlergias() {
        return this.alergias;
    }

    public String getEnfermedadesConocidas() {
        return this.enfermedadesConocidas;
    }

    public String getMedicamentosActuales() {
        return this.medicamentosActuales;
    }

    public String getAntecedentes() {
        return this.antecedentes;
    }

    public String getObservaciones() {
        return this.observaciones;
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

    public void setChip(String chip) {
        this.chip = chip;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setPesoActual(BigDecimal pesoActual) {
        this.pesoActual = pesoActual;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setEsterilizado(Boolean esterilizado) {
        this.esterilizado = esterilizado;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public void setEnfermedadesConocidas(String enfermedadesConocidas) {
        this.enfermedadesConocidas = enfermedadesConocidas;
    }

    public void setMedicamentosActuales(String medicamentosActuales) {
        this.medicamentosActuales = medicamentosActuales;
    }

    public void setAntecedentes(String antecedentes) {
        this.antecedentes = antecedentes;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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
