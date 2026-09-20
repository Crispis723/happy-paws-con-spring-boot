package com.Happypaws.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Un episodio de hospitalización de una mascota: desde el ingreso hasta
 * el alta (o fallecimiento). Los signos vitales que se ven "en grande"
 * en la ficha (temperatura, peso) son los del último
 * {@link ControlHospitalizacion} registrado, cayendo a los valores de
 * ingreso si todavía no se ha registrado ningún control (ver
 * {@link #getUltimoControl()} en HospitalizacionService, que es quien
 * arma esa vista para no acoplar la entidad a esa lógica de presentación).
 */
@Entity
@Table(name = "hospitalizaciones")
public class Hospitalizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hospitalizacion")
    private Long idHospitalizacion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false)
    private Pet mascota;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String jaula;

    @NotNull
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_alta")
    private LocalDateTime fechaAlta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario")
    private User veterinarioResponsable;

    @NotBlank
    @Size(max = 500)
    @Column(name = "motivo_ingreso", nullable = false, length = 500)
    private String motivoIngreso;

    @Size(max = 500)
    @Column(name = "diagnostico_ingreso", length = 500)
    private String diagnosticoIngreso;

    @Size(max = 1000)
    @Column(name = "tratamiento_general", length = 1000)
    private String tratamientoGeneral;

    @DecimalMin(value = "0.0")
    @Column(name = "peso_ingreso", precision = 6, scale = 2)
    private BigDecimal pesoIngreso;

    @DecimalMin(value = "0.0")
    @Column(name = "temperatura_ingreso", precision = 4, scale = 1)
    private BigDecimal temperaturaIngreso;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoHospitalizacion estado = EstadoHospitalizacion.EN_OBSERVACION;

    @Size(max = 1000)
    @Column(name = "observaciones_alta", length = 1000)
    private String observacionesAlta;

    @OneToMany(mappedBy = "hospitalizacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("fechaHora DESC")
    private List<ControlHospitalizacion> controles = new ArrayList<>();

    @Transient
    public boolean isActiva() {
        return fechaAlta == null && (estado == null || !estado.esFinal());
    }

    public Hospitalizacion() {
    }

    public Long getId() {
        return idHospitalizacion;
    }

    public void setId(Long id) {
        this.idHospitalizacion = id;
    }

    public Pet getMascota() {
        return mascota;
    }

    public void setMascota(Pet mascota) {
        this.mascota = mascota;
    }

    public String getJaula() {
        return jaula;
    }

    public void setJaula(String jaula) {
        this.jaula = jaula;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public LocalDateTime getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDateTime fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public User getVeterinarioResponsable() {
        return veterinarioResponsable;
    }

    public void setVeterinarioResponsable(User veterinarioResponsable) {
        this.veterinarioResponsable = veterinarioResponsable;
    }

    public String getMotivoIngreso() {
        return motivoIngreso;
    }

    public void setMotivoIngreso(String motivoIngreso) {
        this.motivoIngreso = motivoIngreso;
    }

    public String getDiagnosticoIngreso() {
        return diagnosticoIngreso;
    }

    public void setDiagnosticoIngreso(String diagnosticoIngreso) {
        this.diagnosticoIngreso = diagnosticoIngreso;
    }

    public String getTratamientoGeneral() {
        return tratamientoGeneral;
    }

    public void setTratamientoGeneral(String tratamientoGeneral) {
        this.tratamientoGeneral = tratamientoGeneral;
    }

    public BigDecimal getPesoIngreso() {
        return pesoIngreso;
    }

    public void setPesoIngreso(BigDecimal pesoIngreso) {
        this.pesoIngreso = pesoIngreso;
    }

    public BigDecimal getTemperaturaIngreso() {
        return temperaturaIngreso;
    }

    public void setTemperaturaIngreso(BigDecimal temperaturaIngreso) {
        this.temperaturaIngreso = temperaturaIngreso;
    }

    public EstadoHospitalizacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHospitalizacion estado) {
        this.estado = estado;
    }

    public String getObservacionesAlta() {
        return observacionesAlta;
    }

    public void setObservacionesAlta(String observacionesAlta) {
        this.observacionesAlta = observacionesAlta;
    }

    public List<ControlHospitalizacion> getControles() {
        return controles;
    }

    public void setControles(List<ControlHospitalizacion> controles) {
        this.controles = controles;
    }
}
