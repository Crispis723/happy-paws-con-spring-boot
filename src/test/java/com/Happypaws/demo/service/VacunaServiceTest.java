package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Pet;
import com.Happypaws.demo.model.Vacuna;
import com.Happypaws.demo.repository.VacunaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacunaServiceTest {

    @Mock
    private VacunaRepository vacunaRepository;

    @InjectMocks
    private VacunaService vacunaService;

    @Test
    void listarPorMascotaId_delegaEnElRepositorioOrdenadoPorFecha() {
        Vacuna vacuna = new Vacuna();
        vacuna.setNombre("Rabia");
        vacuna.setFecha(LocalDate.now());

        when(vacunaRepository.findByMascotaIdMascotaOrderByFechaDesc(10L)).thenReturn(List.of(vacuna));

        List<Vacuna> resultado = vacunaService.listarPorMascotaId(10L);

        assertThat(resultado).containsExactly(vacuna);
    }

    @Test
    void guardar_asociaLaVacunaALaMascotaYLaPersiste() {
        Pet mascota = new Pet();
        mascota.setId(1L);

        Vacuna vacuna = new Vacuna();
        vacuna.setMascota(mascota);
        vacuna.setNombre("Rabia");
        vacuna.setFecha(LocalDate.now());

        when(vacunaRepository.save(vacuna)).thenReturn(vacuna);

        Vacuna guardada = vacunaService.guardar(vacuna);

        assertThat(guardada.getMascota()).isEqualTo(mascota);
        verify(vacunaRepository).save(vacuna);
    }

    @Test
    void eliminar_conIdInexistente_lanzaExcepcion() {
        when(vacunaRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> vacunaService.eliminar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
