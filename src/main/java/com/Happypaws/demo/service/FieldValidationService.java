package com.Happypaws.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Servicio de validación de campos personalizadas
 * Proporciona métodos para validar:
 * - Emails
 * - Teléfonos
 * - Números positivos y negativos
 * - Fechas y horas
 * - Documentos de identidad
 */
@Service
@Slf4j
public class FieldValidationService {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\+?[0-9]{7,15}$");
    
    private static final Pattern RUC_PATTERN = 
        Pattern.compile("^[0-9]{11}$");
    
    private static final Pattern DNI_PATTERN = 
        Pattern.compile("^[0-9]{8}$");

    /**
     * Valida un correo electrónico
     */
    public boolean esEmailValido(String email) {
        if (email == null || email.isBlank()) {
            log.warn("Email vacío");
            return false;
        }
        
        boolean valido = EMAIL_PATTERN.matcher(email).matches();
        if (!valido) {
            log.warn("Email inválido: {}", email);
        }
        return valido;
    }

    /**
     * Valida un número de teléfono
     * Acepta formatos: +51999999999 o 999999999
     */
    public boolean esTelefonoValido(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            log.warn("Teléfono vacío");
            return false;
        }
        
        String telefonoLimpio = telefono.replaceAll("[\\s()-]", "");
        boolean valido = PHONE_PATTERN.matcher(telefonoLimpio).matches();
        if (!valido) {
            log.warn("Teléfono inválido: {}", telefono);
        }
        return valido;
    }

    /**
     * Valida un número positivo
     */
    public boolean esNumeroPositivo(Number numero) {
        if (numero == null) {
            log.warn("Número nulo");
            return false;
        }
        
        boolean valido = numero.doubleValue() > 0;
        if (!valido) {
            log.warn("Número no positivo: {}", numero);
        }
        return valido;
    }

    /**
     * Valida un número no negativo (incluye 0)
     */
    public boolean esNumeroNoNegativo(Number numero) {
        if (numero == null) {
            log.warn("Número nulo");
            return false;
        }
        
        boolean valido = numero.doubleValue() >= 0;
        if (!valido) {
            log.warn("Número negativo: {}", numero);
        }
        return valido;
    }

    /**
     * Valida un RUC (11 dígitos)
     */
    public boolean esRucValido(String ruc) {
        if (ruc == null || ruc.isBlank()) {
            log.warn("RUC vacío");
            return false;
        }
        
        String rucLimpio = ruc.replaceAll("[^0-9]", "");
        boolean valido = RUC_PATTERN.matcher(rucLimpio).matches();
        if (!valido) {
            log.warn("RUC inválido: {}", ruc);
        }
        return valido;
    }

    /**
     * Valida un DNI (8 dígitos)
     */
    public boolean esDniValido(String dni) {
        if (dni == null || dni.isBlank()) {
            log.warn("DNI vacío");
            return false;
        }
        
        String dniLimpio = dni.replaceAll("[^0-9]", "");
        boolean valido = DNI_PATTERN.matcher(dniLimpio).matches();
        if (!valido) {
            log.warn("DNI inválido: {}", dni);
        }
        return valido;
    }

    /**
     * Valida una fecha en formato DD/MM/YYYY
     */
    public boolean esFechaValida(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            log.warn("Fecha vacía");
            return false;
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(fecha, formatter);
            return true;
        } catch (DateTimeParseException e) {
            log.warn("Fecha inválida: {} - {}", fecha, e.getMessage());
            return false;
        }
    }

    /**
     * Valida una fecha y hora en formato DD/MM/YYYY HH:mm
     */
    public boolean esFechaHoraValida(String fechaHora) {
        if (fechaHora == null || fechaHora.isBlank()) {
            log.warn("Fecha y hora vacías");
            return false;
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime.parse(fechaHora, formatter);
            return true;
        } catch (DateTimeParseException e) {
            log.warn("Fecha y hora inválidas: {} - {}", fechaHora, e.getMessage());
            return false;
        }
    }

    /**
     * Valida que una fecha sea en el futuro
     */
    public boolean esFechaFutura(LocalDateTime fecha) {
        if (fecha == null) {
            log.warn("Fecha nula");
            return false;
        }
        
        boolean valida = fecha.isAfter(LocalDateTime.now());
        if (!valida) {
            log.warn("Fecha no es futura: {}", fecha);
        }
        return valida;
    }

    /**
     * Valida que una fecha sea en el pasado
     */
    public boolean esFechaPasada(LocalDateTime fecha) {
        if (fecha == null) {
            log.warn("Fecha nula");
            return false;
        }
        
        boolean valida = fecha.isBefore(LocalDateTime.now());
        if (!valida) {
            log.warn("Fecha no es pasada: {}", fecha);
        }
        return valida;
    }

    /**
     * Valida que una fecha esté dentro de un rango
     */
    public boolean esFechaEnRango(LocalDateTime fecha, LocalDateTime inicio, LocalDateTime fin) {
        if (fecha == null || inicio == null || fin == null) {
            log.warn("Fecha o rango nulo");
            return false;
        }
        
        boolean valida = fecha.isAfter(inicio) && fecha.isBefore(fin);
        if (!valida) {
            log.warn("Fecha {} no está en rango [{}, {}]", fecha, inicio, fin);
        }
        return valida;
    }

    /**
     * Valida que un texto tenga una longitud específica
     */
    public boolean esLongitudValida(String texto, int minimo, int maximo) {
        if (texto == null) {
            log.warn("Texto nulo");
            return false;
        }
        
        int longitud = texto.length();
        boolean valida = longitud >= minimo && longitud <= maximo;
        if (!valida) {
            log.warn("Texto tiene {} caracteres, se esperaba entre {} y {}", 
                longitud, minimo, maximo);
        }
        return valida;
    }

    /**
     * Valida que un porcentaje esté entre 0 y 100
     */
    public boolean esPorcentajeValido(Number porcentaje) {
        if (porcentaje == null) {
            log.warn("Porcentaje nulo");
            return false;
        }
        
        double valor = porcentaje.doubleValue();
        boolean valido = valor >= 0 && valor <= 100;
        if (!valido) {
            log.warn("Porcentaje fuera de rango: {}", valor);
        }
        return valido;
    }
}
