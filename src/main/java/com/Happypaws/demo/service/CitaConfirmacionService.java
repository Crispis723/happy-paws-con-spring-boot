package com.Happypaws.demo.service;

import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.model.CitaConfirmacionToken;
import com.Happypaws.demo.model.EstadoCita;
import com.Happypaws.demo.repository.AppointmentRepository;
import com.Happypaws.demo.repository.CitaConfirmacionTokenRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Genera y consume los tokens de un solo uso que le permiten a un
 * cliente confirmar o cancelar su cita desde el botón del correo de
 * recordatorio, sin iniciar sesión.
 *
 * Depende directamente de AppointmentRepository (no de AppointmentService)
 * y de EmailService (no de AppointmentReminderService) a propósito: este
 * servicio es usado POR AppointmentReminderService para generar el token
 * de cada recordatorio, así que si dependiera de AppointmentReminderService
 * (o transitivamente de AppointmentService, que sí depende de
 * AppointmentReminderService) se formaría una dependencia circular entre
 * beans de Spring.
 */
@Service
public class CitaConfirmacionService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final CitaConfirmacionTokenRepository tokenRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    public CitaConfirmacionService(
            CitaConfirmacionTokenRepository tokenRepository,
            AppointmentRepository appointmentRepository,
            EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
    }

    /**
     * Genera un token nuevo para la cita dada, invalidando cualquier
     * token anterior que siga activo (por ejemplo, si ya se había
     * enviado un recordatorio de 24h y ahora se envía el de 1h).
     * El token deja de ser válido en el momento en que empieza la cita:
     * después de esa hora, confirmarla o cancelarla ya no tiene sentido.
     */
    @Transactional
    public String generarToken(Appointment cita) {
        tokenRepository.invalidarTokensAnteriores(cita.getIdCita());

        CitaConfirmacionToken token = new CitaConfirmacionToken();
        token.setToken(UUID.randomUUID().toString());
        token.setCita(cita);
        token.setExpiryDate(cita.getFechaHora());
        token.setUsed(false);

        return tokenRepository.save(token).getToken();
    }

    /**
     * Confirma la asistencia a la cita asociada al token. Devuelve la
     * cita actualizada para que la página pública pueda mostrar los
     * detalles ("Confirmaste tu cita del [fecha] con [veterinario]").
     */
    @Transactional
    public Appointment confirmarPorToken(String token) {
        Appointment cita = consumirToken(token);

        cita.setEstado(EstadoCita.CONFIRMADA);
        Appointment guardada = appointmentRepository.save(cita);

        enviarCorreoConfirmacion(guardada);

        return guardada;
    }

    /**
     * Cancela la cita asociada al token. Devuelve la cita actualizada.
     */
    @Transactional
    public Appointment cancelarPorToken(String token) {
        Appointment cita = consumirToken(token);

        cita.setEstado(EstadoCita.CANCELADA);
        Appointment guardada = appointmentRepository.save(cita);

        enviarCorreoCancelacion(guardada);

        return guardada;
    }

    /**
     * Valida el token (existe, no usado, no expirado) y la cita (no está
     * ya en un estado final), lo marca como usado y devuelve la cita.
     * No guarda todavía el nuevo estado de la cita: eso lo hace cada
     * método que llama a este (confirmarPorToken/cancelarPorToken),
     * después de decidir a qué estado la mueve.
     */
    private Appointment consumirToken(String token) {
        CitaConfirmacionToken confirmacionToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Este enlace no es válido."));

        if (confirmacionToken.getUsed()) {
            throw new IllegalArgumentException("Este enlace ya fue utilizado.");
        }

        if (confirmacionToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Este enlace ya expiró.");
        }

        Appointment cita = confirmacionToken.getCita();

        if (cita.getEstado() != null && cita.getEstado().esFinal()) {
            throw new IllegalArgumentException(
                    "Esta cita ya está " + cita.getEstado().getEtiqueta().toLowerCase()
                            + " y no se puede modificar desde este enlace."
            );
        }

        confirmacionToken.setUsed(true);
        tokenRepository.save(confirmacionToken);

        return cita;
    }

    private void enviarCorreoConfirmacion(Appointment cita) {
        if (cita.getCliente() == null || cita.getCliente().getEmail() == null
                || cita.getCliente().getEmail().isBlank()) {
            return;
        }

        String nombreMascota = cita.getMascota() != null ? cita.getMascota().getNombre() : "tu mascota";
        String nombreVeterinario = cita.getVeterinarioNombre();
        String fechaFormato = cita.getFechaHora().format(FORMATTER);
        String motivo = cita.getMotivo() != null ? cita.getMotivo() : "No especificado";

        String cuerpo = emailService.generarPlantillaConfirmacionCita(
                cita.getCliente().getRazonSocial(),
                nombreMascota,
                fechaFormato,
                nombreVeterinario != null && !nombreVeterinario.isBlank() ? nombreVeterinario : "Por asignar",
                motivo
        );

        emailService.enviarCorreoHTML(cita.getCliente().getEmail(), "✓ Cita Confirmada - Happy Paws", cuerpo);
    }

    private void enviarCorreoCancelacion(Appointment cita) {
        if (cita.getCliente() == null || cita.getCliente().getEmail() == null
                || cita.getCliente().getEmail().isBlank()) {
            return;
        }

        String nombreMascota = cita.getMascota() != null ? cita.getMascota().getNombre() : "tu mascota";
        String fechaFormato = cita.getFechaHora().format(FORMATTER);

        String cuerpo = emailService.generarPlantillaCancelacionCita(
                cita.getCliente().getRazonSocial(),
                nombreMascota,
                fechaFormato,
                "Cancelada por el cliente desde el correo de recordatorio"
        );

        emailService.enviarCorreoHTML(cita.getCliente().getEmail(), "❌ Cita Cancelada - Happy Paws", cuerpo);
    }
}
