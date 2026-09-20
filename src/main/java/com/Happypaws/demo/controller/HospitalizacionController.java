package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.ControlHospitalizacion;
import com.Happypaws.demo.model.EstadoHospitalizacion;
import com.Happypaws.demo.model.Hospitalizacion;
import com.Happypaws.demo.model.User;
import com.Happypaws.demo.service.HospitalizacionService;
import com.Happypaws.demo.service.PetService;
import com.Happypaws.demo.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/hospitalizacion")
public class HospitalizacionController {

    private final HospitalizacionService hospitalizacionService;
    private final PetService petService;
    private final UserService userService;

    public HospitalizacionController(HospitalizacionService hospitalizacionService,
                                      PetService petService,
                                      UserService userService) {
        this.hospitalizacionService = hospitalizacionService;
        this.petService = petService;
        this.userService = userService;
    }

    @GetMapping
    public String listar(
            Model model,
            @RequestParam(name = "vista", defaultValue = "activas") String vista,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        model.addAttribute("vista", vista);

        if ("historial".equals(vista)) {
            Page<Hospitalizacion> historial = hospitalizacionService.listarHistorial(
                    PageRequest.of(Math.max(page, 0), 20));
            model.addAttribute("historial", historial.getContent());
            model.addAttribute("currentPage", historial.getNumber());
            model.addAttribute("totalPages", historial.getTotalPages());
            model.addAttribute("totalItems", historial.getTotalElements());
            model.addAttribute("pageSize", 20);
        } else {
            model.addAttribute("activas", hospitalizacionService.listarActivas());
        }

        return "views/hospitalizacion/index";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("hospitalizacion", new Hospitalizacion());
        aplicarContexto(model);
        return "views/hospitalizacion/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("hospitalizacion") Hospitalizacion hospitalizacion,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            aplicarContexto(model);
            return "views/hospitalizacion/formulario";
        }

        Hospitalizacion guardada = hospitalizacionService.ingresar(hospitalizacion);

        redirectAttributes.addFlashAttribute("success", "Ingreso registrado correctamente");
        return "redirect:/hospitalizacion/" + guardada.getId();
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        Hospitalizacion hospitalizacion = hospitalizacionService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Hospitalización no encontrada"));

        model.addAttribute("hospitalizacion", hospitalizacion);
        model.addAttribute("signosVitales", hospitalizacionService.obtenerUltimosSignosVitales(hospitalizacion));
        model.addAttribute("estados", EstadoHospitalizacion.values());

        return "views/hospitalizacion/show";
    }

    @PostMapping("/{id}/controles/guardar")
    public String guardarControl(
            @PathVariable Long id,
            @ModelAttribute("control") ControlHospitalizacion control,
            @RequestParam(name = "nuevoEstado", required = false) String nuevoEstadoParam,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        if (auth != null) {
            userService.buscarPorEmail(auth.getName()).ifPresent(control::setRegistradoPor);
        }

        // El <select> del formulario incluye la opción "No cambiar" con
        // valor vacío; Spring no puede convertir un string vacío
        // directamente a un enum, así que se resuelve manualmente aquí.
        EstadoHospitalizacion nuevoEstado = (nuevoEstadoParam != null && !nuevoEstadoParam.isBlank())
                ? EstadoHospitalizacion.valueOf(nuevoEstadoParam)
                : null;

        try {
            hospitalizacionService.registrarControl(id, control, nuevoEstado);
            redirectAttributes.addFlashAttribute("success", "Control registrado correctamente");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/hospitalizacion/" + id;
    }

    @PostMapping("/{id}/cerrar")
    public String cerrar(
            @PathVariable Long id,
            @RequestParam("estadoFinal") EstadoHospitalizacion estadoFinal,
            @RequestParam(name = "observacionesAlta", required = false) String observacionesAlta,
            RedirectAttributes redirectAttributes) {

        try {
            hospitalizacionService.cerrar(id, estadoFinal, observacionesAlta);
            redirectAttributes.addFlashAttribute("success", "Hospitalización cerrada correctamente");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/hospitalizacion/" + id;
    }

    private void aplicarContexto(Model model) {
        model.addAttribute("mascotas", petService.listar());
        List<User> veterinarios = userService.listarVeterinarios();
        model.addAttribute("veterinarios", veterinarios);
    }
}
