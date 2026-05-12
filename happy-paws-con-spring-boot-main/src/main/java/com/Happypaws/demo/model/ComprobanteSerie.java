package com.Happypaws.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "comprobante_tipo_codigo", nullable = false)
    private String comprobanteTipoCodigo;

    @Column(nullable = false)
    private String serie;

    @Column(nullable = false)
    private Integer correlativo;
}