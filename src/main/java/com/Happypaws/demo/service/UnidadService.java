package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Unidad;
import com.Happypaws.demo.repository.UnidadRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UnidadService {

    private final UnidadRepository repository;

    public UnidadService(UnidadRepository repository) {
        this.repository = repository;
    }

    public List<Unidad> listar() {
        return repository.findAll();
    }

    public Optional<Unidad> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Unidad guardar(Unidad unidad) {
        return repository.save(unidad);
    }

    public Unidad actualizar(Unidad unidad) {
        if (unidad.getIdUnidad() == null || !repository.existsById(unidad.getIdUnidad())) {
            throw new ResourceNotFoundException("Unidad no encontrada");
        }
        return repository.save(unidad);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}