package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.HistorialMascota;
import com.Happypaws.demo.repository.HistorialMascotaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HistorialMascotaService {

    private final HistorialMascotaRepository repository;

    public HistorialMascotaService(HistorialMascotaRepository repository) {
        this.repository = repository;
    }

    public List<HistorialMascota> listarPorMascotaId(Long mascotaId) {
        return repository.findByMascotaIdMascotaOrderByFechaRegistroDesc(mascotaId);
    }
    public List<HistorialMascota> listarVencimientosPorCliente(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        return repository.findByMascotaClienteIdClienteAndFechaExpiracionBetweenOrderByFechaExpiracionAsc(clienteId, fechaInicio, fechaFin);
    }

    public Optional<HistorialMascota> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public HistorialMascota guardar(HistorialMascota historial) {
        aplicarVigenciaAnual(historial);
        return repository.save(historial);
    }

    public HistorialMascota actualizar(HistorialMascota historial) {
        if (historial.getIdHistorial() == null || !repository.existsById(historial.getIdHistorial())) {
            throw new ResourceNotFoundException("Registro de historial no encontrado");
        }
        aplicarVigenciaAnual(historial);
        return repository.save(historial);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    private void aplicarVigenciaAnual(HistorialMascota historial) {
        if (historial.getFechaRegistro() == null) {
            historial.setFechaRegistro(LocalDate.now());
        }
        historial.setFechaExpiracion(historial.getFechaRegistro().plusYears(1));
    }
}
