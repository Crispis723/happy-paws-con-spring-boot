package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.User;
import com.Happypaws.demo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    // ================= LOGIN =================

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
            return "redirect:views/dashboard";
        }

        model.addAttribute("error", "Credenciales inválidas");
        model.addAttribute("email", email);

        return "views/autenticacion/login";
    }

    // ================= REGISTER =================

    @GetMapping("/register")
    public String registerPage() {
        return "views/autenticacion/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {

        authService.saveUser(user);

        return "redirect:/login";
    }
}