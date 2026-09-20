package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Desparasitacion;
import com.Happypaws.demo.repository.DesparasitacionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DesparasitacionService {

    private final DesparasitacionRepository repository;

    public DesparasitacionService(DesparasitacionRepository repository) {
        this.repository = repository;
    }

    public List<Desparasitacion> listarPorMascotaId(Long mascotaId) {
        return repository.findByMascotaIdMascotaOrderByFechaDesc(mascotaId);
    }

    public Optional<Desparasitacion> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Desparasitacion guardar(Desparasitacion desparasitacion) {
        return repository.save(desparasitacion);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Registro de desparasitación no encontrado");
        }
        repository.deleteById(id);
    }
}
