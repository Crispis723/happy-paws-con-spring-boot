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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proveedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}