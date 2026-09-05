package com.Happypaws.demo.controller;

import com.Happypaws.demo.service.EmailService;
import com.Happypaws.demo.service.FieldValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de prueba para validación y envío de correos
 * Rutas:
 * - POST /api/test/email/send - Envía un correo de prueba
 * - GET /api/test/validate/email/{email} - Valida un email
 * - GET /api/test/validate/phone/{phone} - Valida un teléfono
 * - GET /api/test/validate/ruc/{ruc} - Valida un RUC
 * - GET /api/test/validate/dni/{dni} - Valida un DNI
 * - GET /api/test/validate/date/{date} - Valida una fecha (DD/MM/YYYY)
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestValidationController {

    private final EmailService emailService;
    private final FieldValidationService validationService;

    public TestValidationController(EmailService emailService, FieldValidationService validationService) {
        this.emailService = emailService;
        this.validationService = validationService;
    }

    /**
     * Envía un correo de prueba simple
     * POST /api/test/email/send?to=correo@example.com&subject=Asunto&body=Cuerpo
     */
    @PostMapping("/email/send")
    public ResponseEntity<?> enviarCorreoPrueba(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body) {
        try {
            // Validar email
            if (!validationService.esEmailValido(to)) {
                return ResponseEntity.badRequest().body(
                    "El email '" + to + "' no es válido"
                );
            }

            // Enviar correo
            emailService.enviarCorreoSimple(to, subject, body);
            
            return ResponseEntity.ok().body(
                "Correo enviado exitosamente a: " + to
            );
        } catch (Exception e) {
            log.error("Error al enviar correo de prueba: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                "Error al enviar correo: " + e.getMessage()
            );
        }
    }

    /**
     * Envía un correo HTML de prueba
     * POST /api/test/email/send-html?to=correo@example.com
     */
    @PostMapping("/email/send-html")
    public ResponseEntity<?> enviarCorreoHTMLPrueba(
            @RequestParam String to) {
        try {
            if (!validationService.esEmailValido(to)) {
                return ResponseEntity.badRequest().body(
                    "El email '" + to + "' no es válido"
                );
            }

            String html = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head><meta charset='UTF-8'>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f5f5f5; }" +
                    ".container { max-width: 600px; margin: 20px auto; background-color: white; padding: 20px; border-radius: 8px; }" +
                    ".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 8px; }" +
                    ".success { color: #28a745; padding: 10px; margin: 15px 0; border-left: 4px solid #28a745; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div class='header'><h1>✓ Prueba de Email HTML</h1></div>" +
                    "<div style='padding: 20px;'>" +
                    "<p>Hola,</p>" +
                    "<div class='success'>" +
                    "<p><strong>¡Tu correo de prueba ha sido enviado exitosamente!</strong></p>" +
                    "</div>" +
                    "<p>Este es un correo de prueba para validar la configuración SMTP.</p>" +
                    "<p>Fecha y hora: " + java.time.LocalDateTime.now() + "</p>" +
                    "<p>Saludos,<br><strong>Equipo Happy Paws</strong></p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            emailService.enviarCorreoHTML(to, "✓ Prueba de Email HTML - Happy Paws", html);
            
            return ResponseEntity.ok().body(
                "Correo HTML enviado exitosamente a: " + to
            );
        } catch (Exception e) {
            log.error("Error al enviar correo HTML de prueba: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                "Error al enviar correo: " + e.getMessage()
            );
        }
    }

    /**
     * Valida un email
     * GET /api/test/validate/email/correo@example.com
     */
    @GetMapping("/validate/email/{email}")
    public ResponseEntity<?> validarEmail(@PathVariable String email) {
        boolean valido = validationService.esEmailValido(email);
        return ResponseEntity.ok().body(new ValidationResponse(
            "Email: " + email,
            valido ? "✓ Email válido" : "✗ Email inválido",
            valido
        ));
    }

    /**
     * Valida un teléfono
     * GET /api/test/validate/phone/999999999
     */
    @GetMapping("/validate/phone/{phone}")
    public ResponseEntity<?> validarTelefono(@PathVariable String phone) {
        boolean valido = validationService.esTelefonoValido(phone);
        return ResponseEntity.ok().body(new ValidationResponse(
            "Teléfono: " + phone,
            valido ? "✓ Teléfono válido" : "✗ Teléfono inválido",
            valido
        ));
    }

    /**
     * Valida un RUC
     * GET /api/test/validate/ruc/12345678901
     */
    @GetMapping("/validate/ruc/{ruc}")
    public ResponseEntity<?> validarRuc(@PathVariable String ruc) {
        boolean valido = validationService.esRucValido(ruc);
        return ResponseEntity.ok().body(new ValidationResponse(
            "RUC: " + ruc,
            valido ? "✓ RUC válido (11 dígitos)" : "✗ RUC inválido",
            valido
        ));
    }

    /**
     * Valida un DNI
     * GET /api/test/validate/dni/12345678
     */
    @GetMapping("/validate/dni/{dni}")
    public ResponseEntity<?> validarDni(@PathVariable String dni) {
        boolean valido = validationService.esDniValido(dni);
        return ResponseEntity.ok().body(new ValidationResponse(
            "DNI: " + dni,
            valido ? "✓ DNI válido (8 dígitos)" : "✗ DNI inválido",
            valido
        ));
    }

    /**
     * Valida una fecha
     * GET /api/test/validate/date/25/12/2024
     */
    @GetMapping("/validate/date/{day}/{month}/{year}")
    public ResponseEntity<?> validarFecha(
            @PathVariable String day,
            @PathVariable String month,
            @PathVariable String year) {
        String fecha = day + "/" + month + "/" + year;
        boolean valido = validationService.esFechaValida(fecha);
        return ResponseEntity.ok().body(new ValidationResponse(
            "Fecha: " + fecha,
            valido ? "✓ Fecha válida (DD/MM/YYYY)" : "✗ Fecha inválida",
            valido
        ));
    }

    /**
     * Valida un número
     * GET /api/test/validate/number/123.45
     */
    @GetMapping("/validate/number/{number}")
    public ResponseEntity<?> validarNumero(@PathVariable Double number) {
        boolean valido = validationService.esNumeroPositivo(number);
        return ResponseEntity.ok().body(new ValidationResponse(
            "Número: " + number,
            valido ? "✓ Número positivo válido" : "✗ Número no es positivo",
            valido
        ));
    }

    /**
     * Valida un porcentaje
     * GET /api/test/validate/percentage/50
     */
    @GetMapping("/validate/percentage/{percentage}")
    public ResponseEntity<?> validarPorcentaje(@PathVariable Double percentage) {
        boolean valido = validationService.esPorcentajeValido(percentage);
        return ResponseEntity.ok().body(new ValidationResponse(
            "Porcentaje: " + percentage,
            valido ? "✓ Porcentaje válido (0-100)" : "✗ Porcentaje fuera de rango",
            valido
        ));
    }

    /**
     * Clase auxiliar para respuestas de validación
     */
    public static class ValidationResponse {
        public final String campo;
        public final String mensaje;
        public final boolean valido;

        public ValidationResponse(String campo, String mensaje, boolean valido) {
            this.campo = campo;
            this.mensaje = mensaje;
            this.valido = valido;
        }

        public String getCampo() { return campo; }
        public String getMensaje() { return mensaje; }
        public boolean isValido() { return valido; }
    }
}
