package com.Happypaws.demo.service;

import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${sendgrid.api-key:}")
    private String apiKey;

    @Value("${sendgrid.from-address:}")
    private String fromAddress;

    @Value("${sendgrid.from-name:Happy Paws}")
    private String fromName;

    @Value("${app.name:Happy Paws}")
    private String appName;

    public EmailService(ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    /**
     * Envía un correo simple de texto
     */
    public void enviarCorreoSimple(String para, String asunto, String cuerpo) {
        try {
            enviarPorSendGrid(para, asunto, cuerpo, false);
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
            enviarPorSendGrid(para, asunto, cuerpoHTML, true);
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
            for (String destinatario : paraBcc) {
                enviarPorSendGrid(destinatario, asunto, cuerpo, false);
            }
            log.info("Correo múltiple enviado a {} destinatarios", paraBcc.length);

        } catch (Exception e) {
            log.error("Error al enviar correo múltiple: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar correo múltiple: " + e.getMessage());
        }
    }

    private void enviarPorSendGrid(String para, String asunto, String contenido, boolean esHtml)
            throws Exception {
        if (apiKey.isBlank() || fromAddress.isBlank()) {
            throw new IllegalStateException(
                "SendGrid no está configurado: define SENDGRID_API_KEY "
                    + "y SENDGRID_FROM_ADDRESS");
        }

        Map<String, Object> payload = Map.of(
            "personalizations", List.of(Map.of(
                "to", List.of(Map.of("email", para)),
                "subject", asunto)),
            "from", Map.of("email", fromAddress, "name", fromName),
            "content", List.of(Map.of(
                "type", esHtml ? "text/html" : "text/plain",
                "value", contenido)));
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.sendgrid.com/v3/mail/send"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(
                "SendGrid rechazó el correo (HTTP " + response.statusCode() + "): " + response.body());
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
