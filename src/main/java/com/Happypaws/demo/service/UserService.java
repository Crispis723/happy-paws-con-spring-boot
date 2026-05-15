package com.Happypaws.demo.service;

import com.Happypaws.demo.dto.UserDTO;
import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Role;
import com.Happypaws.demo.model.User;
import com.Happypaws.demo.repository.RoleRepository;
import com.Happypaws.demo.repository.UserRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> listar() {
        return repository.findAll();
    }

    public Optional<User> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public User guardar(UserDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setEnabled(dto.getEnabled() == null || dto.getEnabled());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setRoles(resolveRoles(dto.getRoleIds()));
        return repository.save(user);
    }

    public User actualizar(UserDTO dto) {
        User user = repository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setEnabled(dto.getEnabled() == null || dto.getEnabled());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setRoles(resolveRoles(dto.getRoleIds()));
        return repository.save(user);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    private Set<Role> resolveRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            Role role = roleRepository.findByName("CLIENTE")
                    .orElseThrow(() -> new IllegalStateException("No existe el rol CLIENTE"));
            LinkedHashSet<Role> defaultRoles = new LinkedHashSet<>();
            defaultRoles.add(role);
            return defaultRoles;
        }

        return roleIds.stream()
                .map(id -> roleRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + id)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}