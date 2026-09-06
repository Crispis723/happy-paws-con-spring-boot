package com.Happypaws.demo.service;

import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.model.Appointment.EstadoCita;
import com.Happypaws.demo.model.AppointmentVersion;
import com.Happypaws.demo.repository.AppointmentRepository;
import com.Happypaws.demo.repository.AppointmentVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentVersionRepository appointmentVersionRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                               AppointmentVersionRepository appointmentVersionRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentVersionRepository = appointmentVersionRepository;
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

    public List<Appointment> citasPorPropietarioEmail(String email) {
        return appointmentRepository.findByMascotaOwnerEmailOrderByFechaHoraDesc(email);
    }

    /**
     * Guardar nueva cita
     */
    public Appointment guardar(Appointment appointment) {
        return guardar(appointment, "Sistema");
    }

    public Appointment guardar(Appointment appointment, String createdBy) {
        if (appointment.getEstado() == null) {
            appointment.setEstado(EstadoCita.PENDIENTE);
        }
        Appointment saved = appointmentRepository.save(appointment);
        appointmentVersionRepository.save(AppointmentVersion.from(saved, 1,
                createdBy == null || createdBy.isBlank() ? "Sistema" : createdBy));
        return saved;
    }

    /**
     * Actualizar cita
     */
    public Appointment actualizar(Long id, Appointment appointmentActualizada, String changedBy) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        guardarVersionInicialSiEsNecesario(appointment, changedBy);
        appointment.setFechaHora(appointmentActualizada.getFechaHora());
        appointment.setMotivo(appointmentActualizada.getMotivo());
        appointment.setNotas(appointmentActualizada.getNotas());
        appointment.setEstado(appointmentActualizada.getEstado());
        appointment.setPrecio(appointmentActualizada.getPrecio());
        appointment.setVeterinario(appointmentActualizada.getVeterinario());

        Appointment saved = appointmentRepository.save(appointment);
        guardarVersion(saved, changedBy);
        return saved;
    }

    public Appointment actualizar(Long id, Appointment appointmentActualizada) {
        return actualizar(id, appointmentActualizada, "Sistema");
    }

    /**
     * Cambiar estado de cita
     */
    public Appointment cambiarEstado(Long id, EstadoCita nuevoEstado, String changedBy) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        guardarVersionInicialSiEsNecesario(appointment, changedBy);
        appointment.setEstado(nuevoEstado);
        Appointment saved = appointmentRepository.save(appointment);
        guardarVersion(saved, changedBy);
        return saved;
    }

    public Appointment cambiarEstado(Long id, EstadoCita nuevoEstado) {
        return cambiarEstado(id, nuevoEstado, "Sistema");
    }

    public List<AppointmentVersion> historial(Long appointmentId) {
        return appointmentVersionRepository.findByAppointmentIdOrderByVersionNumberDesc(appointmentId);
    }

    private void guardarVersion(Appointment appointment, String changedBy) {
        int nextVersion = (int) appointmentVersionRepository.countByAppointmentId(appointment.getId()) + 1;
        appointmentVersionRepository.save(AppointmentVersion.from(appointment,
                nextVersion,
                changedBy == null || changedBy.isBlank() ? "Sistema" : changedBy));
    }

    private void guardarVersionInicialSiEsNecesario(Appointment appointment, String changedBy) {
        if (appointmentVersionRepository.countByAppointmentId(appointment.getId()) == 0) {
            appointmentVersionRepository.save(AppointmentVersion.from(appointment, 1,
                    changedBy == null || changedBy.isBlank() ? "Registro previo" : changedBy));
        }
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
