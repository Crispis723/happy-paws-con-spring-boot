package com.Happypaws.demo.service;

import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.model.Appointment.EstadoCita;
import com.Happypaws.demo.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Obtener todas las citas
     */
    public List<Appointment> listarTodas() {
        return appointmentRepository.findAll();
    }

    /**
     * Buscar cita por ID
     */
    public Optional<Appointment> buscarPorId(Long id) {
        return appointmentRepository.findById(id);
    }

    /**
     * Obtener citas de un veterinario
     */
    public List<Appointment> citasPorVeterinario(Long veterinarioId) {
        return appointmentRepository.findByVeterinarioId(veterinarioId);
    }

    /**
     * Obtener citas de un veterinario en estado específico
     */
    public List<Appointment> citasPorVeterinarioYEstado(Long veterinarioId, EstadoCita estado) {
        return appointmentRepository.findByVeterinarioIdAndEstado(veterinarioId, estado);
    }

    /**
     * Obtener citas de una mascota
     */
    public List<Appointment> citasPorMascota(Long mascotaId) {
        return appointmentRepository.findByMascotaId(mascotaId);
    }

    /**
     * Obtener citas en un rango de fechas
     */
    public List<Appointment> citasEnRango(LocalDateTime inicio, LocalDateTime fin) {
        return appointmentRepository.findByFechaHoraBetween(inicio, fin);
    }

    /**
     * Obtener citas de un veterinario en un rango de fechas
     */
    public List<Appointment> citasVeterinarioEnRango(Long veterinarioId, LocalDateTime inicio, LocalDateTime fin) {
        return appointmentRepository.findByVeterinarioIdAndFechaHoraBetween(veterinarioId, inicio, fin);
    }

    /**
     * Obtener citas recientes de un veterinario
     */
    public List<Appointment> citasRecientes(Long veterinarioId) {
        return appointmentRepository.findByVeterinarioRecientes(veterinarioId);
    }

    /**
     * Obtener citas de hoy para un veterinario
     */
    public List<Appointment> citasDeHoy(Long veterinarioId) {
        return appointmentRepository.findTodayAppointments(veterinarioId);
    }

    /**
     * Obtener citas de un propietario
     */
    public List<Appointment> citasPorPropietario(Long propietarioId) {
        return appointmentRepository.findByOwnerId(propietarioId);
    }

    /**
     * Guardar nueva cita
     */
    public Appointment guardar(Appointment appointment) {
        if (appointment.getEstado() == null) {
            appointment.setEstado(EstadoCita.PENDIENTE);
        }
        return appointmentRepository.save(appointment);
    }

    /**
     * Actualizar cita
     */
    public Appointment actualizar(Long id, Appointment appointmentActualizada) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        appointment.setFechaHora(appointmentActualizada.getFechaHora());
        appointment.setMotivo(appointmentActualizada.getMotivo());
        appointment.setNotas(appointmentActualizada.getNotas());
        appointment.setEstado(appointmentActualizada.getEstado());
        appointment.setPrecio(appointmentActualizada.getPrecio());
        appointment.setVeterinario(appointmentActualizada.getVeterinario());

        return appointmentRepository.save(appointment);
    }

    /**
     * Cambiar estado de cita
     */
    public Appointment cambiarEstado(Long id, EstadoCita nuevoEstado) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        appointment.setEstado(nuevoEstado);
        return appointmentRepository.save(appointment);
    }

    /**
     * Eliminar cita
     */
    public void eliminar(Long id) {
        appointmentRepository.deleteById(id);
    }

    /**
     * Contar citas de veterinario en estado
     */
    public long contarPorVeterinarioYEstado(Long veterinarioId, EstadoCita estado) {
        return appointmentRepository.countByVeterinarioAndEstado(veterinarioId, estado);
    }

    /**
     * Obtener citas pendientes por estado
     */
    public List<Appointment> citasPorEstado(EstadoCita estado) {
        return appointmentRepository.findByEstadoOrdenado(estado);
    }
}
