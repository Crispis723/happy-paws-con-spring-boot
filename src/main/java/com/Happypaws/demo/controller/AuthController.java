package com.Happypaws.demo.controller;

import com.Happypaws.demo.service.AuthService;
import com.Happypaws.demo.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;




@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String landing() {
        return "views/landing";
    }

    // ================= LOGIN =================

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        model.addAttribute("authOpen", true);
        model.addAttribute("authForm", "login");
        if (error != null) {
            model.addAttribute("error", "Credenciales inválidas");
        }
        if (logout != null) {
            model.addAttribute("success", "Sesión cerrada correctamente");
        }
        return "views/landing";
    }

    // ================= REGISTER =================

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("authOpen", true);
        model.addAttribute("authForm", "register");
        return "views/landing";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest, org.springframework.validation.BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("authOpen", true);
            model.addAttribute("authForm", "register");
            return "views/landing";
        }

        authService.registrar(registerRequest);

        redirectAttributes.addFlashAttribute("success", "Cuenta creada correctamente. Inicia sesión para continuar.");
        return "redirect:/login";
    }
}