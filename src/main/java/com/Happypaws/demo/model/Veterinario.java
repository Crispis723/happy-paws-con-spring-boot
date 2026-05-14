package com.Happypaws.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "veterinarios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Veterinario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es requerido")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El email es requerido")
    @Email(message = "Debe ser un email válido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "El teléfono es requerido")
    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos")
    @Column(nullable = false)
    private String telefono;

    @NotBlank(message = "La cédula/DNI es requerida")
    @Column(nullable = false, unique = true)
    private String cedula;

    @NotBlank(message = "La especialidad es requerida")
    @Column(nullable = false)
    private String especialidad;

    @Lob
    private String biografia;

    @NotNull(message = "La licencia profesional es requerida")
    @Column(nullable = false)
    private String licencia;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "es_activo")
    private Boolean esActivo = true;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "horario_disponible")
    private String horarioDisponible;

    // Relación con User (si existe)
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User usuario;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public String getNombreCompleto() {
        return this.nombre;
    }

    public String getEspecialidadFormatted() {
        return this.especialidad != null ? this.especialidad.toUpperCase() : "N/A";
    }
}
