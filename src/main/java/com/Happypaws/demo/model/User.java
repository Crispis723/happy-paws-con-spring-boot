package com.Happypaws.demo.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity// Le dice a Spring:
//  👉 “esto representa una tabla MySQL
@Table(name = "users") /// nombre  de la tabla

//Setter y Getter
@Getter
@Setter
public class User {
    @Id  // llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Hace que MySQL maneje el AUTO_INCREMENT.
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(min = 8, max = 255)
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new LinkedHashSet<>();


}
