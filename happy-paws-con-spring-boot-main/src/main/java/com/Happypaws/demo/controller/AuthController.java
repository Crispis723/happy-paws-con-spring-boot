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
    public String loginForm(Model model) {
        model.addAttribute("authOpen", true);
        model.addAttribute("authForm", "login");
        return "views/landing";
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
        model.addAttribute("authOpen", true);
        model.addAttribute("authForm", "login");

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
    public String registerUser(@ModelAttribute User user, Model model) {

        authService.saveUser(user);

        model.addAttribute("success", "Cuenta creada correctamente. Inicia sesión para continuar.");
        model.addAttribute("authOpen", true);
        model.addAttribute("authForm", "login");

        return "views/landing";
    }
}