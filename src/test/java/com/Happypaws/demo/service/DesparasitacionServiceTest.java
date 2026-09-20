package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Desparasitacion;
import com.Happypaws.demo.model.Pet;
import com.Happypaws.demo.repository.DesparasitacionRepository;
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
class DesparasitacionServiceTest {

    @Mock
    private DesparasitacionRepository desparasitacionRepository;

    @InjectMocks
    private DesparasitacionService desparasitacionService;

    @Test
    void listarPorMascotaId_delegaEnElRepositorioOrdenadoPorFecha() {
        Desparasitacion registro = new Desparasitacion();
        registro.setProducto("Drontal");
        registro.setFecha(LocalDate.now());

        when(desparasitacionRepository.findByMascotaIdMascotaOrderByFechaDesc(10L))
                .thenReturn(List.of(registro));

        List<Desparasitacion> resultado = desparasitacionService.listarPorMascotaId(10L);

        assertThat(resultado).containsExactly(registro);
    }

    @Test
    void guardar_asociaElRegistroALaMascotaYLoPersiste() {
        Pet mascota = new Pet();
        mascota.setId(1L);

        Desparasitacion registro = new Desparasitacion();
        registro.setMascota(mascota);
        registro.setProducto("Drontal");
        registro.setFecha(LocalDate.now());

        when(desparasitacionRepository.save(registro)).thenReturn(registro);

        Desparasitacion guardado = desparasitacionService.guardar(registro);

        assertThat(guardado.getMascota()).isEqualTo(mascota);
        verify(desparasitacionRepository).save(registro);
    }

    @Test
    void eliminar_conIdInexistente_lanzaExcepcion() {
        when(desparasitacionRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> desparasitacionService.eliminar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
