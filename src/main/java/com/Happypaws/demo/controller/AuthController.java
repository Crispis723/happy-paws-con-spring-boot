package com.Happypaws.demo.controller;

import com.Happypaws.demo.service.AuthService;
import com.Happypaws.demo.service.PasswordResetService;
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
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
    this.authService = authService;
    this.passwordResetService = passwordResetService;
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
        if (error != null) {
            model.addAttribute("error", "Credenciales inválidas");
        }
        if (logout != null) {
            model.addAttribute("success", "Sesión cerrada correctamente");
        }
        return "views/autenticacion/login";
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
    // ================= FORGOT PASSWORD =================

@GetMapping("/forgot-password")
public String forgotPasswordForm() {
    return "views/autenticacion/forgot-password";
}

@PostMapping("/forgot-password")
public String forgotPasswordSubmit(@RequestParam String email, RedirectAttributes redirectAttributes) {
    passwordResetService.solicitarRecuperacion(email);
    redirectAttributes.addFlashAttribute("success",
        "Si el correo existe en nuestro sistema, te enviamos un enlace para restablecer tu contraseña.");
    return "redirect:/login";
}

// ================= RESET PASSWORD =================

@GetMapping("/reset-password")
public String resetPasswordForm(@RequestParam String token, Model model) {
    if (!passwordResetService.tokenValido(token)) {
        model.addAttribute("error", "El enlace es inválido o ya expiró.");
        model.addAttribute("tokenInvalido", true);
    }
    model.addAttribute("token", token);
    return "views/autenticacion/reset-password";
}

@PostMapping("/reset-password")
public String resetPasswordSubmit(@RequestParam String token,
                                   @RequestParam String password,
                                   @RequestParam String confirmPassword,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
    if (!password.equals(confirmPassword)) {
        model.addAttribute("error", "Las contraseñas no coinciden");
        model.addAttribute("token", token);
        return "views/autenticacion/reset-password";
    }
    try {
        passwordResetService.restablecerPassword(token, password);
        redirectAttributes.addFlashAttribute("success", "Contraseña actualizada. Ya puedes iniciar sesión.");
        return "redirect:/login";
    } catch (IllegalArgumentException e) {
        model.addAttribute("error", e.getMessage());
        model.addAttribute("token", token);
        return "views/autenticacion/reset-password";
    }
}
}
