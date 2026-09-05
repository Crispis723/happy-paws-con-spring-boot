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

    @Size(max = 500)
    @Column(length = 500)
    private String diagnostico;

    @Size(max = 500)
    @Column(length = 500)
    private String tratamiento;

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

    public HistorialMascota(Long id, Pet mascota, Appointment cita, String titulo, String detalle, String diagnostico, String tratamiento, LocalDate fechaRegistro, LocalDate fechaExpiracion, String archivoOriginal, String archivoGuardado, String archivoTipo, Long archivoTamanio) {
        this.idHistorial = id;
        this.mascota = mascota;
        this.cita = cita;
        this.titulo = titulo;
        this.detalle = detalle;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.fechaRegistro = fechaRegistro;
        this.fechaExpiracion = fechaExpiracion;
        this.archivoOriginal = archivoOriginal;
        this.archivoGuardado = archivoGuardado;
        this.archivoTipo = archivoTipo;
        this.archivoTamanio = archivoTamanio;
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

    public String getDiagnostico() {
        return this.diagnostico;
    }

    public String getTratamiento() {
        return this.tratamiento;
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

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
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
