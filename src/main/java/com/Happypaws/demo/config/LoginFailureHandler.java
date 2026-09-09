package com.Happypaws.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * Igual que el failureUrl por defecto ("/login?error=true"), pero cuando la
 * cuenta está bloqueada por LoginAttemptService (ver UserDetailsServiceImpl)
 * redirige con "?locked=true" para mostrar un mensaje distinto al usuario en
 * vez de "credenciales inválidas", que sería confuso en ese caso.
 */
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public LoginFailureHandler() {
        super("/login?error=true");
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        if (exception instanceof LockedException) {
            getRedirectStrategy().sendRedirect(request, response, "/login?locked=true");
            return;
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}
