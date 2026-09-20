package com.Happypaws.demo.controller;

import com.Happypaws.demo.service.ProfileService;
import java.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/perfil")
    public String perfil(Principal principal, Authentication authentication, Model model) {
        model.addAttribute("registro", profileService.buscarActual(principal));
        model.addAttribute("isClientUser", authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()))
                && authentication.getAuthorities().stream()
                .noneMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())
                        || "ROLE_VETERINARIO".equals(authority.getAuthority())
                        || "ROLE_RECEPCIONISTA".equals(authority.getAuthority())));
        return "views/autenticacion/perfil";
    }

    @PostMapping("/perfil")
    public String actualizar(Principal principal,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam(required = false) String password,
                             RedirectAttributes redirectAttributes) {
        profileService.actualizarActual(principal, name, email, password);
        redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
        return "redirect:/perfil";
    }
}