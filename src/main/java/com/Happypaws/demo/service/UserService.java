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

    public List<User> listarVeterinarios() {
        return repository.findAll().stream()
                .filter(user -> user.getRoles() != null && user.getRoles().stream().anyMatch(role -> "VETERINARIO".equals(role.getName())))
                .toList();
    }

    public Optional<User> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public User guardar(UserDTO dto) {
        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setEnabled(dto.getEnabled() == null || dto.getEnabled());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(resolveRoles(dto.getRoleIds()));
        return repository.save(user);
    }

    public User actualizar(UserDTO dto, String currentUserEmail) {
        User user = repository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        repository.findByEmail(dto.getEmail())
                .filter(existing -> !existing.getIdUsuario().equals(dto.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Ya existe un usuario con ese email");
                });

        boolean isSelf = user.getEmail().equals(currentUserEmail);
        boolean losingAdmin = isSelf
                && user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()))
                && resolveRoles(dto.getRoleIds()).stream().noneMatch(role -> "ADMIN".equals(role.getName()));
        if (isSelf && Boolean.FALSE.equals(dto.getEnabled())) {
            throw new IllegalStateException("No puedes desactivar tu propia cuenta");
        }
        if (losingAdmin) {
            throw new IllegalStateException("No puedes quitarte a ti mismo el rol ADMIN");
        }

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

    public void asignarRolAEmail(String email, Long roleId) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        // Un usuario solo puede tener un rol. Asignar uno nuevo reemplaza el anterior.
        LinkedHashSet<Role> singleRole = new LinkedHashSet<>();
        singleRole.add(role);
        user.setRoles(singleRole);
        repository.save(user);
    }

    private Set<Role> resolveRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            Role role = roleRepository.findByName("CLIENTE")
                    .orElseThrow(() -> new IllegalStateException("No existe el rol CLIENTE"));
            LinkedHashSet<Role> defaultRoles = new LinkedHashSet<>();
            defaultRoles.add(role);
            return defaultRoles;
        }

        if (roleIds.size() != 1) {
            throw new IllegalArgumentException("Cada usuario debe tener exactamente un rol");
        }

        Role role = roleRepository.findById(roleIds.get(0))
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + roleIds.get(0)));
        LinkedHashSet<Role> singleRole = new LinkedHashSet<>();
        singleRole.add(role);
        return singleRole;
    }
}