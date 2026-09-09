package com.Happypaws.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotBlank;
public class ForgotPasswordRequest {
    @NotBlank
    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es requerido")
    private String email;


    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}