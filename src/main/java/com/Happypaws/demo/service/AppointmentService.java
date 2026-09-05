package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.repository.AppointmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    private final AppointmentRepository repository;

    public AppointmentService(AppointmentRepository repository) {
        this.repository = repository;
    }

    public List<Appointment> listar() {
        return repository.findAll();
    }

    public List<Appointment> listarPorClienteId(Long clienteId) {
        return repository.findByClienteIdCliente(clienteId);
    }

    public List<Appointment> listarPorMascotaId(Long mascotaId) {
        return repository.findByMascotaIdMascotaOrderByFechaHoraDesc(mascotaId);
    }

    public Optional<Appointment> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Optional<Appointment> buscarPorVeterinarioYFechaHora(Long veterinarioId, LocalDateTime fechaHora) {
        return repository.findByVeterinarioIdUsuarioAndFechaHora(veterinarioId, fechaHora);
    }

    public Appointment guardar(Appointment appointment) {
        return repository.save(appointment);
    }

    public Appointment actualizar(Appointment appointment) {
        if (appointment.getIdCita() == null || !repository.existsById(appointment.getIdCita())) {
            throw new ResourceNotFoundException("Cita no encontrada");
        }
        return repository.save(appointment);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
