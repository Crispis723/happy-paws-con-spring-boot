package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Vacuna;
import com.Happypaws.demo.repository.VacunaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class VacunaService {

    private final VacunaRepository repository;

    public VacunaService(VacunaRepository repository) {
        this.repository = repository;
    }

    public List<Vacuna> listarPorMascotaId(Long mascotaId) {
        return repository.findByMascotaIdMascotaOrderByFechaDesc(mascotaId);
    }

    public Optional<Vacuna> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Vacuna guardar(Vacuna vacuna) {
        return repository.save(vacuna);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Registro de vacuna no encontrado");
        }
        repository.deleteById(id);
    }
}
