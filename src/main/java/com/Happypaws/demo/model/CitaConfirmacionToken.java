package com.Happypaws.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Token de un solo uso que se envía en el correo de recordatorio de cita
 * para que el cliente pueda confirmar o cancelar su asistencia con un
 * solo clic, sin necesidad de iniciar sesión (mismo patrón que
 * {@link PasswordResetToken}).
 *
 * Un mismo token sirve tanto para el enlace "Confirmar" como para el
 * enlace "Cancelar" del correo; lo que decide la acción es la ruta a la
 * que apunta cada botón (/citas/confirmar/{token} vs
 * /citas/cancelar/{token}), no el token en sí. Una vez usado en
 * cualquiera de las dos acciones, queda inválido para evitar que el
 * mismo enlace se reutilice después.
 */
@Entity
@Table(name = "cita_confirmacion_tokens")
public class CitaConfirmacionToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token")
    private Long idToken;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita", nullable = false)
    private Appointment cita;

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

    public Appointment getCita() {
        return this.cita;
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

    public void setCita(Appointment cita) {
        this.cita = cita;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }
}
