package com.Happypaws.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_versions")
@Getter
@Setter
@NoArgsConstructor
public class AppointmentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Appointment.EstadoCita estado;

    private Double precio;

    @Column(name = "veterinario_nombre")
    private String veterinarioNombre;

    @Column(name = "cliente_nombre")
    private String clienteNombre;

    @Column(name = "cliente_telefono")
    private String clienteTelefono;

    @Column(name = "mascota_nombre")
    private String mascotaNombre;

    @Column(name = "mascota_especie")
    private String mascotaEspecie;

    @Column(name = "cambiado_en", nullable = false)
    private LocalDateTime cambiadoEn;

    @Column(name = "cambiado_por", nullable = false)
    private String cambiadoPor;

    public static AppointmentVersion from(Appointment appointment, int versionNumber, String changedBy) {
        AppointmentVersion version = new AppointmentVersion();
        version.appointment = appointment;
        version.versionNumber = versionNumber;
        version.fechaHora = appointment.getFechaHora();
        version.motivo = appointment.getMotivo();
        version.notas = appointment.getNotas();
        version.estado = appointment.getEstado();
        version.precio = appointment.getPrecio();
        version.veterinarioNombre = appointment.getVeterinario() != null
                ? appointment.getVeterinario().getNombreCompleto() : null;
        version.clienteNombre = appointment.getClienteNombre();
        version.clienteTelefono = appointment.getClienteTelefono();
        version.mascotaNombre = appointment.getMascotaNombre();
        version.mascotaEspecie = appointment.getMascotaEspecie();
        version.cambiadoEn = LocalDateTime.now();
        version.cambiadoPor = changedBy;
        return version;
    }
}