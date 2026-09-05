package com.Happypaws.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

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

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Pet> mascotas = new ArrayList<>();
    
    public Cliente() {}

    public Cliente(Long id, String documentoTipoCodigo, String numeroDocumento, String razonSocial, String direccion, String telefono, String email, List<Pet> mascotas) {
        this.idCliente = id;
        this.documentoTipoCodigo = documentoTipoCodigo;
        this.numeroDocumento = numeroDocumento;
        this.razonSocial = razonSocial;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.mascotas = mascotas != null ? mascotas : new ArrayList<>();
    }


    public Long getIdCliente() {
        return this.idCliente;
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

    public List<Pet> getMascotas() {
        return this.mascotas;
    }

    public void setId(Long id) {
        this.idCliente = id;
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

    public void setMascotas(List<Pet> mascotas) {
        this.mascotas = mascotas;
    }


    public void setIdCliente(Long id) { this.idCliente = id; }

    /** Compatibilidad de API: el identificador persistido es idCliente. */
    public Long getId() { return idCliente; }
}