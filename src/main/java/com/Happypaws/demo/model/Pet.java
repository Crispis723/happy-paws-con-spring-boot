package com.Happypaws.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pets")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "La especie es requerida")
    @Column(nullable = false)
    private String species;

    @Column(name = "raza")
    private String raza;

    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 100, message = "La edad debe ser realista")
    @Column(name = "edad")
    private Integer age;

    @Column(name = "peso")
    private Double peso;

    @Column(name = "color")
    private String color;

    @Column(name = "fecha_nacimiento")
    private String fechaNacimiento;

    @Column(name = "numero_microchip")
    private String numeroMicrochip;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "notas_medicas", columnDefinition = "TEXT")
    private String notasMedicas;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Appointment> appointments;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public String getNombreCompleto() {
        return String.format("%s (%s)", this.name, this.species);
    }
}

