package com.Happypaws.demo.config;

import com.Happypaws.demo.service.LoginAttemptService;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Escucha los eventos que Spring Security publica automáticamente durante
 * el login (formLogin) y los traduce en llamadas a LoginAttemptService:
 * cada credencial incorrecta suma un intento fallido, y un login exitoso
 * limpia el contador de esa cuenta.
 */
@Component
public class LoginAttemptListener {

    private final LoginAttemptService loginAttemptService;

    public LoginAttemptListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String email = event.getAuthentication().getName();
        loginAttemptService.loginFailed(email);
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String email = event.getAuthentication().getName();
        loginAttemptService.loginSucceeded(email);
    }
}
