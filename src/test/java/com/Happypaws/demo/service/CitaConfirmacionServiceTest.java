package com.Happypaws.demo.service;

import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.model.CitaConfirmacionToken;
import com.Happypaws.demo.model.EstadoCita;
import com.Happypaws.demo.repository.AppointmentRepository;
import com.Happypaws.demo.repository.CitaConfirmacionTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitaConfirmacionServiceTest {

    @Mock
    private CitaConfirmacionTokenRepository tokenRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private CitaConfirmacionService citaConfirmacionService;

    private Appointment citaConEmail(EstadoCita estado) {
        Cliente cliente = new Cliente();
        cliente.setEmail("ana@example.com");
        cliente.setRazonSocial("Ana");

        Appointment cita = new Appointment();
        cita.setIdCita(1L);
        cita.setCliente(cliente);
        cita.setFechaHora(LocalDateTime.now().plusDays(1));
        cita.setMotivo("Control");
        cita.setEstado(estado);
        return cita;
    }

    @Test
    void generarToken_invalidaTokensAnterioresYCreaUnoNuevo() {
        Appointment cita = citaConEmail(EstadoCita.AGENDADA);

        when(tokenRepository.save(any(CitaConfirmacionToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String token = citaConfirmacionService.generarToken(cita);

        assertThat(token).isNotBlank();
        verify(tokenRepository).invalidarTokensAnteriores(1L);
    }

    @Test
    void confirmarPorToken_moviendoUnaCitaAgendada_laConfirmaYNotifica() {
        Appointment cita = citaConEmail(EstadoCita.AGENDADA);

        CitaConfirmacionToken token = new CitaConfirmacionToken();
        token.setToken("abc");
        token.setCita(cita);
        token.setUsed(false);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken("abc")).thenReturn(Optional.of(token));
        when(appointmentRepository.save(cita)).thenReturn(cita);

        Appointment resultado = citaConfirmacionService.confirmarPorToken("abc");

        assertThat(resultado.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
        assertThat(token.getUsed()).isTrue();
        verify(emailService).enviarCorreoHTML(anyString(), anyString(), anyString());
    }

    @Test
    void cancelarPorToken_moviendoUnaCitaAgendada_laCancelaYNotifica() {
        Appointment cita = citaConEmail(EstadoCita.AGENDADA);

        CitaConfirmacionToken token = new CitaConfirmacionToken();
        token.setToken("abc");
        token.setCita(cita);
        token.setUsed(false);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken("abc")).thenReturn(Optional.of(token));
        when(appointmentRepository.save(cita)).thenReturn(cita);

        Appointment resultado = citaConfirmacionService.cancelarPorToken("abc");

        assertThat(resultado.getEstado()).isEqualTo(EstadoCita.CANCELADA);
        verify(emailService).enviarCorreoHTML(anyString(), anyString(), anyString());
    }

    @Test
    void confirmarPorToken_conTokenYaUsado_lanzaExcepcion() {
        CitaConfirmacionToken token = new CitaConfirmacionToken();
        token.setToken("abc");
        token.setUsed(true);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken("abc")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> citaConfirmacionService.confirmarPorToken("abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya fue utilizado");
    }

    @Test
    void confirmarPorToken_conTokenExpirado_lanzaExcepcion() {
        CitaConfirmacionToken token = new CitaConfirmacionToken();
        token.setToken("abc");
        token.setUsed(false);
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        when(tokenRepository.findByToken("abc")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> citaConfirmacionService.confirmarPorToken("abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("expiró");
    }

    @Test
    void confirmarPorToken_conTokenInexistente_lanzaExcepcion() {
        when(tokenRepository.findByToken("no-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> citaConfirmacionService.confirmarPorToken("no-existe"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void confirmarPorToken_conCitaYaEnEstadoFinal_lanzaExcepcionYNoLaModifica() {
        Appointment cita = citaConEmail(EstadoCita.CANCELADA);

        CitaConfirmacionToken token = new CitaConfirmacionToken();
        token.setToken("abc");
        token.setCita(cita);
        token.setUsed(false);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken("abc")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> citaConfirmacionService.confirmarPorToken("abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cancelada");

        verify(appointmentRepository, org.mockito.Mockito.never()).save(any());
    }
}
