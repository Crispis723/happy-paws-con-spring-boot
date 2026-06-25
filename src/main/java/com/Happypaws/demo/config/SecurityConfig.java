package com.Happypaws.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationSuccessHandler successHandler) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/assets/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/error", "/error/403", "/assets/**", "/css/**", "/js/**", "/bootstrap-icons-1.13.1/**", "/images/**").permitAll()
                        .requestMatchers("/usuarios/**", "/users/**", "/roles/**", "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/ventas/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA", "CLIENTE")
                        .requestMatchers("/productos/**", "/compras/**", "/proveedores/**", "/comprobante-series/**", "/comprobante-tipos/**", "/documento-tipos/**", "/unidades/**", "/afectacion-tipos/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                        .requestMatchers("/clientes/**", "/mascotas/**", "/citas/**", "/historial/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA", "CLIENTE")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                    .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(ex -> ex.accessDeniedPage("/error/403"));

        return http.build();
    }

    @Bean
    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new WebAuthenticationSuccessHandler();
    }
}