package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.repository.AppointmentRepository;
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
        return repository.findByClienteId(clienteId);
    }

    public List<Appointment> listarPorMascotaId(Long mascotaId) {
        return repository.findByMascotaIdOrderByFechaDesc(mascotaId);
    }

    public Optional<Appointment> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Appointment guardar(Appointment appointment) {
        return repository.save(appointment);
    }

    public Appointment actualizar(Appointment appointment) {
        if (appointment.getId() == null || !repository.existsById(appointment.getId())) {
            throw new ResourceNotFoundException("Cita no encontrada");
        }
        return repository.save(appointment);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}