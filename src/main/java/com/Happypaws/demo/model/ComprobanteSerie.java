package com.Happypaws.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "comprobante_series")
public class ComprobanteSerie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_serie")
    private Long idSerie;

    @NotBlank
    @Size(max = 20)
    @Column(name = "comprobante_tipo_codigo", nullable = false)
    private String comprobanteTipoCodigo;

    @NotBlank
    @Size(max = 10)
    @Column(nullable = false)
    private String serie;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer correlativo;

    public ComprobanteSerie() {
    }

    public ComprobanteSerie(Long id, String comprobanteTipoCodigo, String serie, Integer correlativo) {
        this.idSerie = id;
        this.comprobanteTipoCodigo = comprobanteTipoCodigo;
        this.serie = serie;
        this.correlativo = correlativo;
    }

    public Long getIdSerie() {
        return this.idSerie;
    }

    public String getComprobanteTipoCodigo() {
        return this.comprobanteTipoCodigo;
    }

    public String getSerie() {
        return this.serie;
    }

    public Integer getCorrelativo() {
        return this.correlativo;
    }

    public void setId(Long id) {
        this.idSerie = id;
    }

    public void setComprobanteTipoCodigo(String comprobanteTipoCodigo) {
        this.comprobanteTipoCodigo = comprobanteTipoCodigo;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public void setCorrelativo(Integer correlativo) {
        this.correlativo = correlativo;
    }


    public void setIdSerie(Long id) { this.idSerie = id; }

    /** Compatibilidad de API: el identificador persistido es idSerie. */
    public Long getId() { return idSerie; }
}