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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Un control puntual durante una hospitalización: la "ronda" que el
 * personal clínico hace varias veces al día para registrar signos
 * vitales y evolución. Varias mascotas hospitalizadas pueden acumular
 * muchos de estos por día, por eso es una entidad propia y no un campo
 * más de Hospitalizacion.
 */
@Entity
@Table(name = "controles_hospitalizacion")
public class ControlHospitalizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_control")
    private Long idControl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hospitalizacion", nullable = false)
    private Hospitalizacion hospitalizacion;

    @NotNull
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

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

    @Size(max = 255)
    @Column(name = "estado_general", length = 255)
    private String estadoGeneral;

    @Size(max = 500)
    @Column(name = "medicacion_administrada", length = 500)
    private String medicacionAdministrada;

    @Size(max = 1000)
    @Column(length = 1000)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_registro")
    private User registradoPor;

    public ControlHospitalizacion() {
    }

    public Long getId() {
        return idControl;
    }

    public void setId(Long id) {
        this.idControl = id;
    }

    public Hospitalizacion getHospitalizacion() {
        return hospitalizacion;
    }

    public void setHospitalizacion(Hospitalizacion hospitalizacion) {
        this.hospitalizacion = hospitalizacion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public BigDecimal getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(BigDecimal temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public Integer getFrecuenciaRespiratoria() {
        return frecuenciaRespiratoria;
    }

    public void setFrecuenciaRespiratoria(Integer frecuenciaRespiratoria) {
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
    }

    public String getEstadoGeneral() {
        return estadoGeneral;
    }

    public void setEstadoGeneral(String estadoGeneral) {
        this.estadoGeneral = estadoGeneral;
    }

    public String getMedicacionAdministrada() {
        return medicacionAdministrada;
    }

    public void setMedicacionAdministrada(String medicacionAdministrada) {
        this.medicacionAdministrada = medicacionAdministrada;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public User getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(User registradoPor) {
        this.registradoPor = registradoPor;
    }
}
