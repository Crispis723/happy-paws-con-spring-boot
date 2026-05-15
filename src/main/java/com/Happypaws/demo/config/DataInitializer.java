package com.Happypaws.demo.config;

import com.Happypaws.demo.model.AfectacionTipo;
import com.Happypaws.demo.model.DocumentoTipo;
import com.Happypaws.demo.model.Role;
import com.Happypaws.demo.model.Unidad;
import com.Happypaws.demo.model.User;
import com.Happypaws.demo.repository.AfectacionTipoRepository;
import com.Happypaws.demo.repository.DocumentoTipoRepository;
import com.Happypaws.demo.repository.RoleRepository;
import com.Happypaws.demo.repository.UnidadRepository;
import com.Happypaws.demo.repository.UserRepository;
import com.Happypaws.demo.repository.SystemSettingRepository;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(RoleRepository roleRepository,
                               UserRepository userRepository,
                               UnidadRepository unidadRepository,
                               AfectacionTipoRepository afectacionTipoRepository,
                               DocumentoTipoRepository documentoTipoRepository,
                               SystemSettingRepository systemSettingRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            List<String> roles = List.of("ADMIN", "VETERINARIO", "RECEPCIONISTA", "CLIENTE");
            for (String roleName : roles) {
                roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(new Role(null, roleName)));
            }

            if (unidadRepository.count() == 0) {
                unidadRepository.save(new Unidad(null, "NIU", "Unidad"));
                unidadRepository.save(new Unidad(null, "PZA", "Pieza"));
            }

            if (afectacionTipoRepository.count() == 0) {
                afectacionTipoRepository.save(new AfectacionTipo(null, "10", "Gravado", "Gravado - Operación onerosa", "G", new BigDecimal("18.00")));
                afectacionTipoRepository.save(new AfectacionTipo(null, "20", "Exonerado", "Exonerado - Operación no onerosa", "E", new BigDecimal("0.00")));
            }

            if (documentoTipoRepository.count() == 0) {
                documentoTipoRepository.save(new DocumentoTipo(null, "CC", "Cédula de ciudadanía"));
                documentoTipoRepository.save(new DocumentoTipo(null, "NIT", "Número de identificación tributaria"));
            }

            if (systemSettingRepository.findBySettingKey("cita.precio").isEmpty()) {
                systemSettingRepository.save(new com.Happypaws.demo.model.SystemSetting(null, "cita.precio", "50.00"));
            }

            if (userRepository.count() == 0) {
                Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
                Role recepcionistaRole = roleRepository.findByName("RECEPCIONISTA").orElseThrow();
                Role veterinarioRole = roleRepository.findByName("VETERINARIO").orElseThrow();
                Role clienteRole = roleRepository.findByName("CLIENTE").orElseThrow();

                userRepository.save(createUser(passwordEncoder, "Administrador", "admin@happypaws.com", "admin123", adminRole));
                userRepository.save(createUser(passwordEncoder, "Recepción", "recepcion@happypaws.com", "recep1234", recepcionistaRole));
                userRepository.save(createUser(passwordEncoder, "Veterinario", "vet@happypaws.com", "vet12345", veterinarioRole));
                userRepository.save(createUser(passwordEncoder, "Cliente Demo", "cliente@happypaws.com", "cliente123", clienteRole));
            }
        };
    }

    private User createUser(PasswordEncoder passwordEncoder, String name, String email, String password, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setRoles(new LinkedHashSet<>(List.of(role)));
        return user;
    }
}