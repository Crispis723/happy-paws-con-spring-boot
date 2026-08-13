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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // ============================================================
    // PASSWORD ENCODER
    // ============================================================

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ============================================================
    // AUTHENTICATION MANAGER
    // ============================================================

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    // ============================================================
    // SECURITY FILTER CHAIN
    // ============================================================

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationSuccessHandler successHandler) throws Exception {

        http

            // ====================================================
            // CSRF
            // ====================================================

            .csrf(csrf -> csrf
                .csrfTokenRepository(
                    CookieCsrfTokenRepository.withHttpOnlyFalse()
                )
                .ignoringRequestMatchers("/assets/**")
            )

            // ====================================================
            // AUTORIZACIÓN
            // ====================================================

            .authorizeHttpRequests(auth -> auth

                // ------------------------------------------------
                // PÁGINAS Y RECURSOS PÚBLICOS
                // ------------------------------------------------

                .requestMatchers(
                    "/",
                    "/login",
                    "/register",
                    "/forgot-password",
                    "/reset-password",
                    "/error",
                    "/error/403",

                    // Recursos estáticos
                    "/assets/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/bootstrap-icons-1.13.1/**",
                    "/favicon.ico"
                ).permitAll()


                // =================================================
                // DASHBOARD CLIENTE
                // =================================================

                .requestMatchers(
                    "/dashboard/cliente/**"
                ).hasRole("CLIENTE")


                // =================================================
                // DASHBOARD COLABORADORES
                // =================================================

                .requestMatchers(
                    "/dashboard/colaboradores/**",
                    "/dashboard/staff/**"
                ).hasAnyRole(
                    "ADMIN",
                    "VETERINARIO",
                    "RECEPCIONISTA"
                )


                // =================================================
                // ADMINISTRACIÓN DE USUARIOS
                // SOLO ADMIN
                // =================================================

                .requestMatchers(
                    "/usuarios/**",
                    "/users/**",
                    "/roles/**",
                    "/admin/**"
                ).hasRole("ADMIN")


                // =================================================
                // CONFIGURACIÓN DEL SISTEMA
                // SOLO ADMIN
                // =================================================

                .requestMatchers(
                    "/comprobante-series/**",
                    "/comprobante-tipos/**",
                    "/documento-tipos/**",
                    "/unidades/**",
                    "/afectacion-tipos/**"
                ).hasRole("ADMIN")


                // =================================================
                // CLIENTES
                //
                // ADMIN:
                //     Todo
                //
                // VETERINARIO:
                //     Consultar clientes
                //
                // RECEPCIONISTA:
                //     Gestionar clientes
                //
                // CLIENTE:
                //     Solo sus propios datos
                // =================================================

                .requestMatchers(
                    "/clientes/**"
                ).hasAnyRole(
                    "ADMIN",
                    "VETERINARIO",
                    "RECEPCIONISTA",
                    "CLIENTE"
                )


                // =================================================
                // MASCOTAS
                //
                // ADMIN:
                //     Todo
                //
                // VETERINARIO:
                //     Gestionar mascotas de pacientes
                //
                // RECEPCIONISTA:
                //     Registrar/gestionar mascotas
                //
                // CLIENTE:
                //     SOLO sus mascotas
                // =================================================

                .requestMatchers(
                    "/mascotas/**",
                    "/pets/**"
                ).hasAnyRole(
                    "ADMIN",
                    "VETERINARIO",
                    "RECEPCIONISTA",
                    "CLIENTE"
                )


                // =================================================
                // CITAS
                //
                // ADMIN:
                //     Todas
                //
                // VETERINARIO:
                //     Sus citas
                //
                // RECEPCIONISTA:
                //     Gestionar agenda
                //
                // CLIENTE:
                //     Sus citas
                // =================================================

                .requestMatchers(
                    "/citas/**",
                    "/appointments/**"
                ).hasAnyRole(
                    "ADMIN",
                    "VETERINARIO",
                    "RECEPCIONISTA",
                    "CLIENTE"
                )


                // =================================================
                // HISTORIAL CLÍNICO
                //
                // ADMIN:
                //     Todo
                //
                // VETERINARIO:
                //     Crear / editar / consultar
                //
                // RECEPCIONISTA:
                //     NO
                //
                // CLIENTE:
                //     Consultar SOLO el historial de sus mascotas
                // =================================================

                .requestMatchers(
                    "/historial/**",
                    "/historial-mascotas/**"
                ).hasAnyRole(
                    "ADMIN",
                    "VETERINARIO",
                    "CLIENTE"
                )


                // =================================================
                // PRODUCTOS
                //
                // ADMIN:
                //     Todo
                //
                // VETERINARIO:
                //     Consultar productos
                //
                // RECEPCIONISTA:
                //     Gestionar productos
                //
                // CLIENTE:
                //     NO acceso administrativo
                // =================================================

                .requestMatchers(
                    "/productos/**",
                    "/products/**"
                ).hasAnyRole(
                    "ADMIN",
                    "VETERINARIO",
                    "RECEPCIONISTA"
                )


                // =================================================
                // COMPRAS
                //
                // ADMIN:
                //     Todo
                //
                // RECEPCIONISTA:
                //     Gestionar compras
                //
                // VETERINARIO:
                //     NO
                //
                // CLIENTE:
                //     NO
                // =================================================

                .requestMatchers(
                    "/compras/**",
                    "/compras/**"
                ).hasAnyRole(
                    "ADMIN",
                    "RECEPCIONISTA"
                )


                // =================================================
                // PROVEEDORES
                //
                // ADMIN:
                //     Todo
                //
                // RECEPCIONISTA:
                //     Gestionar
                //
                // VETERINARIO:
                //     NO
                //
                // CLIENTE:
                //     NO
                // =================================================

                .requestMatchers(
                    "/proveedores/**",
                    "/suppliers/**"
                ).hasAnyRole(
                    "ADMIN",
                    "RECEPCIONISTA"
                )


                // =================================================
                // VENTAS
                //
                // ADMIN:
                //     Todo
                //
                // VETERINARIO:
                //     Registrar ventas
                //
                // RECEPCIONISTA:
                //     Registrar ventas
                //
                // CLIENTE:
                //     Puede consultar/comprar según el controlador
                //
                // IMPORTANTE:
                // VentaController debe controlar qué operaciones
                // puede hacer CLIENTE.
                // =================================================

                .requestMatchers(
                    "/ventas/**",
                    "/sales/**"
                ).hasAnyRole(
                    "ADMIN",
                    "VETERINARIO",
                    "RECEPCIONISTA",
                    "CLIENTE"
                )


                // =================================================
                // REPORTES
                //
                // ADMIN:
                //     Todos
                //
                // VETERINARIO:
                //     Reportes clínicos
                //
                // RECEPCIONISTA:
                //     NO reportes administrativos sensibles
                //
                // CLIENTE:
                //     NO
                // =================================================

                .requestMatchers(
                    "/reportes/**",
                    "/reports/**"
                ).hasAnyRole(
                    "ADMIN",
                    "VETERINARIO"
                )


                // =================================================
                // CUALQUIER OTRA RUTA
                // SOLO USUARIOS AUTENTICADOS
                // =================================================

                .anyRequest().authenticated()
            )


            // ====================================================
            // LOGIN
            // ====================================================

            .formLogin(form -> form

                .loginPage("/login")

                .loginProcessingUrl("/login")

                // Tu HTML usa:
                // name="email"
                .usernameParameter("email")

                // Tu HTML usa:
                // name="password"
                .passwordParameter("password")

                /*
                 * IMPORTANTE:
                 *
                 * WebAuthenticationSuccessHandler recibe:
                 *
                 * loginRole=cliente
                 *
                 * o
                 *
                 * loginRole=colaborador
                 *
                 * desde tu login.html.
                 */
                .successHandler(successHandler)

                .failureUrl("/login?error=true")

                .permitAll()
            )


            // ====================================================
            // LOGOUT
            // ====================================================

            .logout(logout -> logout

                .logoutUrl("/logout")

                .logoutSuccessUrl("/login?logout=true")

                .invalidateHttpSession(true)

                .clearAuthentication(true)

                .deleteCookies("JSESSIONID")
            )


            // ====================================================
            // ACCESO DENEGADO
            // ====================================================

            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            );

        return http.build();
    }

    // ============================================================
    // SUCCESS HANDLER
    // ============================================================

    @Bean
    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new WebAuthenticationSuccessHandler();
    }
}
