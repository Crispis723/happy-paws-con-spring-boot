package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.ComprobanteTipo;
import com.Happypaws.demo.repository.ComprobanteTipoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ComprobanteTipoService {

    private final ComprobanteTipoRepository repository;

    public ComprobanteTipoService(ComprobanteTipoRepository repository) {
        this.repository = repository;
    }

    public List<ComprobanteTipo> listar() {
        return repository.findAll();
    }

    public Optional<ComprobanteTipo> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public ComprobanteTipo guardar(ComprobanteTipo comprobanteTipo) {
        return repository.save(comprobanteTipo);
    }

    public ComprobanteTipo actualizar(ComprobanteTipo comprobanteTipo) {
        if (comprobanteTipo.getId() == null || !repository.existsById(comprobanteTipo.getId())) {
            throw new ResourceNotFoundException("Tipo de comprobante no encontrado");
        }
        return repository.save(comprobanteTipo);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}