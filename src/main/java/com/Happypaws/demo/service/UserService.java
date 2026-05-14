package com.Happypaws.demo.service;

import com.Happypaws.demo.model.User;
import com.Happypaws.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Obtener todos los usuarios
     */
    public List<User> listarTodos() {
        return userRepository.findAll();
    }

    /**
     * Buscar usuario por ID
     */
    public Optional<User> buscarPorId(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Buscar usuario por email
     */
    public Optional<User> buscarPorEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Guardar nuevo usuario
     */
    public User guardar(User user) {
        // Encriptar contraseña si es nuevo
        if (user.getId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    /**
     * Actualizar usuario
     */
    public User actualizar(Long id, User userActualizado) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setName(userActualizado.getName());
        user.setEmail(userActualizado.getEmail());

        // Si la contraseña fue cambiada, encriptarla
        if (userActualizado.getPassword() != null && !userActualizado.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userActualizado.getPassword()));
        }

        return userRepository.save(user);
    }

    /**
     * Eliminar usuario
     */
    public void eliminar(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Validar credenciales
     */
    public boolean validarCredenciales(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return false;
        }
        return passwordEncoder.matches(password, user.get().getPassword());
    }

    /**
     * Verificar si existe email
     */
    public boolean existeEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Obtener usuario por email
     */
    public User obtenerPorEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}
