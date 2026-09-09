package com.Happypaws.demo.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lleva la cuenta de intentos fallidos de inicio de sesión por correo y
 * bloquea temporalmente una cuenta tras demasiados intentos seguidos.
 *
 * Implementación en memoria (ConcurrentHashMap): suficiente para una sola
 * instancia del servidor. Si en el futuro Happy Paws corre en varias
 * instancias detrás de un balanceador, esto debería moverse a Redis o a una
 * tabla en PostgreSQL para que el contador se comparta entre instancias.
 */
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private static final class Attempt {
        int count;
        Instant lockedUntil;
    }

    private final ConcurrentHashMap<String, Attempt> attemptsByEmail = new ConcurrentHashMap<>();

    /** Registra un intento fallido; bloquea la cuenta si llega al máximo. */
    public void loginFailed(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        String key = normalize(email);
        Attempt attempt = attemptsByEmail.computeIfAbsent(key, k -> new Attempt());

        synchronized (attempt) {
            attempt.count++;
            if (attempt.count >= MAX_ATTEMPTS) {
                attempt.lockedUntil = Instant.now().plus(LOCK_DURATION);
            }
        }
    }

    /** Limpia el contador tras un login exitoso. */
    public void loginSucceeded(String email) {
        if (email == null) {
            return;
        }
        attemptsByEmail.remove(normalize(email));
    }

    /** true si la cuenta está actualmente bloqueada por intentos fallidos. */
    public boolean isBlocked(String email) {
        if (email == null) {
            return false;
        }
        Attempt attempt = attemptsByEmail.get(normalize(email));
        if (attempt == null || attempt.lockedUntil == null) {
            return false;
        }
        if (Instant.now().isAfter(attempt.lockedUntil)) {
            // El bloqueo ya expiró: se reinicia el contador para no dejar
            // la entrada colgada en el mapa indefinidamente.
            attemptsByEmail.remove(normalize(email));
            return false;
        }
        return true;
    }

    /** Minutos restantes de bloqueo (redondeado hacia arriba), 0 si no está bloqueada. */
    public long minutesRemaining(String email) {
        Attempt attempt = attemptsByEmail.get(normalize(email));
        if (attempt == null || attempt.lockedUntil == null) {
            return 0;
        }
        long seconds = Duration.between(Instant.now(), attempt.lockedUntil).getSeconds();
        return Math.max(0, (seconds + 59) / 60);
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
