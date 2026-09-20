package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.service.CitaConfirmacionService;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Rutas públicas (sin login) para que un cliente confirme o cancele su
 * cita con un clic desde el correo de recordatorio. Ver
 * SecurityConfig: "/citas/confirmar/**" y "/citas/cancelar/**" están en
 * permitAll(), igual que "/reset-password".
 */
@Controller
public class CitaConfirmacionController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final CitaConfirmacionService citaConfirmacionService;

    public CitaConfirmacionController(CitaConfirmacionService citaConfirmacionService) {
        this.citaConfirmacionService = citaConfirmacionService;
    }

    @GetMapping("/citas/confirmar/{token}")
    public String confirmar(@PathVariable String token, Model model) {
        try {
            Appointment cita = citaConfirmacionService.confirmarPorToken(token);

            model.addAttribute("exito", true);
            model.addAttribute("titulo", "¡Cita confirmada!");
            model.addAttribute("mensaje", "Gracias por confirmar. Te esperamos con "
                    + nombreMascota(cita) + " el " + cita.getFechaHora().format(FORMATTER) + ".");

        } catch (IllegalArgumentException ex) {
            model.addAttribute("exito", false);
            model.addAttribute("titulo", "No se pudo confirmar la cita");
            model.addAttribute("mensaje", ex.getMessage());
        }

        return "views/citas/confirmacion-publica";
    }

    @GetMapping("/citas/cancelar/{token}")
    public String cancelar(@PathVariable String token, Model model) {
        try {
            Appointment cita = citaConfirmacionService.cancelarPorToken(token);

            model.addAttribute("exito", true);
            model.addAttribute("titulo", "Cita cancelada");
            model.addAttribute("mensaje", "Cancelamos la cita de " + nombreMascota(cita)
                    + " del " + cita.getFechaHora().format(FORMATTER)
                    + ". Si quieres reprogramar, contáctanos cuando quieras.");

        } catch (IllegalArgumentException ex) {
            model.addAttribute("exito", false);
            model.addAttribute("titulo", "No se pudo cancelar la cita");
            model.addAttribute("mensaje", ex.getMessage());
        }

        return "views/citas/confirmacion-publica";
    }

    private String nombreMascota(Appointment cita) {
        return cita.getMascota() != null ? cita.getMascota().getNombre() : "tu mascota";
    }
}
