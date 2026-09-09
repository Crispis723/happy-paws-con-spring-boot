package com.Happypaws.demo.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSize(MaxUploadSizeExceededException exception, Model model) {
        model.addAttribute("title", "Archivo demasiado grande");
        model.addAttribute("error", "El archivo que intentaste subir supera el tamaño máximo permitido (2MB).");
        return "views/errors/403";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException exception, Model model) {
        model.addAttribute("title", "No encontrado");
        model.addAttribute("error", exception.getMessage());
        return "views/errors/403";
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String handleBadRequest(Exception exception, Model model) {
        // Estas excepciones las lanza nuestro propio código con mensajes ya
        // pensados para el usuario final (ej: "Ya existe un usuario con ese email"),
        // por lo que es seguro mostrarlas tal cual.
        model.addAttribute("title", "No se pudo completar la acción");
        model.addAttribute("error", exception.getMessage());
        return "views/errors/403";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(DataIntegrityViolationException exception, Model model) {
        // Nunca mostramos exception.getMessage() aquí: contiene detalles internos
        // de la base de datos (nombres de constraints, tablas, SQL) que no deben
        // exponerse al usuario final.
        model.addAttribute("title", "No se pudo completar la acción");
        model.addAttribute("error", "El registro no se pudo guardar o eliminar porque está en uso por otros datos del sistema, o ya existe un registro igual.");
        return "views/errors/403";
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public String handleValidation(Exception exception, Model model) {
        model.addAttribute("title", "Existen errores de validación");
        model.addAttribute("error", "Revisa los datos ingresados e inténtalo nuevamente.");
        return "views/errors/403";
    }
}