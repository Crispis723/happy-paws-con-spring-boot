package com.Happypaws.demo.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuración de JavaMailSender para envío de correos
 * Requiere variables de entorno en application.properties:
 * - spring.mail.host
 * - spring.mail.port
 * - spring.mail.username
 * - spring.mail.password
 * 
 * Spring Boot autoconfigura JavaMailSender automáticamente
 * basado en las propiedades en application.properties
 */
@Configuration
public class MailConfig {
    // La configuración de SMTP se maneja en application.properties
    // Spring Boot autoconfigura JavaMailSender automáticamente
}
