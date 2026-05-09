package com.Happypaws.demo.controller;

import com.Happypaws.demo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/login")
    public String loginForm() {
        return "views/autenticacion/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        if (authService.login(email, password)) {
            session.setAttribute("userEmail", email);
            return "redirect:/dashboard";
        }

        model.addAttribute("error", "Credenciales inválidas");
        model.addAttribute("email", email);
        return "views/autenticacion/login";
    }
}