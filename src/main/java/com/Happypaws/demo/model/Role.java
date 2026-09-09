package com.Happypaws.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @NotBlank @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Size(max = 150)
    @Column(length = 150)
    private String description;

    @Column(nullable = false)
    private Boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions",
        joinColumns = @JoinColumn(name = "id_rol", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "id_permiso", nullable = false))
    private Set<Permission> permissions = new HashSet<>();

    public Role() {}
    public Role(String name) { this.name = name; }
    public Role(Long id, String name) { this.idRol = id; this.name = name; }
    public Long getIdRol() { return idRol; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }
    public void addPermission(Permission permission) { this.permissions.add(permission); }
    public void removePermission(Permission permission) { this.permissions.remove(permission); }

    public void setIdRol(Long id) { this.idRol = id; }

    /** Compatibilidad de API: el identificador persistido es idRol. */
    public Long getId() { return idRol; }
}
