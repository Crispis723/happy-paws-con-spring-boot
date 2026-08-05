package com.Happypaws.demo.service;

import com.Happypaws.demo.model.PasswordResetToken;
import com.Happypaws.demo.model.User;
import com.Happypaws.demo.repository.PasswordResetTokenRepository;
import com.Happypaws.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public PasswordResetService(UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Transactional
    public void solicitarRecuperacion(String email) {

        userRepository.findByEmail(email).ifPresent(user -> {

            // Invalidar tokens anteriores
            tokenRepository.invalidarTokensAnteriores(user.getId());

            // Crear nuevo token
            PasswordResetToken token = new PasswordResetToken();
            token.setToken(UUID.randomUUID().toString());
            token.setUser(user);

            // El enlace será válido durante 30 minutos
            token.setExpiryDate(LocalDateTime.now().plusMinutes(30));

            token.setUsed(false);

            tokenRepository.save(token);

            enviarCorreo(user, token.getToken());
        });
    }

    private void enviarCorreo(User user, String token) {
        String link = baseUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Recupera tu contraseña - Happy Paws");
        message.setText("Hola " + user.getName() + ",\n\n"
                + "Recibimos una solicitud para restablecer tu contraseña.\n"
                + "Haz clic en el siguiente enlace (válido por 30 minutos):\n\n"
                + link + "\n\n"
                + "Si no fuiste tú, ignora este correo.");

        mailSender.send(message);
    }

    public boolean tokenValido(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.getUsed())
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    @Transactional
    public void restablecerPassword(String token, String nuevaPassword) {

        if (nuevaPassword == null || nuevaPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener mínimo 8 caracteres"
            );
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (resetToken.getUsed()
                || !resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "El enlace expiró o ya fue usado"
            );
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(nuevaPassword));
        userRepository.save(user);

        // El token solamente puede utilizarse una vez
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}
