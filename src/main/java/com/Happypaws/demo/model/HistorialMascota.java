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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "historial_mascotas")
public class HistorialMascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long idHistorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false)
    private Pet mascota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita")
    private Appointment cita;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank
    @Size(max = 1500)
    @Column(nullable = false, length = 1500)
    private String detalle;

    // =========================================================
    // SIGNOS VITALES / EXAMEN CLÍNICO
    // =========================================================

    @DecimalMin(value = "0.0")
    @Column(precision = 6, scale = 2)
    private BigDecimal peso;

    @DecimalMin(value = "0.0")
    @Column(precision = 4, scale = 1)
    private BigDecimal temperatura;

    @Min(0)
    @Column(name = "frecuencia_cardiaca")
    private Integer frecuenciaCardiaca;

    @Min(0)
    @Column(name = "frecuencia_respiratoria")
    private Integer frecuenciaRespiratoria;

    /** Escala de condición corporal estándar (1 = muy delgado, 9 = obeso). */
    @Min(1)
    @Max(9)
    @Column(name = "condicion_corporal")
    private Integer condicionCorporal;

    @Size(max = 255)
    @Column(name = "motivo_consulta", length = 255)
    private String motivoConsulta;

    @Size(max = 1000)
    @Column(length = 1000)
    private String anamnesis;

    @Size(max = 1000)
    @Column(name = "examen_fisico", length = 1000)
    private String examenFisico;

    @Size(max = 500)
    @Column(length = 500)
    private String diagnostico;

    @Size(max = 500)
    @Column(length = 500)
    private String tratamiento;

    // =========================================================
    // MEDICACIÓN INDICADA
    // =========================================================

    @Size(max = 255)
    @Column(length = 255)
    private String medicamento;

    @Size(max = 100)
    @Column(length = 100)
    private String dosis;

    @Size(max = 100)
    @Column(name = "frecuencia_medicamento", length = 100)
    private String frecuenciaMedicamento;

    @Size(max = 100)
    @Column(name = "duracion_tratamiento", length = 100)
    private String duracionTratamiento;

    @Size(max = 1000)
    @Column(length = 1000)
    private String observaciones;

    @Size(max = 1000)
    @Column(length = 1000)
    private String recomendaciones;

    @Column(name = "proximo_control")
    private LocalDate proximoControl;

    // =========================================================
    // METADATOS DEL REGISTRO
    // =========================================================

    @NotNull
    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDate fechaExpiracion;

    @Column(name = "archivo_original", length = 255)
    private String archivoOriginal;

    @Column(name = "archivo_guardado", length = 255)
    private String archivoGuardado;

    @Column(name = "archivo_tipo", length = 120)
    private String archivoTipo;

    @Column(name = "archivo_tamanio")
    private Long archivoTamanio;

    @Transient
    public boolean isVigente() {
        return fechaExpiracion != null && !fechaExpiracion.isBefore(LocalDate.now());
    }

    public HistorialMascota() {
    }

    public Long getIdHistorial() {
        return this.idHistorial;
    }

    public Pet getMascota() {
        return this.mascota;
    }

    public Appointment getCita() {
        return this.cita;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public String getDetalle() {
        return this.detalle;
    }

    public BigDecimal getPeso() {
        return this.peso;
    }

    public BigDecimal getTemperatura() {
        return this.temperatura;
    }

    public Integer getFrecuenciaCardiaca() {
        return this.frecuenciaCardiaca;
    }

    public Integer getFrecuenciaRespiratoria() {
        return this.frecuenciaRespiratoria;
    }

    public Integer getCondicionCorporal() {
        return this.condicionCorporal;
    }

    public String getMotivoConsulta() {
        return this.motivoConsulta;
    }

    public String getAnamnesis() {
        return this.anamnesis;
    }

    public String getExamenFisico() {
        return this.examenFisico;
    }

    public String getDiagnostico() {
        return this.diagnostico;
    }

    public String getTratamiento() {
        return this.tratamiento;
    }

    public String getMedicamento() {
        return this.medicamento;
    }

    public String getDosis() {
        return this.dosis;
    }

    public String getFrecuenciaMedicamento() {
        return this.frecuenciaMedicamento;
    }

    public String getDuracionTratamiento() {
        return this.duracionTratamiento;
    }

    public String getObservaciones() {
        return this.observaciones;
    }

    public String getRecomendaciones() {
        return this.recomendaciones;
    }

    public LocalDate getProximoControl() {
        return this.proximoControl;
    }

    public LocalDate getFechaRegistro() {
        return this.fechaRegistro;
    }

    public LocalDate getFechaExpiracion() {
        return this.fechaExpiracion;
    }

    public String getArchivoOriginal() {
        return this.archivoOriginal;
    }

    public String getArchivoGuardado() {
        return this.archivoGuardado;
    }

    public String getArchivoTipo() {
        return this.archivoTipo;
    }

    public Long getArchivoTamanio() {
        return this.archivoTamanio;
    }

    public void setId(Long id) {
        this.idHistorial = id;
    }

    public void setMascota(Pet mascota) {
        this.mascota = mascota;
    }

    public void setCita(Appointment cita) {
        this.cita = cita;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public void setTemperatura(BigDecimal temperatura) {
        this.temperatura = temperatura;
    }

    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public void setFrecuenciaRespiratoria(Integer frecuenciaRespiratoria) {
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
    }

    public void setCondicionCorporal(Integer condicionCorporal) {
        this.condicionCorporal = condicionCorporal;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public void setAnamnesis(String anamnesis) {
        this.anamnesis = anamnesis;
    }

    public void setExamenFisico(String examenFisico) {
        this.examenFisico = examenFisico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public void setFrecuenciaMedicamento(String frecuenciaMedicamento) {
        this.frecuenciaMedicamento = frecuenciaMedicamento;
    }

    public void setDuracionTratamiento(String duracionTratamiento) {
        this.duracionTratamiento = duracionTratamiento;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void setRecomendaciones(String recomendaciones) {
        this.recomendaciones = recomendaciones;
    }

    public void setProximoControl(LocalDate proximoControl) {
        this.proximoControl = proximoControl;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void setFechaExpiracion(LocalDate fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public void setArchivoOriginal(String archivoOriginal) {
        this.archivoOriginal = archivoOriginal;
    }

    public void setArchivoGuardado(String archivoGuardado) {
        this.archivoGuardado = archivoGuardado;
    }

    public void setArchivoTipo(String archivoTipo) {
        this.archivoTipo = archivoTipo;
    }

    public void setArchivoTamanio(Long archivoTamanio) {
        this.archivoTamanio = archivoTamanio;
    }


    public void setIdHistorial(Long id) { this.idHistorial = id; }

    /** Compatibilidad de API: el identificador persistido es idHistorial. */
    public Long getId() { return idHistorial; }
}
