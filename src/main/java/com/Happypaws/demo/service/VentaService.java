package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Venta;
import com.Happypaws.demo.repository.VentaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class VentaService {

    private final VentaRepository repository;

    public VentaService(VentaRepository repository) {
        this.repository = repository;
    }

    public List<Venta> listar() {
        return repository.findAll();
    }

    public List<Venta> listarPorClienteId(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    public Optional<Venta> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Venta guardar(Venta venta) {
        return repository.save(venta);
    }

    public Venta actualizar(Venta venta) {
        if (venta.getId() == null || !repository.existsById(venta.getId())) {
            throw new ResourceNotFoundException("Venta no encontrada");
        }
        return repository.save(venta);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}