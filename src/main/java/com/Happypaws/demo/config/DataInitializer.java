package com.Happypaws.demo.config;

import com.Happypaws.demo.model.AfectacionTipo;
import com.Happypaws.demo.model.DocumentoTipo;
import com.Happypaws.demo.model.Role;
import com.Happypaws.demo.model.SystemSetting;
import com.Happypaws.demo.model.Unidad;
import com.Happypaws.demo.model.User;
import com.Happypaws.demo.repository.AfectacionTipoRepository;
import com.Happypaws.demo.repository.DocumentoTipoRepository;
import com.Happypaws.demo.repository.RoleRepository;
import com.Happypaws.demo.repository.SystemSettingRepository;
import com.Happypaws.demo.repository.UnidadRepository;
import com.Happypaws.demo.repository.UserRepository;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner seedData(RoleRepository roleRepository,
                               UserRepository userRepository,
                               UnidadRepository unidadRepository,
                               AfectacionTipoRepository afectacionTipoRepository,
                               DocumentoTipoRepository documentoTipoRepository,
                               SystemSettingRepository systemSettingRepository,
                               PasswordEncoder passwordEncoder,
                               @Value("${ADMIN_INITIAL_EMAIL:admin@happypaws.com}") String adminInitialEmail,
                               @Value("${ADMIN_INITIAL_PASSWORD:}") String adminInitialPassword,
                               @Value("${SEED_DEMO_USERS:false}") boolean seedDemoUsers,
                               @Value("${DEMO_USERS_PASSWORD:}") String demoUsersPassword) {
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
                afectacionTipoRepository.save(new AfectacionTipo(null, "10", "Gravado", "Gravado - Operacion onerosa", "G", new BigDecimal("18.00")));
                afectacionTipoRepository.save(new AfectacionTipo(null, "20", "Exonerado", "Exonerado - Operacion no onerosa", "E", new BigDecimal("0.00")));
            }

            if (documentoTipoRepository.count() == 0) {
                documentoTipoRepository.save(new DocumentoTipo(null, "CC", "Cedula de ciudadania"));
                documentoTipoRepository.save(new DocumentoTipo(null, "NIT", "Numero de identificacion tributaria"));
            }

            if (systemSettingRepository.findBySettingKey("cita.precio").isEmpty()) {
                systemSettingRepository.save(new SystemSetting(null, "cita.precio", "50.00"));
            }

            if (userRepository.count() == 0) {
                createInitialUsers(roleRepository, userRepository, passwordEncoder, adminInitialEmail, adminInitialPassword, seedDemoUsers, demoUsersPassword);
            }
        };
    }

    private void createInitialUsers(RoleRepository roleRepository,
                                    UserRepository userRepository,
                                    PasswordEncoder passwordEncoder,
                                    String adminInitialEmail,
                                    String adminInitialPassword,
                                    boolean seedDemoUsers,
                                    String demoUsersPassword) {
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

        if (adminInitialPassword != null && !adminInitialPassword.isBlank()) {
            userRepository.save(createUser(passwordEncoder, "Administrador", adminInitialEmail, adminInitialPassword, adminRole));
            log.info("Usuario administrador inicial creado: {}", adminInitialEmail);
            return;
        }

        if (seedDemoUsers && demoUsersPassword != null && !demoUsersPassword.isBlank()) {
            Role recepcionistaRole = roleRepository.findByName("RECEPCIONISTA").orElseThrow();
            Role veterinarioRole = roleRepository.findByName("VETERINARIO").orElseThrow();
            Role clienteRole = roleRepository.findByName("CLIENTE").orElseThrow();

            userRepository.save(createUser(passwordEncoder, "Administrador", "admin@happypaws.com", demoUsersPassword, adminRole));
            userRepository.save(createUser(passwordEncoder, "Recepcion", "recepcion@happypaws.com", demoUsersPassword, recepcionistaRole));
            userRepository.save(createUser(passwordEncoder, "Veterinario", "vet@happypaws.com", demoUsersPassword, veterinarioRole));
            userRepository.save(createUser(passwordEncoder, "Cliente Demo", "cliente@happypaws.com", demoUsersPassword, clienteRole));
            log.warn("Usuarios demo creados porque SEED_DEMO_USERS=true. No uses esto en produccion.");
            return;
        }

        log.warn("No se crearon usuarios iniciales. Define ADMIN_INITIAL_PASSWORD para crear el primer administrador.");
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
