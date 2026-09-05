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

        String loginRole = esColaborador(authorities) ? "colaborador" : "cliente";

        // Guardamos el tipo de acceso en la sesión persistente.
        request.getSession(true).setAttribute("loginRole", loginRole);

        String targetUrl;

        // ==========================================
        // ENTRÓ COMO CLIENTE
        // ==========================================

        if ("colaborador".equals(loginRole)) {
            targetUrl = "/dashboard/colaboradores";
        } else if (authorities.contains("ROLE_CLIENTE")) {
            targetUrl = "/dashboard/cliente";
        } else {
            targetUrl = "/error/403";
        }

        getRedirectStrategy().sendRedirect(
                request,
                response,
                targetUrl
        );
    }

    private boolean esColaborador(Set<String> authorities) {
        return authorities.contains("ROLE_ADMIN")
                || authorities.contains("ROLE_VETERINARIO")
                || authorities.contains("ROLE_RECEPCIONISTA");
    }
}
