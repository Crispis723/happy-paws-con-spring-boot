package com.Happypaws.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.name:Happy Paws}")
    private String appName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía un correo simple de texto
     */
    public void enviarCorreoSimple(String para, String asunto, String cuerpo) {
        try {
            if (fromEmail == null || fromEmail.isEmpty()) {
                log.warn("Email no configurado. Omitiendo envío a: {}", para);
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(para);
            message.setSubject(asunto);
            message.setText(cuerpo);

            mailSender.send(message);
            log.info("Correo enviado exitosamente a: {}", para);

        } catch (Exception e) {
            log.error("Error al enviar correo a {}: {}", para, e.getMessage(), e);
            throw new RuntimeException("Error al enviar correo: " + e.getMessage());
        }
    }

    /**
     * Envía un correo con formato HTML
     */
    public void enviarCorreoHTML(String para, String asunto, String cuerpoHTML) {
        try {
            if (fromEmail == null || fromEmail.isEmpty()) {
                log.warn("Email no configurado. Omitiendo envío HTML a: {}", para);
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(cuerpoHTML, true); // true = es HTML

            mailSender.send(message);
            log.info("Correo HTML enviado exitosamente a: {}", para);

        } catch (Exception e) {
            log.error("Error al enviar correo HTML a {}: {}", para, e.getMessage(), e);
            throw new RuntimeException("Error al enviar correo HTML: " + e.getMessage());
        }
    }

    /**
     * Envía un correo a múltiples destinatarios
     */
    public void enviarCorreoMultiple(String[] paraBcc, String asunto, String cuerpo) {
        try {
            if (fromEmail == null || fromEmail.isEmpty()) {
                log.warn("Email no configurado. Omitiendo envío múltiple");
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setBcc(paraBcc);
            message.setSubject(asunto);
            message.setText(cuerpo);

            mailSender.send(message);
            log.info("Correo múltiple enviado a {} destinatarios", paraBcc.length);

        } catch (Exception e) {
            log.error("Error al enviar correo múltiple: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar correo múltiple: " + e.getMessage());
        }
    }

    /**
     * Plantilla de correo para recordatorio de cita
     */
    public String generarPlantillaRecordatorioCita(
            String nombreCliente,
            String nombreMascota,
            String fechaHora,
            String nombreVeterinario) {

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }\n" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; }\n" +
                "        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }\n" +
                "        .content { padding: 20px; }\n" +
                "        .info-box { background-color: #f9f9f9; padding: 15px; margin: 15px 0; border-left: 4px solid #4CAF50; }\n" +
                "        .footer { background-color: #f5f5f5; padding: 10px; text-align: center; font-size: 12px; color: #999; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>🐾 Recordatorio de Cita - Happy Paws</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Hola <strong>" + nombreCliente + "</strong>,</p>\n" +
                "            <p>Te recordamos que tienes una cita próxima con nosotros.</p>\n" +
                "            <div class=\"info-box\">\n" +
                "                <p><strong>📅 Fecha y Hora:</strong> " + fechaHora + "</p>\n" +
                "                <p><strong>🐕 Mascota:</strong> " + nombreMascota + "</p>\n" +
                "                <p><strong>👨‍⚕️ Veterinario:</strong> " + nombreVeterinario + "</p>\n" +
                "            </div>\n" +
                "            <p>Si necesitas cambiar o cancelar tu cita, por favor contáctanos lo antes posible.</p>\n" +
                "            <p>¡Esperamos verte pronto!</p>\n" +
                "            <p>Saludos,<br><strong>Equipo Happy Paws</strong></p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>Este es un correo automático. Por favor no responder a este mensaje.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * Plantilla de correo de confirmación de cita
     */
    public String generarPlantillaConfirmacionCita(
            String nombreCliente,
            String nombreMascota,
            String fechaHora,
            String nombreVeterinario,
            String motivo) {

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }\n" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; }\n" +
                "        .header { background-color: #2196F3; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }\n" +
                "        .content { padding: 20px; }\n" +
                "        .success { background-color: #d4edda; padding: 15px; margin: 15px 0; border-left: 4px solid #28a745; border-radius: 4px; }\n" +
                "        .info-box { background-color: #f9f9f9; padding: 15px; margin: 15px 0; border-left: 4px solid #2196F3; }\n" +
                "        .footer { background-color: #f5f5f5; padding: 10px; text-align: center; font-size: 12px; color: #999; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>✓ Cita Confirmada - Happy Paws</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Hola <strong>" + nombreCliente + "</strong>,</p>\n" +
                "            <div class=\"success\">\n" +
                "                <p><strong>¡Tu cita ha sido confirmada exitosamente!</strong></p>\n" +
                "            </div>\n" +
                "            <div class=\"info-box\">\n" +
                "                <p><strong>📅 Fecha y Hora:</strong> " + fechaHora + "</p>\n" +
                "                <p><strong>🐕 Mascota:</strong> " + nombreMascota + "</p>\n" +
                "                <p><strong>👨‍⚕️ Veterinario:</strong> " + nombreVeterinario + "</p>\n" +
                "                <p><strong>📝 Motivo:</strong> " + motivo + "</p>\n" +
                "            </div>\n" +
                "            <p>Por favor llega 10 minutos antes de tu cita. Si tienes alguna pregunta, no dudes en contactarnos.</p>\n" +
                "            <p>¡Gracias por confiar en nosotros!</p>\n" +
                "            <p>Saludos,<br><strong>Equipo Happy Paws</strong></p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>Este es un correo automático. Por favor no responder a este mensaje.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
