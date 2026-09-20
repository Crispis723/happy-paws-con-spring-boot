package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.AfectacionTipo;
import com.Happypaws.demo.repository.AfectacionTipoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AfectacionTipoService {

    private final AfectacionTipoRepository repository;

    public AfectacionTipoService(AfectacionTipoRepository repository) {
        this.repository = repository;
    }

    public List<AfectacionTipo> listar() {
        return repository.findAll();
    }

    public Optional<AfectacionTipo> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public AfectacionTipo guardar(AfectacionTipo afectacionTipo) {
        return repository.save(afectacionTipo);
    }

    public AfectacionTipo actualizar(AfectacionTipo afectacionTipo) {
        if (afectacionTipo.getIdAfectacionTipo() == null || !repository.existsById(afectacionTipo.getIdAfectacionTipo())) {
            throw new ResourceNotFoundException("Tipo de afectación no encontrado");
        }
        return repository.save(afectacionTipo);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}