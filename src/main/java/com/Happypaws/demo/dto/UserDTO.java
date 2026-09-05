package com.Happypaws.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
public class UserDTO {

    private Long id;

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @Size(min = 8, max = 255)
    private String password;

    @NotNull
    private Boolean enabled = true;

    private List<Long> roleIds = new ArrayList<>();


    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public List<Long> getRoleIds() {
        return this.roleIds;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

}