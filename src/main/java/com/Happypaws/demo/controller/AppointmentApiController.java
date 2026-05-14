package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/citas")
public class AppointmentApiController {

    private final AppointmentService appointmentService;

    public AppointmentApiController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * Obtener todas las citas
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<List<Appointment>> listarTodas() {
        List<Appointment> citas = appointmentService.listarTodas();
        return ResponseEntity.ok(citas);
    }

    /**
     * Obtener cita por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<Appointment> obtenerPorId(@PathVariable Long id) {
        Optional<Appointment> cita = appointmentService.buscarPorId(id);
        return cita.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Obtener citas de un veterinario (IMPORTANTE PARA VETERINARIOS)
     */
    @GetMapping("/veterinario/{veterinarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<List<Appointment>> citasPorVeterinario(@PathVariable Long veterinarioId) {
        List<Appointment> citas = appointmentService.citasPorVeterinario(veterinarioId);
        return ResponseEntity.ok(citas);
    }

    /**
     * Obtener citas de hoy para un veterinario
     */
    @GetMapping("/veterinario/{veterinarioId}/hoy")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<List<Appointment>> citasDeHoy(@PathVariable Long veterinarioId) {
        List<Appointment> citas = appointmentService.citasDeHoy(veterinarioId);
        return ResponseEntity.ok(citas);
    }

    /**
     * Obtener citas recientes
     */
    @GetMapping("/veterinario/{veterinarioId}/recientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<List<Appointment>> citasRecientes(@PathVariable Long veterinarioId) {
        List<Appointment> citas = appointmentService.citasRecientes(veterinarioId);
        return ResponseEntity.ok(citas);
    }

    /**
     * Obtener citas de una mascota
     */
    @GetMapping("/mascota/{mascotaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'CLIENTE')")
    public ResponseEntity<List<Appointment>> citasPorMascota(@PathVariable Long mascotaId) {
        List<Appointment> citas = appointmentService.citasPorMascota(mascotaId);
        return ResponseEntity.ok(citas);
    }

    /**
     * Obtener citas de un propietario (para clientes)
     */
    @GetMapping("/propietario/{propietarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<List<Appointment>> citasPorPropietario(@PathVariable Long propietarioId) {
        List<Appointment> citas = appointmentService.citasPorPropietario(propietarioId);
        return ResponseEntity.ok(citas);
    }

    /**
     * Cambiar estado de cita
     */
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<Appointment> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado) {
        try {
            Appointment.EstadoCita estado = Appointment.EstadoCita.valueOf(nuevoEstado.toUpperCase());
            Appointment cita = appointmentService.cambiarEstado(id, estado);
            return ResponseEntity.ok(cita);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Crear nueva cita
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<Appointment> crearCita(@RequestBody Appointment appointment) {
        Appointment nueva = appointmentService.guardar(appointment);
        return ResponseEntity.ok(nueva);
    }

    /**
     * Actualizar cita
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<Appointment> actualizarCita(
            @PathVariable Long id,
            @RequestBody Appointment appointment) {
        try {
            Appointment actualizada = appointmentService.actualizar(id, appointment);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar cita
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> eliminarCita(@PathVariable Long id) {
        appointmentService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Contar citas de veterinario
     */
    @GetMapping("/veterinario/{veterinarioId}/contar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<Long> contarCitasVeterinario(@PathVariable Long veterinarioId) {
        long total = appointmentService.citasPorVeterinario(veterinarioId).size();
        return ResponseEntity.ok(total);
    }
}
