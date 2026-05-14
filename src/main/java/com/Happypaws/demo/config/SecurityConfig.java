package com.Happypaws.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers("/", "/login", "/register", "/assets/**", "/css/**", "/js/**", "/bootstrap-icons-1.13.1/**").permitAll()
                        
                        // API - Citas (acceso por roles)
                        .requestMatchers("/api/citas/veterinario/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                        .requestMatchers("/api/citas/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                        
                        // Rutas de veterinarios
                        .requestMatchers("/veterinarios", "/veterinarios/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                        
                        // Rutas de admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")
                        
                        // Rutas protegidas
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/403")
                );

        return http.build();
    }
}