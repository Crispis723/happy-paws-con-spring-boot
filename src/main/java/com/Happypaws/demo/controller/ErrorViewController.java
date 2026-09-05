package com.Happypaws.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * SecurityConfig configura accessDeniedPage("/error/403"), pero esa ruta
 * no tenía ningún controlador que la sirviera: un usuario sin permisos
 * terminaba viendo la Whitelabel Error Page de Spring en vez de la
 * pantalla de "acceso no autorizado" del sistema.
 */
@Controller
public class ErrorViewController {

    @GetMapping("/error/403")
    public String accesoDenegado(Model model) {
        model.addAttribute("title", "Acceso no autorizado");
        model.addAttribute("error", "No tienes permisos para ver esta sección.");
        return "views/errors/403";
    }
}
