package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Role;
import com.Happypaws.demo.repository.RoleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository repository;

    public RoleService(RoleRepository repository) {
        this.repository = repository;
    }

    public List<Role> listar() {
        return repository.findAll();
    }

    public Optional<Role> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Role guardar(Role role) {
        return repository.save(role);
    }

    public Role actualizar(Role role) {
        if (role.getId() == null || !repository.existsById(role.getId())) {
            throw new ResourceNotFoundException("Rol no encontrado");
        }
        return repository.save(role);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}