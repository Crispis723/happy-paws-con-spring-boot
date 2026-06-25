package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Role;
import com.Happypaws.demo.service.RoleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String listar(Model model) {
        List<Role> roles = roleService.listar();
        model.addAttribute("roles", roles);
        model.addAttribute("roleViews", roles.stream().map(this::toView).toList());
        return "views/roles/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("mode", "create");
        return "views/roles/action";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("role") Role role,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", role.getId() == null ? "create" : "edit");
            return "views/roles/action";
        }

        if (role.getId() == null) {
            roleService.guardar(role);
            redirectAttributes.addFlashAttribute("success", "Rol creado correctamente");
        } else {
            roleService.actualizar(role);
            redirectAttributes.addFlashAttribute("success", "Rol actualizado correctamente");
        }

        return "redirect:/roles";
    }

    @GetMapping("/edit/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Role role = roleService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        model.addAttribute("role", role);
        model.addAttribute("mode", "edit");
        return "views/roles/action";
    }

    @GetMapping("/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        roleService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Rol eliminado correctamente");
        return "redirect:/roles";
    }

    private RoleView toView(Role role) {
        String badgeClass = switch (role.getName()) {
            case "ADMIN" -> "role-badge--danger";
            case "VETERINARIO" -> "role-badge--primary";
            case "RECEPCIONISTA" -> "role-badge--success";
            case "CLIENTE" -> "role-badge--info";
            default -> "role-badge--neutral";
        };

        String icon = switch (role.getName()) {
            case "ADMIN" -> "bi-shield-lock";
            case "VETERINARIO" -> "bi-heart-pulse";
            case "RECEPCIONISTA" -> "bi-telephone";
            case "CLIENTE" -> "bi-person";
            default -> "bi-diagram-3";
        };

        String description = switch (role.getName()) {
            case "ADMIN" -> "Acceso total a usuarios, ventas, productos y configuración.";
            case "VETERINARIO" -> "Gestión de citas y atención clínica.";
            case "RECEPCIONISTA" -> "Registro operativo de clientes, mascotas y ventas.";
            case "CLIENTE" -> "Acceso básico para consultar y solicitar servicios.";
            default -> "Rol personalizado del sistema.";
        };

        String scope = switch (role.getName()) {
            case "ADMIN" -> "Administración completa";
            case "VETERINARIO" -> "Área clínica";
            case "RECEPCIONISTA" -> "Área operativa";
            case "CLIENTE" -> "Área pública";
            default -> "Acceso personalizado";
        };

        String color = switch (role.getName()) {
            case "ADMIN" -> "#e74c3c";
            case "VETERINARIO" -> "#58b8c7";
            case "RECEPCIONISTA" -> "#2fb36c";
            case "CLIENTE" -> "#0ea5b9";
            default -> "#6c757d";
        };

        return new RoleView(role.getId(), role.getName(), description, badgeClass, icon, scope, color);
    }

    private record RoleView(Long id, String name, String description, String badgeClass, String icon, String scope, String color) {
    }
}