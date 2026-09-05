package com.Happypaws.demo.service;

import com.Happypaws.demo.model.User;
import com.Happypaws.demo.repository.UserRepository;
import java.security.Principal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User buscarActual(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public User actualizarActual(Principal principal, String name, String email, String password) {
        User user = buscarActual(principal);
        user.setName(name);
        user.setEmail(email);
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        return userRepository.save(user);
    }
}