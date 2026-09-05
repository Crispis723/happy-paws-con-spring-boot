package com.Happypaws.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class RegisterRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank
    @Size(min = 8, max = 255)
    private String password;

    // NOTA DE SEGURIDAD: el campo "role" fue eliminado intencionalmente.
    // El endpoint /register es publico (permitAll en SecurityConfig), por lo que
    // jamas debe confiar en un rol enviado por el cliente: cualquiera podia
    // mandar role=ADMIN por fuera del formulario (curl/Postman) y autoasignarse
    // privilegios de administrador. El registro publico SIEMPRE crea CLIENTE.
    // Los roles ADMIN/VETERINARIO/RECEPCIONISTA solo deben asignarse desde el
    // panel /users o /roles, que ya estan protegidos con hasRole("ADMIN").


    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
