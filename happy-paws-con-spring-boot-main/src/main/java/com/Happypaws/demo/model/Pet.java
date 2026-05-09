package com.Happypaws.demo.model;
import jakarta.persistence.*;

@Entity
@Table(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String species;
    private Integer age;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    // getters y setters
}

