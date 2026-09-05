package com.Happypaws.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class WebAuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        Set<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // ==========================================
        // TIPO DE LOGIN SELECCIONADO
        // ==========================================

        String loginRole = request.getParameter("loginRole");

        // Si el formulario no envía loginRole, inferimos el tipo de acceso
        // a partir del rol autenticado. Esto evita perder la sesión por un
        // parámetro de formulario ausente.
        if (loginRole == null || loginRole.isBlank()) {
            if (authorities.contains("ROLE_ADMIN")
                    || authorities.contains("ROLE_VETERINARIO")
                    || authorities.contains("ROLE_RECEPCIONISTA")) {
                loginRole = "colaborador";
            } else if (authorities.contains("ROLE_CLIENTE")) {
                loginRole = "cliente";
            }
        }

        // Guardamos el tipo de acceso en la sesión persistente.
        request.getSession(true).setAttribute("loginRole", loginRole);

        String targetUrl;

        // ==========================================
        // ENTRÓ COMO CLIENTE
        // ==========================================

        if ("cliente".equalsIgnoreCase(loginRole)) {

            if (authorities.contains("ROLE_CLIENTE")) {

                targetUrl = "/dashboard/cliente";

            } else {

                targetUrl = "/error/403";
            }

        }

        // ==========================================
        // ENTRÓ COMO COLABORADOR
        // ==========================================

        else if ("colaborador".equalsIgnoreCase(loginRole)) {

            boolean esColaborador =
                    authorities.contains("ROLE_ADMIN")
                    || authorities.contains("ROLE_VETERINARIO")
                    || authorities.contains("ROLE_RECEPCIONISTA");

            if (esColaborador) {

                targetUrl = "/dashboard/colaboradores";

            } else {

                targetUrl = "/error/403";
            }

        }

        // ==========================================
        // LOGIN ROLE NO VÁLIDO
        // ==========================================

        else {

            targetUrl = "/error/403";
        }

        getRedirectStrategy().sendRedirect(
                request,
                response,
                targetUrl
        );
    }
}
