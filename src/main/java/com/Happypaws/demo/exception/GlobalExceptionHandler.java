package com.Happypaws.demo.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException exception, Model model) {
        model.addAttribute("error", exception.getMessage());
        return "views/errors/403";
    }

    @ExceptionHandler({IllegalArgumentException.class, DataIntegrityViolationException.class})
    public String handleBadRequest(Exception exception, Model model) {
        model.addAttribute("error", exception.getMessage());
        return "views/errors/403";
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public String handleValidation(Exception exception, Model model) {
        Map<String, String> errors = new HashMap<>();
        model.addAttribute("validationErrors", errors);
        model.addAttribute("error", "Existen errores de validación");
        return "views/errors/403";
    }
}