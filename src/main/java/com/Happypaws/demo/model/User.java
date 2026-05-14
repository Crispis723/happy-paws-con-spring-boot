package com.Happypaws.demo.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    private String name;
    private String email;
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;


}
