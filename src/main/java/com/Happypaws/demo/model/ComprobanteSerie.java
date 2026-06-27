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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comprobante_series")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComprobanteSerie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}