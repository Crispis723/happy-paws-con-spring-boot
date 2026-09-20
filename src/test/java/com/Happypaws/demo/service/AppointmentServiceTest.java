package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.model.EstadoCita;
import com.Happypaws.demo.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentReminderService reminderService;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void actualizar_sinEstadoEnElObjetoEntrante_conservaElEstadoActual() {
        Appointment existente = new Appointment();
        existente.setIdCita(1L);
        existente.setEstado(EstadoCita.CONFIRMADA);

        // El formulario de edición reconstruye la cita desde cero y nunca
        // toca "estado", así que el objeto que llega a actualizar() trae
        // null en ese campo.
        Appointment entrante = new Appointment();
        entrante.setIdCita(1L);
        entrante.setEstado(null);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(appointmentRepository.save(entrante)).thenReturn(entrante);

        appointmentService.actualizar(entrante);

        assertThat(entrante.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
    }

    @Test
    void actualizar_conCitaInexistente_lanzaExcepcion() {
        Appointment entrante = new Appointment();
        entrante.setIdCita(99L);

        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.actualizar(entrante))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void cambiarEstado_deAgendadaAConfirmada_funciona() {
        Appointment cita = new Appointment();
        cita.setIdCita(1L);
        cita.setEstado(EstadoCita.AGENDADA);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(appointmentRepository.save(cita)).thenReturn(cita);

        Appointment resultado = appointmentService.cambiarEstado(1L, EstadoCita.CONFIRMADA);

        assertThat(resultado.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
        verify(reminderService, never()).enviarNotificacionCancelacion(any(), anyString());
    }

    @Test
    void cambiarEstado_aCancelada_envianNotificacionDeCancelacion() {
        Appointment cita = new Appointment();
        cita.setIdCita(1L);
        cita.setEstado(EstadoCita.AGENDADA);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(appointmentRepository.save(cita)).thenReturn(cita);

        appointmentService.cambiarEstado(1L, EstadoCita.CANCELADA);

        verify(reminderService).enviarNotificacionCancelacion(any(Appointment.class), anyString());
    }

    @Test
    void cambiarEstado_desdeUnEstadoFinal_lanzaExcepcion() {
        Appointment cita = new Appointment();
        cita.setIdCita(1L);
        cita.setEstado(EstadoCita.ATENDIDA);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(cita));

        assertThatThrownBy(() -> appointmentService.cambiarEstado(1L, EstadoCita.CONFIRMADA))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cambiarEstado_alMismoEstadoFinal_noLanzaExcepcion() {
        Appointment cita = new Appointment();
        cita.setIdCita(1L);
        cita.setEstado(EstadoCita.CANCELADA);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(appointmentRepository.save(cita)).thenReturn(cita);

        Appointment resultado = appointmentService.cambiarEstado(1L, EstadoCita.CANCELADA);

        assertThat(resultado.getEstado()).isEqualTo(EstadoCita.CANCELADA);
    }
}
