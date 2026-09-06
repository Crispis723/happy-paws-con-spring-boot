package com.Happypaws.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication){
        if (tieneRol(authentication, "ROLE_CLIENTE")) {
            return "redirect:/dashboard/cliente";
        }
        return "redirect:/dashboard/staff";
    }

    @GetMapping("/dashboard/cliente")
    public String dashboardCliente() {
        return "views/dashboard/public";
    }

    @GetMapping({"/dashboard/staff", "/dashboard/colaboradores"})
    public String dashboardStaff() {
        return "views/dashboard/staff";
    }

    @GetMapping("/error/403")
    public String accessDenied() {
        return "views/errors/403";
    }

    private boolean tieneRol(Authentication authentication, String role) {
        return authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }
}