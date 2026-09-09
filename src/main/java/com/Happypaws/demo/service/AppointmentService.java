package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.repository.AppointmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

    private final AppointmentRepository repository;

    public AppointmentService(AppointmentRepository repository) {
        this.repository = repository;
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

    @Transactional
    public Appointment actualizar(Appointment appointment) {
        if (appointment.getIdCita() == null || !repository.existsById(appointment.getIdCita())) {
            throw new ResourceNotFoundException("Cita no encontrada");
        }
        return repository.save(appointment);
    }

    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
