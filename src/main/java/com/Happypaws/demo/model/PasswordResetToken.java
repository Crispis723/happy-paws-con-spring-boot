package com.Happypaws.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token")
    private Long idToken;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private Boolean used = false;


    public Long getIdToken() {
        return this.idToken;
    }

    public String getToken() {
        return this.token;
    }

    public User getUser() {
        return this.user;
    }

    public LocalDateTime getExpiryDate() {
        return this.expiryDate;
    }

    public Boolean getUsed() {
        return this.used;
    }

    public void setId(Long id) {
        this.idToken = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }


    public void setIdToken(Long id) { this.idToken = id; }

    /** Compatibilidad de API: el identificador persistido es idToken. */
    public Long getId() { return idToken; }
}