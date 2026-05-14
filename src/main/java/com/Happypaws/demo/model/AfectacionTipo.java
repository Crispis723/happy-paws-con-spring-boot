package com.Happypaws.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "afectacion_tipos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AfectacionTipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true)
    private String codigo;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String descripcion;

    @NotBlank
    @Size(max = 1)
    @Column(nullable = false, length = 1)
    private String letra;

    @NotNull
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;
}