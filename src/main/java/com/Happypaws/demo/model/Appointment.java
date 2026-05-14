package com.Happypaws.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha es requerida")
    @Column(nullable = false, name = "fecha_hora")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El motivo es requerido")
    @Size(min = 10, max = 500, message = "El motivo debe tener entre 10 y 500 caracteres")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCita estado = EstadoCita.PENDIENTE;

    @Column(name = "precio", nullable = true)
    private Double precio;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // Relaciones
    @ManyToOne(optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Pet mascota;

    @ManyToOne
    @JoinColumn(name = "veterinario_id")
    private Veterinario veterinario;

    @Column(name = "cliente_nombre")
    private String clienteNombre;

    @Column(name = "cliente_telefono")
    private String clienteTelefono;

    @Column(name = "mascota_nombre")
    private String mascotaNombre;

    @Column(name = "mascota_especie")
    private String mascotaEspecie;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public enum EstadoCita {
        PENDIENTE("Pendiente"),
        CONFIRMADA("Confirmada"),
        COMPLETADA("Completada"),
        CANCELADA("Cancelada"),
        NO_PRESENTADA("No Presentada");

        private final String displayName;

        EstadoCita(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public String getEstadoDisplay() {
        return this.estado != null ? this.estado.getDisplayName() : "N/A";
    }
}

