package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.model.EstadoCita;
import com.Happypaws.demo.repository.AppointmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentReminderService reminderService;

    public AppointmentService(AppointmentRepository repository, AppointmentReminderService reminderService) {
        this.repository = repository;
        this.reminderService = reminderService;
    }

    @Transactional(readOnly = true)
    public List<Appointment> listar() {
        return repository.findAllWithRelations();
    }

    /**
     * Cuenta citas sin ejecutar el JOIN FETCH de {@link #listar()}, que
     * trae cliente/mascota junto con cada cita y sería un desperdicio si
     * solo se necesita el total para un contador del dashboard.
     */
    @Transactional(readOnly = true)
    public long contar() {
        return repository.count();
    }

    @Transactional(readOnly = true)
    public List<Appointment> listarPorClienteId(Long clienteId) {
        return repository.findByClienteIdClienteWithRelations(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Appointment> listarPorMascotaId(Long mascotaId) {
        return repository.findByMascotaIdMascotaOrderByFechaHoraDesc(mascotaId);
    }

    @Transactional(readOnly = true)
    public Optional<Appointment> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Appointment> buscarPorVeterinarioYFechaHora(Long veterinarioId, LocalDateTime fechaHora) {
        return repository.findByVeterinarioIdUsuarioAndFechaHora(veterinarioId, fechaHora);
    }

    @Transactional
    public Appointment guardar(Appointment appointment) {
        return repository.save(appointment);
    }

    /**
     * Actualiza una cita existente. El formulario de edición reconstruye
     * un Appointment desde cero (ver AppointmentController.guardar) y
     * nunca toca el campo "estado", así que si aquí simplemente
     * guardáramos ese objeto se pisaría el estado real de la cita
     * (CONFIRMADA, ATENDIDA, etc.) con el valor por defecto AGENDADA en
     * cada edición. Por eso, si el objeto entrante no trae estado, se
     * conserva el que ya tenía la cita en la base de datos.
     */
    @Transactional
    public Appointment actualizar(Appointment appointment) {
        Appointment existente = repository.findById(appointment.getIdCita())
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        if (appointment.getEstado() == null) {
            appointment.setEstado(existente.getEstado());
        }

        return repository.save(appointment);
    }

    /**
     * Cambia el estado de una cita. No permite mover una cita que ya
     * está en un estado final (ATENDIDA, CANCELADA, NO_ASISTIO) porque
     * esos representan cierres de la cita: si se cometió un error real,
     * lo correcto es crear una cita nueva, no reabrir una ya cerrada.
     */
    @Transactional
    public Appointment cambiarEstado(Long id, EstadoCita nuevoEstado) {
        Appointment cita = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        if (cita.getEstado() != null && cita.getEstado().esFinal() && cita.getEstado() != nuevoEstado) {
            throw new IllegalArgumentException(
                    "Esta cita ya está " + cita.getEstado().getEtiqueta().toLowerCase()
                            + " y no se puede cambiar de estado; agenda una cita nueva si corresponde."
            );
        }

        cita.setEstado(nuevoEstado);
        Appointment guardada = repository.save(cita);

        if (nuevoEstado == EstadoCita.CANCELADA) {
            reminderService.enviarNotificacionCancelacion(guardada, "Cancelada desde el panel de citas");
        }

        return guardada;
    }

    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}

