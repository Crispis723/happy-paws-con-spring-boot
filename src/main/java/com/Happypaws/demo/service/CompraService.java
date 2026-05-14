package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Compra;
import com.Happypaws.demo.repository.CompraRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CompraService {

    private final CompraRepository repository;

    public CompraService(CompraRepository repository) {
        this.repository = repository;
    }

    public List<Compra> listar() {
        return repository.findAll();
    }

    public Optional<Compra> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Compra guardar(Compra compra) {
        return repository.save(compra);
    }

    public Compra actualizar(Compra compra) {
        if (compra.getId() == null || !repository.existsById(compra.getId())) {
            throw new ResourceNotFoundException("Compra no encontrada");
        }
        return repository.save(compra);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}