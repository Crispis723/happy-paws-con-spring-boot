package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.model.Appointment.EstadoCita;
import com.Happypaws.demo.service.AppointmentService;
import com.Happypaws.demo.service.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/citas")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final VeterinarioService veterinarioService;

    public AppointmentController(AppointmentService appointmentService, VeterinarioService veterinarioService) {
        this.appointmentService = appointmentService;
        this.veterinarioService = veterinarioService;
    }

    /**
     * Listar todas las citas (según rol)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public String index(Model model, Authentication authentication) {
        List<Appointment> citas;
        
        // Si es veterinario, mostrar solo sus citas
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VETERINARIO"))) {
            // TODO: Obtener ID del veterinario autenticado
            citas = appointmentService.listarTodas(); // Por ahora mostrar todas
        } else {
            // Admin y recepcionista ven todas
            citas = appointmentService.listarTodas();
        }
        
        model.addAttribute("citas", citas);
        model.addAttribute("totalCitas", citas.size());
        return "views/citas/index";
    }

    /**
     * Ver detalles de una cita
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public String show(@PathVariable Long id, Model model) {
        Optional<Appointment> cita = appointmentService.buscarPorId(id);
        
        if (cita.isEmpty()) {
            throw new IllegalArgumentException("Cita no encontrada");
        }
        
        model.addAttribute("cita", cita.get());
        return "views/citas/show";
    }

    /**
     * Formulario para crear nueva cita
     */
    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public String createForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("veterinarios", veterinarioService.listarActivos());
        return "views/citas/create";
    }

    /**
     * Guardar nueva cita
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public String store(@Valid @ModelAttribute Appointment appointment,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (result.hasErrors()) {
            model.addAttribute("veterinarios", veterinarioService.listarActivos());
            return "views/citas/create";
        }

        try {
            appointmentService.guardar(appointment);
            redirectAttributes.addFlashAttribute("success", "Cita creada exitosamente");
            return "redirect:/citas";
        } catch (Exception e) {
            result.reject("error.general", "Error al guardar: " + e.getMessage());
            model.addAttribute("veterinarios", veterinarioService.listarActivos());
            return "views/citas/create";
        }
    }

    /**
     * Formulario para editar cita
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Appointment> cita = appointmentService.buscarPorId(id);
        
        if (cita.isEmpty()) {
            throw new IllegalArgumentException("Cita no encontrada");
        }
        
        model.addAttribute("appointment", cita.get());
        model.addAttribute("veterinarios", veterinarioService.listarActivos());
        model.addAttribute("estados", EstadoCita.values());
        return "views/citas/edit";
    }

    /**
     * Actualizar cita
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute Appointment appointment,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (result.hasErrors()) {
            model.addAttribute("veterinarios", veterinarioService.listarActivos());
            model.addAttribute("estados", EstadoCita.values());
            return "views/citas/edit";
        }

        try {
            appointmentService.actualizar(id, appointment);
            redirectAttributes.addFlashAttribute("success", "Cita actualizada exitosamente");
            return "redirect:/citas/" + id;
        } catch (Exception e) {
            result.reject("error.general", "Error al actualizar: " + e.getMessage());
            model.addAttribute("veterinarios", veterinarioService.listarActivos());
            model.addAttribute("estados", EstadoCita.values());
            return "views/citas/edit";
        }
    }

    /**
     * Cambiar estado de cita (AJAX)
     */
    @PostMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public String cambiarEstado(@PathVariable Long id,
                               @RequestParam String estado,
                               RedirectAttributes redirectAttributes) {
        try {
            EstadoCita nuevoEstado = EstadoCita.valueOf(estado.toUpperCase());
            appointmentService.cambiarEstado(id, nuevoEstado);
            redirectAttributes.addFlashAttribute("success", "Estado de cita actualizado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar estado: " + e.getMessage());
        }
        return "redirect:/citas/" + id;
    }

    /**
     * Eliminar cita
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String delete(@PathVariable Long id,
                        RedirectAttributes redirectAttributes) {
        try {
            appointmentService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Cita eliminada exitosamente");
            return "redirect:/citas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
            return "redirect:/citas/" + id;
        }
    }

    /**
     * Citas de un veterinario
     */
    @GetMapping("/veterinario/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public String citasVeterinario(@PathVariable Long id, Model model) {
        List<Appointment> citas = appointmentService.citasPorVeterinario(id);
        model.addAttribute("citas", citas);
        model.addAttribute("veterinarioId", id);
        return "views/citas/lista-veterinario";
    }

    /**
     * Citas de hoy para un veterinario
     */
    @GetMapping("/veterinario/{id}/hoy")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public String citasVeterinarioHoy(@PathVariable Long id, Model model) {
        List<Appointment> citas = appointmentService.citasDeHoy(id);
        model.addAttribute("citas", citas);
        model.addAttribute("veterinarioId", id);
        model.addAttribute("titulo", "Citas de Hoy");
        return "views/citas/lista-veterinario";
    }
}
