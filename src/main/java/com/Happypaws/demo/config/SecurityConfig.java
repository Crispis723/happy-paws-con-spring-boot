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

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationSuccessHandler successHandler) throws Exception {

        http

            .csrf(csrf -> csrf
                .csrfTokenRepository(
                    CookieCsrfTokenRepository.withHttpOnlyFalse()
                )
                .ignoringRequestMatchers("/assets/**")
            )

            .headers(headers -> headers
                .cacheControl(cache -> {})
            )

            .authorizeHttpRequests(auth -> auth

                // =================================================
                // PÚBLICO
                // =================================================

                .requestMatchers(
                    "/",
                    "/login",
                    "/register",
                    "/forgot-password",
                    "/reset-password",
                    "/error",
                    "/error/403",
                    "/assets/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/bootstrap-icons-1.13.1/**",
                    "/favicon.ico",
                    "/uploads/**",
                    "/actuator/health"
                ).permitAll()


                // =================================================
                // ADMIN
                // =================================================

                .requestMatchers(
                    "/admin",
                    "/admin/**"
                ).hasRole("ADMIN")


                // =================================================
                // DASHBOARD CLIENTE
                // =================================================

                .requestMatchers(
                    "/dashboard/cliente",
                    "/dashboard/cliente/**"
                ).hasAnyAuthority(
                    "DASHBOARD_CLIENTE_VER",
                    "ROLE_ADMIN"
                )


                // =================================================
                // DASHBOARD
                // =================================================

                .requestMatchers(
                    "/dashboard",
                    "/dashboard/colaboradores",
                    "/dashboard/colaboradores/**",
                    "/dashboard/staff",
                    "/dashboard/staff/**"
                ).hasAnyAuthority(
                    "DASHBOARD_VER",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/dashboard/exportar",
                    "/dashboard/exportar/**"
                ).hasAnyAuthority(
                    "REPORTES_EXPORTAR",
                    "DASHBOARD_VER",
                    "ROLE_ADMIN"
                )


                // =================================================
                // CLIENTES
                // =================================================

                .requestMatchers(
                    "/clientes",
                    "/clientes/"
                ).hasAnyAuthority(
                    "CLIENTES_VER",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/clientes/nuevo",
                    "/clientes/crear"
                ).hasAnyAuthority(
                    "CLIENTES_CREAR",
                    "ROLE_ADMIN"
                )

                // NOTA: el controlador real usa "/clientes/edit/{id}" y
                // "/clientes/delete/{id}" (inglés). Las rutas "/editar/**" y
                // "/eliminar/**" de abajo solo existen como redirecciones de
                // compatibilidad (CompatibilityRoutesController) y nunca las
                // usa la UI actual, así que hay que proteger también las
                // rutas reales o cualquier CLIENTES_VER (incluido el rol
                // CLIENTE) puede editar o borrar cualquier cliente.

                .requestMatchers(
                    "/clientes/editar/**",
                    "/clientes/edit/**"
                ).hasAnyAuthority(
                    "CLIENTES_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/clientes/eliminar/**",
                    "/clientes/delete/**"
                ).hasAnyAuthority(
                    "CLIENTES_ELIMINAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/clientes/guardar"
                ).hasAnyAuthority(
                    "CLIENTES_CREAR",
                    "CLIENTES_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/clientes/**"
                ).hasAnyAuthority(
                    "CLIENTES_VER",
                    "ROLE_ADMIN"
                )


                // =================================================
                // MASCOTAS
                // =================================================

                .requestMatchers(
                    "/mascotas",
                    "/mascotas/",
                    "/pets",
                    "/pets/"
                ).hasAnyAuthority(
                    "MASCOTAS_VER",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/mascotas/nuevo",
                    "/mascotas/crear",
                    "/pets/nuevo",
                    "/pets/crear"
                ).hasAnyAuthority(
                    "MASCOTAS_CREAR",
                    "ROLE_ADMIN"
                )

                // NOTA: el controlador real usa "/mascotas/edit/{id}" y
                // "/mascotas/guardar" (inglés/sin acento). Sin este matcher,
                // ambas caían en el catch-all de MASCOTAS_VER más abajo, y
                // cualquier CLIENTE (que solo debería poder crear/editar SUS
                // propias mascotas mediante la validación en el controlador)
                // podía llegar a /mascotas/guardar con el MASCOTAS_CREAR que
                // sí tiene. Esto por sí solo no basta: PetController también
                // valida la propiedad de la mascota antes de guardar.

                .requestMatchers(
                    "/mascotas/editar/**",
                    "/mascotas/edit/**",
                    "/pets/editar/**",
                    "/pets/edit/**"
                ).hasAnyAuthority(
                    "MASCOTAS_EDITAR",
                    "MASCOTAS_CREAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/mascotas/eliminar/**",
                    "/mascotas/delete/**",
                    "/pets/eliminar/**",
                    "/pets/delete/**"
                ).hasAnyAuthority(
                    "MASCOTAS_ELIMINAR",
                    "MASCOTAS_CREAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/mascotas/guardar",
                    "/pets/guardar"
                ).hasAnyAuthority(
                    "MASCOTAS_CREAR",
                    "MASCOTAS_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/mascotas/**",
                    "/pets/**"
                ).hasAnyAuthority(
                    "MASCOTAS_VER",
                    "ROLE_ADMIN"
                )


                // =================================================
                // CITAS
                // =================================================

                .requestMatchers(
                    "/citas",
                    "/citas/"
                ).hasAnyAuthority(
                    "CITAS_VER",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/citas/create",
                    "/citas/nueva",
                    "/citas/crear"
                ).hasAnyAuthority(
                    "CITAS_CREAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/citas/guardar"
                ).hasAnyAuthority(
                    "CITAS_CREAR",
                    "CITAS_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/citas/edit/**",
                    "/citas/editar/**"
                ).hasAnyAuthority(
                    "CITAS_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/citas/show/**"
                ).hasAnyAuthority(
                    "CITAS_VER",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/citas/delete/**",
                    "/citas/eliminar/**"
                ).hasAnyAuthority(
                    "CITAS_ELIMINAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/citas/**"
                ).hasAnyAuthority(
                    "CITAS_VER",
                    "ROLE_ADMIN"
                )


                // =================================================
                // HISTORIAL
                // =================================================

                .requestMatchers(
                    "/historial",
                    "/historial/",
                    "/historial-mascotas",
                    "/historial-mascotas/"
                ).hasAnyAuthority(
                    "HISTORIAL_VER",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/historial/nuevo",
                    "/historial/crear",
                    "/historial-mascotas/nuevo",
                    "/historial-mascotas/crear"
                ).hasAnyAuthority(
                    "HISTORIAL_CREAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/historial/editar/**",
                    "/historial-mascotas/editar/**"
                ).hasAnyAuthority(
                    "HISTORIAL_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/historial/eliminar/**",
                    "/historial-mascotas/eliminar/**"
                ).hasAnyAuthority(
                    "HISTORIAL_ELIMINAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/historial/**",
                    "/historial-mascotas/**"
                ).hasAnyAuthority(
                    "HISTORIAL_VER",
                    "ROLE_ADMIN"
                )


                // =================================================
                // PRODUCTOS
                // =================================================

                .requestMatchers(
                    org.springframework.http.HttpMethod.GET,
                    "/productos/create",
                    "/products/create"
                ).hasAnyAuthority(
                    "PRODUCTOS_CREAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    org.springframework.http.HttpMethod.POST,
                    "/productos",
                    "/products"
                ).hasAnyAuthority(
                    "PRODUCTOS_CREAR",
                    "PRODUCTOS_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/productos/edit/**",
                    "/products/edit/**"
                ).hasAnyAuthority(
                    "PRODUCTOS_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/productos/delete/**",
                    "/products/delete/**"
                ).hasAnyAuthority(
                    "PRODUCTOS_ELIMINAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/productos",
                    "/productos/",
                    "/productos/**",
                    "/products",
                    "/products/",
                    "/products/**"
                ).hasAnyAuthority(
                    "PRODUCTOS_VER",
                    "ROLE_ADMIN"
                )


                // =================================================
                // COMPRAS
                // =================================================

                .requestMatchers(
                    "/compras",
                    "/compras/"
                ).hasAnyAuthority(
                    "COMPRAS_VER",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/compras/nueva",
                    "/compras/crear",
                    "/compras/create"
                ).hasAnyAuthority(
                    "COMPRAS_CREAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/compras/editar/**",
                    "/compras/edit/**"
                ).hasAnyAuthority(
                    "COMPRAS_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/compras/eliminar/**",
                    "/compras/delete/**"
                ).hasAnyAuthority(
                    "COMPRAS_ELIMINAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/compras/guardar"
                ).hasAnyAuthority(
                    "COMPRAS_CREAR",
                    "COMPRAS_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/compras/**"
                ).hasAnyAuthority(
                    "COMPRAS_VER",
                    "ROLE_ADMIN"
                )


                // =================================================
                // PROVEEDORES
                // =================================================

                .requestMatchers(
                    "/proveedores",
                    "/proveedores/",
                    "/suppliers",
                    "/suppliers/"
                ).hasAnyAuthority(
                    "PROVEEDORES_VER",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/proveedores/nuevo",
                    "/proveedores/crear"
                ).hasAnyAuthority(
                    "PROVEEDORES_CREAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/proveedores/editar/**",
                    "/proveedores/edit/**"
                ).hasAnyAuthority(
                    "PROVEEDORES_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/proveedores/eliminar/**",
                    "/proveedores/delete/**"
                ).hasAnyAuthority(
                    "PROVEEDORES_ELIMINAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/proveedores/guardar"
                ).hasAnyAuthority(
                    "PROVEEDORES_CREAR",
                    "PROVEEDORES_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/proveedores/**",
                    "/suppliers/**"
                ).hasAnyAuthority(
                    "PROVEEDORES_VER",
                    "ROLE_ADMIN"
                )


                // =================================================
                // VENTAS
                // =================================================

                .requestMatchers(
                    "/ventas",
                    "/ventas/",
                    "/sales",
                    "/sales/"
                ).hasAnyAuthority(
                    "VENTAS_VER",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/ventas/nueva",
                    "/ventas/crear",
                    "/ventas/create"
                ).hasAnyAuthority(
                    "VENTAS_CREAR",
                    "ROLE_ADMIN"
                )

                // NOTA: el controlador real usa "/ventas/edit/{id}",
                // "/ventas/delete/{id}" y "/ventas/guardar" (inglés). Sin
                // estos matchers, caían en el catch-all de VENTAS_VER y
                // cualquier usuario con solo permiso de VER una venta podía
                // editar o eliminar la venta de OTRO cliente cambiando el id
                // en la URL. VentaController ya valida el dueño de la venta
                // (validarPropietario) para "edit"/"delete"/"show", pero NO
                // lo hacía en "guardar" al actualizar (ver fix en el
                // controlador); esta regla es la primera línea de defensa.

                .requestMatchers(
                    "/ventas/editar/**",
                    "/ventas/edit/**"
                ).hasAnyAuthority(
                    "VENTAS_EDITAR",
                    "VENTAS_CREAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/ventas/eliminar/**",
                    "/ventas/delete/**"
                ).hasAnyAuthority(
                    "VENTAS_ELIMINAR",
                    "VENTAS_CREAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/ventas/guardar"
                ).hasAnyAuthority(
                    "VENTAS_CREAR",
                    "VENTAS_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/ventas/**",
                    "/sales/**"
                ).hasAnyAuthority(
                    "VENTAS_VER",
                    "ROLE_ADMIN"
                )


                // =================================================
                // REPORTES
                // =================================================

                .requestMatchers(
                    "/reportes",
                    "/reportes/",
                    "/reports",
                    "/reports/"
                ).hasAnyAuthority(
                    "REPORTES_VER",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/reportes/**",
                    "/reports/**"
                ).hasAnyAuthority(
                    "REPORTES_VER",
                    "ROLE_ADMIN"
                )


                // =================================================
                // USUARIOS
                // =================================================

                .requestMatchers(
                    "/usuarios",
                    "/usuarios/"
                ).hasAnyAuthority(
                    "USUARIOS_VER",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/usuarios/nuevo",
                    "/usuarios/crear"
                ).hasAnyAuthority(
                    "USUARIOS_CREAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/usuarios/editar/**",
                    "/usuarios/edit/**"
                ).hasAnyAuthority(
                    "USUARIOS_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/usuarios/eliminar/**",
                    "/usuarios/delete/**"
                ).hasAnyAuthority(
                    "USUARIOS_ELIMINAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/usuarios/guardar"
                ).hasAnyAuthority(
                    "USUARIOS_CREAR",
                    "USUARIOS_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/usuarios/**",
                    "/users/**"
                ).hasAnyAuthority(
                    "USUARIOS_VER",
                    "ROLE_ADMIN"
                )


                // =================================================
                // ROLES
                // =================================================

                .requestMatchers(
                    "/roles",
                    "/roles/"
                ).hasAnyAuthority(
                    "ROLES_VER",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/roles/nuevo",
                    "/roles/crear"
                ).hasAnyAuthority(
                    "ROLES_CREAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/roles/edit/**",
                    "/roles/editar/**"
                ).hasAnyAuthority(
                    "ROLES_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/roles/delete/**",
                    "/roles/eliminar/**"
                ).hasAnyAuthority(
                    "ROLES_ELIMINAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/roles/guardar"
                ).hasAnyAuthority(
                    "ROLES_CREAR",
                    "ROLES_EDITAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/roles/asignar"
                ).hasAnyAuthority(
                    "ROLES_ASIGNAR",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/roles/**"
                ).hasAnyAuthority(
                    "ROLES_VER",
                    "ROLE_ADMIN"
                )


                // =================================================
                // ENDPOINTS DE PRUEBA (solo perfil "dev")
                // =================================================
                // Segunda capa de defensa: aunque TestValidationController
                // ya solo se registra con @Profile("dev"), si ese perfil se
                // activara por error en un entorno compartido, esta regla
                // impide que un usuario que no sea ROLE_ADMIN pueda usarlo
                // (por ejemplo, para enviar correos arbitrarios).

                .requestMatchers(
                    "/api/test/**"
                ).hasRole("ADMIN")


                // =================================================
                // CONFIGURACIÓN
                // =================================================

                .requestMatchers(
                    "/comprobante-series/**"
                ).hasAnyAuthority(
                    "CONFIGURACION_COMPROBANTES",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/comprobante-tipos/**"
                ).hasAnyAuthority(
                    "CONFIGURACION_COMPROBANTES",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/documento-tipos/**"
                ).hasAnyAuthority(
                    "CONFIGURACION_DOCUMENTOS",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/unidades/**"
                ).hasAnyAuthority(
                    "CONFIGURACION_UNIDADES",
                    "ROLE_ADMIN"
                )

                .requestMatchers(
                    "/afectacion-tipos/**"
                ).hasAnyAuthority(
                    "CONFIGURACION_AFECTACIONES",
                    "ROLE_ADMIN"
                )


                // =================================================
                // TODO LO DEMÁS
                // =================================================

                .anyRequest().authenticated()
            )


            // =================================================
            // LOGIN
            // =================================================

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureHandler(new LoginFailureHandler())
                .permitAll()
            )


            // =================================================
            // LOGOUT
            // =================================================

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )


            // =================================================
            // ACCESO DENEGADO
            // =================================================

            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            );

        return http.build();
    }

    // ============================================================
    // AUTHENTICATION SUCCESS HANDLER
    // ============================================================

    @Bean
    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new WebAuthenticationSuccessHandler();
    }
}
