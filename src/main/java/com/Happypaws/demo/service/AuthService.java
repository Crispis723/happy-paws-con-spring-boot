package com.Happypaws.demo.service;

import com.Happypaws.demo.dto.RegisterRequest;
import com.Happypaws.demo.model.Role;
import com.Happypaws.demo.model.User;
import com.Happypaws.demo.repository.RoleRepository;
import com.Happypaws.demo.repository.UserRepository;
import java.util.LinkedHashSet;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClienteService clienteService;

    public AuthService(UserRepository repository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ClienteService clienteService) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.clienteService = clienteService;
    }

    public User registrar(RegisterRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);

        // Seguridad: el registro publico SIEMPRE asigna CLIENTE.
        // Nunca confiar en un rol enviado por el formulario/cliente.
        Role role = roleRepository.findByName("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("No existe el rol CLIENTE"));
        user.setRoles(new LinkedHashSet<>(java.util.List.of(role)));

        User savedUser = repository.save(user);

        clienteService.resolverOCrearClienteAutenticado(savedUser.getEmail(), savedUser.getName());

        return savedUser;
    }
}