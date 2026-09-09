package com.Happypaws.demo.service;

import com.Happypaws.demo.model.PasswordResetToken;
import com.Happypaws.demo.model.User;
import com.Happypaws.demo.repository.PasswordResetTokenRepository;
import com.Happypaws.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
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
        private final EmailService emailService;

    @Value("${app.base-url}")
    private String baseUrl;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void solicitarRecuperacion(String email) {

        userRepository.findByEmail(email).ifPresent(user -> {

            // Invalidar tokens anteriores
            tokenRepository.invalidarTokensAnteriores(user.getIdUsuario());

            // Crear nuevo token
            PasswordResetToken token = new PasswordResetToken();

            token.setToken(UUID.randomUUID().toString());
            token.setUser(user);

            // El enlace será válido durante 30 minutos
            token.setExpiryDate(
                    LocalDateTime.now().plusMinutes(30)
            );

            token.setUsed(false);

            tokenRepository.save(token);

            enviarCorreo(user, token.getToken());
        });
    }

    private void enviarCorreo(User user, String token) {

        String link = baseUrl + "/reset-password?token=" + token;

        String cuerpo = "🐾 HAPPY PAWS - VETERINARIA 🐾\n\n"
            + "Hola " + user.getName() + ",\n\n" 
            + "¡Hola! Recibimos una solicitud para restablecer la contraseña de tu cuenta en Happy Paws. 🐶🐱\n\n" 
            + "Para crear una nueva contraseña y recuperar el acceso a tu cuenta, utiliza el siguiente enlace:\n\n" 
            + "🔐 RESTABLECER MI CONTRASEÑA\n" + link + "\n\n" 
            + "⏱️ Este enlace es válido durante 30 minutos por motivos de seguridad.\n\n" 
            + "Si tú no solicitaste este cambio, puedes ignorar este correo. " 
            + "Tu contraseña actual continuará siendo válida.\n\n" 
            + "En Happy Paws cuidamos de tus mascotas y también de la seguridad de tu información. ❤️🐾\n\n" + "Saludos,\n" 
            + "Equipo Happy Paws\n" + "Veterinaria Happy Paws 🐾";

        emailService.enviarCorreoSimple(
                user.getEmail(), "Recupera tu contraseña - Happy Paws", cuerpo);
    }

    public boolean tokenValido(String token) {

        return tokenRepository.findByToken(token)
                .filter(t -> !t.getUsed())
                .filter(t ->
                        t.getExpiryDate()
                                .isAfter(LocalDateTime.now())
                )
                .isPresent();
    }

    @Transactional
    public void restablecerPassword(
            String token,
            String nuevaPassword) {

        if (nuevaPassword == null || nuevaPassword.length() < 8) {

            throw new IllegalArgumentException(
                    "La contraseña debe tener mínimo 8 caracteres"
            );
        }

        PasswordResetToken resetToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Token inválido"
                                )
                        );

        if (resetToken.getUsed()
                || !resetToken.getExpiryDate()
                        .isAfter(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "El enlace expiró o ya fue usado"
            );
        }

        User user = resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(nuevaPassword)
        );

        userRepository.save(user);

        // El token solamente puede utilizarse una vez
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}
