package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Veterinario;
import com.Happypaws.demo.service.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    /**
     * Listar todos los veterinarios activos
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA', 'VETERINARIO')")
    public String index(Model model) {
        List<Veterinario> veterinarios = veterinarioService.listarActivos();
        model.addAttribute("veterinarios", veterinarios);
        model.addAttribute("totalVeterinarios", veterinarios.size());
        return "views/veterinarios/index";
    }

    /**
     * Formulario para crear nuevo veterinario
     */
    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("veterinario", new Veterinario());
        model.addAttribute("isEditing", false);
        return "views/veterinarios/create";
    }

    /**
     * Guardar nuevo veterinario
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String store(@Valid @ModelAttribute Veterinario veterinario,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "views/veterinarios/create";
        }

        try {
            // Validar email único
            if (veterinarioService.existeEmail(veterinario.getEmail())) {
                result.rejectValue("email", "error.email", "El email ya está registrado");
                return "views/veterinarios/create";
            }

            // Validar cédula única
            if (veterinarioService.existeCedula(veterinario.getCedula())) {
                result.rejectValue("cedula", "error.cedula", "La cédula ya está registrada");
                return "views/veterinarios/create";
            }

            veterinarioService.guardar(veterinario);
            redirectAttributes.addFlashAttribute("success", "Veterinario creado exitosamente");
            return "redirect:/veterinarios";
        } catch (Exception e) {
            result.reject("error.general", "Error al guardar: " + e.getMessage());
            return "views/veterinarios/create";
        }
    }

    /**
     * Ver detalles de un veterinario
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA', 'VETERINARIO')")
    public String show(@PathVariable Long id, Model model) {
        Optional<Veterinario> veterinario = veterinarioService.buscarPorId(id);
        
        if (veterinario.isEmpty()) {
            throw new IllegalArgumentException("Veterinario no encontrado");
        }

        model.addAttribute("veterinario", veterinario.get());
        return "views/veterinarios/show";
    }

    /**
     * Formulario para editar veterinario
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Veterinario> veterinario = veterinarioService.buscarPorId(id);
        
        if (veterinario.isEmpty()) {
            throw new IllegalArgumentException("Veterinario no encontrado");
        }

        model.addAttribute("veterinario", veterinario.get());
        model.addAttribute("isEditing", true);
        return "views/veterinarios/edit";
    }

    /**
     * Actualizar veterinario
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute Veterinario veterinario,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "views/veterinarios/edit";
        }

        try {
            veterinarioService.actualizar(id, veterinario);
            redirectAttributes.addFlashAttribute("success", "Veterinario actualizado exitosamente");
            return "redirect:/veterinarios/" + id;
        } catch (Exception e) {
            result.reject("error.general", "Error al actualizar: " + e.getMessage());
            return "views/veterinarios/edit";
        }
    }

    /**
     * Eliminar veterinario (soft delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String delete(@PathVariable Long id,
                        RedirectAttributes redirectAttributes) {
        try {
            veterinarioService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Veterinario desactivado exitosamente");
            return "redirect:/veterinarios";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar: " + e.getMessage());
            return "redirect:/veterinarios";
        }
    }

    /**
     * API: Buscar veterinarios activos
     */
    @GetMapping("/api/activos")
    @ResponseBody
    public List<Veterinario> apiActivos() {
        return veterinarioService.listarActivos();
    }

    /**
     * API: Buscar veterinarios por especialidad
     */
    @GetMapping("/api/especialidad/{especialidad}")
    @ResponseBody
    public List<Veterinario> apiPorEspecialidad(@PathVariable String especialidad) {
        return veterinarioService.buscarPorEspecialidad(especialidad);
    }

    /**
     * API: Buscar veterinarios
     */
    @GetMapping("/api/buscar")
    @ResponseBody
    public List<Veterinario> apiBuscar(@RequestParam String q) {
        return veterinarioService.buscar(q);
    }
}
