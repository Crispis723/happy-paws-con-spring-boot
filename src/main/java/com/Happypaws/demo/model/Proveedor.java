package com.Happypaws.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "proveedores")
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long idProveedor;

    @NotBlank
    @Size(max = 20)
    @Column(name = "documento_tipo_codigo", nullable = false)
    private String documentoTipoCodigo;

    @NotBlank
    @Size(max = 20)
    @Column(name = "numero_documento", nullable = false, unique = true)
    private String numeroDocumento;

    @NotBlank
    @Size(max = 150)
    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    @Size(max = 255)
    private String direccion;

    @Size(max = 30)
    private String telefono;

    @Email
    @Size(max = 150)
    private String email;

    public Proveedor() {
    }

    public Proveedor(Long id, String documentoTipoCodigo, String numeroDocumento, String razonSocial, String direccion, String telefono, String email) {
        this.idProveedor = id;
        this.documentoTipoCodigo = documentoTipoCodigo;
        this.numeroDocumento = numeroDocumento;
        this.razonSocial = razonSocial;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
    }

    public Long getIdProveedor() {
        return this.idProveedor;
    }

    public String getDocumentoTipoCodigo() {
        return this.documentoTipoCodigo;
    }

    public String getNumeroDocumento() {
        return this.numeroDocumento;
    }

    public String getRazonSocial() {
        return this.razonSocial;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public String getEmail() {
        return this.email;
    }

    public void setId(Long id) {
        this.idProveedor = id;
    }

    public void setDocumentoTipoCodigo(String documentoTipoCodigo) {
        this.documentoTipoCodigo = documentoTipoCodigo;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setIdProveedor(Long id) { this.idProveedor = id; }

    /** Compatibilidad de API: el identificador persistido es idProveedor. */
    public Long getId() { return idProveedor; }
}