package com.Happypaws.demo.service;

import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.model.Pet;
import com.Happypaws.demo.model.User;
import com.Happypaws.demo.repository.AppointmentRepository;
import com.Happypaws.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class AppointmentReminderService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public AppointmentReminderService(
            AppointmentRepository appointmentRepository,
            UserRepository userRepository,
            EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Envía recordatorios automáticos 24 horas antes de cada cita
     * Se ejecuta cada hora
     */
    @Scheduled(cron = "0 0 * * * *") // Cada hora
    @Transactional
    public void enviarRecordatorios24Horas() {
        try {
            log.info("Iniciando envío de recordatorios de citas (24 horas antes)");

            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime en24Horas = ahora.plusHours(24);
            LocalDateTime margen = ahora.plusHours(25);

            // Buscar citas en las próximas 24-25 horas
            List<Appointment> citasProximas = appointmentRepository
                    .findAppointmentsInTimeRange(en24Horas, margen);

            log.info("Se encontraron {} citas para recordatorio", citasProximas.size());

            for (Appointment cita : citasProximas) {
                enviarRecordatorioCita(cita);
            }

        } catch (Exception e) {
            log.error("Error al enviar recordatorios: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía recordatorios automáticos 1 hora antes de cada cita
     * Se ejecuta cada 30 minutos
     */
    @Scheduled(cron = "0 */30 * * * *") // Cada 30 minutos
    @Transactional
    public void enviarRecordatorios1Hora() {
        try {
            log.info("Iniciando envío de recordatorios de citas (1 hora antes)");

            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime en1Hora = ahora.plusMinutes(60);
            LocalDateTime margen = ahora.plusMinutes(70);

            // Buscar citas en la próxima hora
            List<Appointment> citasInminentes = appointmentRepository
                    .findAppointmentsInTimeRange(en1Hora, margen);

            log.info("Se encontraron {} citas inminentes para recordatorio", citasInminentes.size());

            for (Appointment cita : citasInminentes) {
                enviarRecordatorioInminente(cita);
            }

        } catch (Exception e) {
            log.error("Error al enviar recordatorios inminentes: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía recordatorio de cita al cliente (24 horas antes)
     */
    @Transactional
    public void enviarRecordatorioCita(Appointment cita) {
        try {
            if (cita == null || cita.getCliente() == null) {
                log.warn("Cita o cliente no válido");
                return;
            }

            Cliente cliente = cita.getCliente();
            if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isEmpty()) {
                log.warn("Cliente sin email para cita {}", cita.getIdCita());
                return;
            }

            Pet mascota = cita.getMascota();
            String nombreMascota = mascota != null ? mascota.getNombre() : "Mascota";

            User veterinario = cita.getVeterinario();
            String nombreVeterinario = veterinario != null ? veterinario.getName() : "Veterinario";

            String fechaFormato = cita.getFechaHora().format(FORMATTER);

            String asunto = "📅 Recordatorio de cita - Happy Paws";

            String cuerpoHTML = emailService.generarPlantillaRecordatorioCita(
                    cliente.getRazonSocial(),
                    nombreMascota,
                    fechaFormato,
                    nombreVeterinario
            );

            emailService.enviarCorreoHTML(cliente.getEmail(), asunto, cuerpoHTML);
            log.info("Recordatorio enviado a {} para cita en {}", cliente.getEmail(), fechaFormato);

        } catch (Exception e) {
            log.error("Error al enviar recordatorio de cita {}: {}", 
                    cita.getIdCita(), e.getMessage(), e);
        }
    }

    /**
     * Envía recordatorio urgente de cita al cliente (1 hora antes)
     */
    @Transactional
    public void enviarRecordatorioInminente(Appointment cita) {
        try {
            if (cita == null || cita.getCliente() == null) {
                log.warn("Cita o cliente no válido para recordatorio inminente");
                return;
            }

            Cliente cliente = cita.getCliente();
            if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isEmpty()) {
                log.warn("Cliente sin email para cita inminente {}", cita.getIdCita());
                return;
            }

            Pet mascota = cita.getMascota();
            String nombreMascota = mascota != null ? mascota.getNombre() : "Mascota";

            User veterinario = cita.getVeterinario();
            String nombreVeterinario = veterinario != null ? veterinario.getName() : "Veterinario";

            String fechaFormato = cita.getFechaHora().format(FORMATTER);

            String asunto = "⏰ ¡Tu cita es en 1 hora! - Happy Paws";

            String cuerpoHTML = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <style>\n" +
                    "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }\n" +
                    "        .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ff9800; border-radius: 8px; background-color: #fff3e0; }\n" +
                    "        .header { background-color: #ff9800; color: white; padding: 20px; text-align: center; border-radius: 8px; }\n" +
                    "        .content { padding: 20px; }\n" +
                    "        .alert { background-color: #ffe0b2; padding: 15px; margin: 15px 0; border-left: 4px solid #ff9800; border-radius: 4px; }\n" +
                    "        .info-box { background-color: #f9f9f9; padding: 15px; margin: 15px 0; border-left: 4px solid #ff9800; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h1>⏰ ¡Tu cita es en 1 hora!</h1>\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "            <p>Hola <strong>" + cliente.getRazonSocial() + "</strong>,</p>\n" +
                    "            <div class=\"alert\">\n" +
                    "                <p><strong>¡Tu cita es en aproximadamente 1 hora!</strong></p>\n" +
                    "            </div>\n" +
                    "            <div class=\"info-box\">\n" +
                    "                <p><strong>📅 Fecha y Hora:</strong> " + fechaFormato + "</p>\n" +
                    "                <p><strong>🐕 Mascota:</strong> " + nombreMascota + "</p>\n" +
                    "                <p><strong>👨‍⚕️ Veterinario:</strong> " + nombreVeterinario + "</p>\n" +
                    "            </div>\n" +
                    "            <p>Por favor, prepárate y asegúrate de estar listo para tu cita.</p>\n" +
                    "            <p>Saludos,<br><strong>Equipo Happy Paws</strong></p>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            emailService.enviarCorreoHTML(cliente.getEmail(), asunto, cuerpoHTML);
            log.info("Recordatorio inminente enviado a {} para cita en {}", 
                    cliente.getEmail(), fechaFormato);

        } catch (Exception e) {
            log.error("Error al enviar recordatorio inminente de cita {}: {}", 
                    cita.getIdCita(), e.getMessage(), e);
        }
    }

    /**
     * Envía confirmación de cita al cliente inmediatamente después de crearla
     */
    @Transactional
    public void enviarConfirmacionCita(Appointment cita) {
        try {
            if (cita == null || cita.getCliente() == null) {
                log.warn("Cita o cliente no válido para confirmación");
                return;
            }

            Cliente cliente = cita.getCliente();
            if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isEmpty()) {
                log.warn("Cliente sin email para confirmación de cita {}", cita.getIdCita());
                return;
            }

            Pet mascota = cita.getMascota();
            String nombreMascota = mascota != null ? mascota.getNombre() : "Mascota";

            User veterinario = cita.getVeterinario();
            String nombreVeterinario = veterinario != null ? veterinario.getName() : "Veterinario";

            String fechaFormato = cita.getFechaHora().format(FORMATTER);
            String motivo = cita.getMotivo() != null ? cita.getMotivo() : "No especificado";

            String asunto = "✓ Cita Confirmada - Happy Paws";

            String cuerpoHTML = emailService.generarPlantillaConfirmacionCita(
                    cliente.getRazonSocial(),
                    nombreMascota,
                    fechaFormato,
                    nombreVeterinario,
                    motivo
            );

            emailService.enviarCorreoHTML(cliente.getEmail(), asunto, cuerpoHTML);
            log.info("Confirmación enviada a {} para cita en {}", cliente.getEmail(), fechaFormato);

        } catch (Exception e) {
            log.error("Error al enviar confirmación de cita {}: {}", 
                    cita.getIdCita(), e.getMessage(), e);
        }
    }

    /**
     * Envía notificación de cancelación de cita
     */
    @Transactional
    public void enviarNotificacionCancelacion(Appointment cita, String motivoCancelacion) {
        try {
            if (cita == null || cita.getCliente() == null) {
                log.warn("Cita o cliente no válido para cancelación");
                return;
            }

            Cliente cliente = cita.getCliente();
            if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isEmpty()) {
                log.warn("Cliente sin email para cancelación de cita {}", cita.getIdCita());
                return;
            }

            Pet mascota = cita.getMascota();
            String nombreMascota = mascota != null ? mascota.getNombre() : "Mascota";

            String fechaFormato = cita.getFechaHora().format(FORMATTER);

            String asunto = "❌ Cita Cancelada - Happy Paws";

            String cuerpoHTML = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <style>\n" +
                    "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }\n" +
                    "        .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #f44336; border-radius: 8px; }\n" +
                    "        .header { background-color: #f44336; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }\n" +
                    "        .content { padding: 20px; }\n" +
                    "        .info-box { background-color: #f9f9f9; padding: 15px; margin: 15px 0; border-left: 4px solid #f44336; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h1>❌ Cita Cancelada</h1>\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "            <p>Hola <strong>" + cliente.getRazonSocial() + "</strong>,</p>\n" +
                    "            <p>Te informamos que tu cita ha sido cancelada.</p>\n" +
                    "            <div class=\"info-box\">\n" +
                    "                <p><strong>📅 Fecha y Hora:</strong> " + fechaFormato + "</p>\n" +
                    "                <p><strong>🐕 Mascota:</strong> " + nombreMascota + "</p>\n" +
                    "                <p><strong>📝 Motivo:</strong> " + motivoCancelacion + "</p>\n" +
                    "            </div>\n" +
                    "            <p>Si deseas reprogramar, por favor contáctanos.</p>\n" +
                    "            <p>Saludos,<br><strong>Equipo Happy Paws</strong></p>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            emailService.enviarCorreoHTML(cliente.getEmail(), asunto, cuerpoHTML);
            log.info("Notificación de cancelación enviada a {}", cliente.getEmail());

        } catch (Exception e) {
            log.error("Error al enviar cancelación de cita {}: {}", 
                    cita.getIdCita(), e.getMessage(), e);
        }
    }
}
