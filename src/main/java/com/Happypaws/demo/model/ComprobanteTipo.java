package com.Happypaws.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "comprobante_tipos")
public class ComprobanteTipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante_tipo")
    private Long idComprobanteTipo;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true)
    private String codigo;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false)
    private String descripcion;

    public ComprobanteTipo() {
    }

    public ComprobanteTipo(Long id, String codigo, String descripcion) {
        this.idComprobanteTipo = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public Long getIdComprobanteTipo() {
        return this.idComprobanteTipo;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setId(Long id) {
        this.idComprobanteTipo = id;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public void setIdComprobanteTipo(Long id) { this.idComprobanteTipo = id; }

    /** Compatibilidad de API: el identificador persistido es idComprobanteTipo. */
    public Long getId() { return idComprobanteTipo; }
}