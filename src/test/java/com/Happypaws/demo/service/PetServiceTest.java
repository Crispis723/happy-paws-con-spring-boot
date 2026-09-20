package com.Happypaws.demo.service;

import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.model.Pet;
import com.Happypaws.demo.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private SupabaseStorageService storageService;

    @InjectMocks
    private PetService petService;

    private Pet mascotaValida() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        Pet pet = new Pet();
        pet.setNombre("Luna");
        pet.setEspecie("Perro");
        pet.setCliente(cliente);
        return pet;
    }

    @Test
    void validarPet_conEdadManual_esValida() {
        Pet pet = mascotaValida();
        pet.setEdad(3);

        assertThat(petService.validarPet(pet)).isTrue();
    }

    @Test
    void validarPet_conFechaNacimientoYSinEdad_esValida() {
        Pet pet = mascotaValida();
        pet.setEdad(null);
        pet.setFechaNacimiento(LocalDate.now().minusYears(2));

        assertThat(petService.validarPet(pet)).isTrue();
    }

    @Test
    void validarPet_sinEdadNiFechaNacimiento_noEsValida() {
        Pet pet = mascotaValida();
        pet.setEdad(null);
        pet.setFechaNacimiento(null);

        assertThat(petService.validarPet(pet)).isFalse();
    }

    @Test
    void getEdadCalculada_conFechaNacimiento_calculaLaEdadEnAnios() {
        Pet pet = new Pet();
        pet.setFechaNacimiento(LocalDate.now().minusYears(4).minusMonths(1));

        assertThat(pet.getEdadCalculada()).isEqualTo(4);
    }

    @Test
    void getEdadCalculada_sinFechaNacimiento_caeAlValorManual() {
        Pet pet = new Pet();
        pet.setEdad(7);

        assertThat(pet.getEdadCalculada()).isEqualTo(7);
    }

    @Test
    void guardarFoto_conTipoNoPermitido_lanzaExcepcion() {
        MultipartFile archivo = new MockMultipartFile(
                "fotoArchivo", "foto.gif", "image/gif", "contenido".getBytes());

        assertThatThrownBy(() -> petService.guardarFoto(archivo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JPG, JPEG o PNG");
    }

    @Test
    void guardarFoto_demasiadoGrande_lanzaExcepcion() {
        byte[] contenidoGrande = new byte[3 * 1024 * 1024];
        MultipartFile archivo = new MockMultipartFile(
                "fotoArchivo", "foto.png", "image/png", contenidoGrande);

        assertThatThrownBy(() -> petService.guardarFoto(archivo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("2MB");
    }

    @Test
    void guardarFoto_sinArchivo_devuelveNull() throws Exception {
        assertThat(petService.guardarFoto(null)).isNull();
    }

    @Test
    void eliminar_borraLaFotoAsociadaEnStorage() {
        Pet pet = mascotaValida();
        pet.setFoto("abc.png");

        when(petRepository.findById(5L)).thenReturn(Optional.of(pet));

        petService.eliminar(5L);

        verify(petRepository).deleteById(5L);
        verify(storageService).eliminar("mascotas/abc.png");
    }
}
