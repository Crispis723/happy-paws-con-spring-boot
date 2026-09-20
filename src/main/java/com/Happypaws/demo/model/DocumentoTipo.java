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
@Table(name = "documento_tipos")
public class DocumentoTipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento_tipo")
    private Long idDocumentoTipo;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true)
    private String codigo;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false)
    private String descripcion;
    
    public DocumentoTipo() {}

    public DocumentoTipo(Long id, String codigo, String descripcion) {
        this.idDocumentoTipo = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
    }


    public Long getIdDocumentoTipo() {
        return this.idDocumentoTipo;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setId(Long id) {
        this.idDocumentoTipo = id;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public void setIdDocumentoTipo(Long id) { this.idDocumentoTipo = id; }

    /** Compatibilidad de API: el identificador persistido es idDocumentoTipo. */
    public Long getId() { return idDocumentoTipo; }
}