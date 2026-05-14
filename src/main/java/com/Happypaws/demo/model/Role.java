package com.Happypaws.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del rol es requerido")
    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @OneToMany(mappedBy = "role")
    private java.util.List<User> usuarios;

    public enum RoleType {
        ADMIN("Administrador"),
        VETERINARIO("Veterinario"),
        RECEPCIONISTA("Recepcionista"),
        CLIENTE("Cliente");

        private final String displayName;

        RoleType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public String getDisplayName() {
        try {
            return RoleType.valueOf(this.name).getDisplayName();
        } catch (IllegalArgumentException e) {
            return this.name;
        }
    }
}

