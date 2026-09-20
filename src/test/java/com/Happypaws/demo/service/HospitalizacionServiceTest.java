package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.ControlHospitalizacion;
import com.Happypaws.demo.model.EstadoHospitalizacion;
import com.Happypaws.demo.model.Hospitalizacion;
import com.Happypaws.demo.model.Pet;
import com.Happypaws.demo.repository.ControlHospitalizacionRepository;
import com.Happypaws.demo.repository.HospitalizacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HospitalizacionServiceTest {

    @Mock
    private HospitalizacionRepository repository;

    @Mock
    private ControlHospitalizacionRepository controlRepository;

    @InjectMocks
    private HospitalizacionService hospitalizacionService;

    private Hospitalizacion hospitalizacionActiva() {
        Pet mascota = new Pet();
        mascota.setId(1L);
        mascota.setNombre("Max");

        Hospitalizacion h = new Hospitalizacion();
        h.setId(10L);
        h.setMascota(mascota);
        h.setJaula("A-03");
        h.setFechaIngreso(LocalDateTime.now().minusDays(1));
        h.setEstado(EstadoHospitalizacion.ESTABLE);
        h.setPesoIngreso(new BigDecimal("22.4"));
        h.setTemperaturaIngreso(new BigDecimal("38.5"));
        return h;
    }

    @Test
    void ingresar_fijaEstadoInicialYFechaDeIngresoPorDefecto() {
        Hospitalizacion nueva = new Hospitalizacion();

        when(repository.save(nueva)).thenReturn(nueva);

        Hospitalizacion resultado = hospitalizacionService.ingresar(nueva);

        assertThat(resultado.getEstado()).isEqualTo(EstadoHospitalizacion.EN_OBSERVACION);
        assertThat(resultado.getFechaIngreso()).isNotNull();
        assertThat(resultado.getFechaAlta()).isNull();
    }

    @Test
    void registrarControl_sobreHospitalizacionActiva_loGuardaYActualizaEstado() {
        Hospitalizacion h = hospitalizacionActiva();
        ControlHospitalizacion control = new ControlHospitalizacion();

        when(repository.findById(10L)).thenReturn(Optional.of(h));
        when(controlRepository.save(control)).thenReturn(control);
        when(repository.save(h)).thenReturn(h);

        hospitalizacionService.registrarControl(10L, control, EstadoHospitalizacion.CRITICO);

        assertThat(control.getHospitalizacion()).isEqualTo(h);
        assertThat(control.getFechaHora()).isNotNull();
        assertThat(h.getEstado()).isEqualTo(EstadoHospitalizacion.CRITICO);
        verify(repository).save(h);
    }

    @Test
    void registrarControl_sinCambiarElEstado_noTocaElEstadoActual() {
        Hospitalizacion h = hospitalizacionActiva();
        ControlHospitalizacion control = new ControlHospitalizacion();

        when(repository.findById(10L)).thenReturn(Optional.of(h));
        when(controlRepository.save(control)).thenReturn(control);

        hospitalizacionService.registrarControl(10L, control, null);

        assertThat(h.getEstado()).isEqualTo(EstadoHospitalizacion.ESTABLE);
        verify(repository, never()).save(any(Hospitalizacion.class));
    }

    @Test
    void registrarControl_sobreHospitalizacionCerrada_lanzaExcepcion() {
        Hospitalizacion h = hospitalizacionActiva();
        h.setEstado(EstadoHospitalizacion.DADO_DE_ALTA);
        h.setFechaAlta(LocalDateTime.now());

        when(repository.findById(10L)).thenReturn(Optional.of(h));

        assertThatThrownBy(() -> hospitalizacionService.registrarControl(10L, new ControlHospitalizacion(), null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(controlRepository, never()).save(any());
    }

    @Test
    void cerrar_conEstadoFinalValido_cierraLaHospitalizacion() {
        Hospitalizacion h = hospitalizacionActiva();

        when(repository.findById(10L)).thenReturn(Optional.of(h));
        when(repository.save(h)).thenReturn(h);

        Hospitalizacion resultado = hospitalizacionService.cerrar(10L, EstadoHospitalizacion.DADO_DE_ALTA, "Recuperado, indicar reposo 3 días");

        assertThat(resultado.getEstado()).isEqualTo(EstadoHospitalizacion.DADO_DE_ALTA);
        assertThat(resultado.getFechaAlta()).isNotNull();
        assertThat(resultado.getObservacionesAlta()).contains("reposo");
    }

    @Test
    void cerrar_conUnEstadoQueNoEsFinal_lanzaExcepcion() {
        Hospitalizacion h = hospitalizacionActiva();

        when(repository.findById(10L)).thenReturn(Optional.of(h));

        assertThatThrownBy(() -> hospitalizacionService.cerrar(10L, EstadoHospitalizacion.ESTABLE, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cerrar_unaHospitalizacionYaCerrada_lanzaExcepcion() {
        Hospitalizacion h = hospitalizacionActiva();
        h.setEstado(EstadoHospitalizacion.FALLECIDO);
        h.setFechaAlta(LocalDateTime.now());

        when(repository.findById(10L)).thenReturn(Optional.of(h));

        assertThatThrownBy(() -> hospitalizacionService.cerrar(10L, EstadoHospitalizacion.DADO_DE_ALTA, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void obtenerUltimosSignosVitales_conControlesRegistrados_usaElMasReciente() {
        Hospitalizacion h = hospitalizacionActiva();

        ControlHospitalizacion ultimoControl = new ControlHospitalizacion();
        ultimoControl.setPeso(new BigDecimal("21.0"));
        ultimoControl.setTemperatura(new BigDecimal("39.0"));
        ultimoControl.setFechaHora(LocalDateTime.now());

        when(controlRepository.findByHospitalizacionIdHospitalizacionOrderByFechaHoraDesc(10L))
                .thenReturn(List.of(ultimoControl));

        var signos = hospitalizacionService.obtenerUltimosSignosVitales(h);

        assertThat(signos.peso()).isEqualByComparingTo("21.0");
        assertThat(signos.temperatura()).isEqualByComparingTo("39.0");
    }

    @Test
    void obtenerUltimosSignosVitales_sinControles_caeALosDeIngreso() {
        Hospitalizacion h = hospitalizacionActiva();

        when(controlRepository.findByHospitalizacionIdHospitalizacionOrderByFechaHoraDesc(10L))
                .thenReturn(List.of());

        var signos = hospitalizacionService.obtenerUltimosSignosVitales(h);

        assertThat(signos.peso()).isEqualByComparingTo("22.4");
        assertThat(signos.temperatura()).isEqualByComparingTo("38.5");
    }

    @Test
    void registrarControl_conHospitalizacionInexistente_lanzaExcepcion() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hospitalizacionService.registrarControl(999L, new ControlHospitalizacion(), null))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
