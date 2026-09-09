package com.Happypaws.demo.service;

import com.Happypaws.demo.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Verifica la validación de la imagen subida para un producto: tipo de
 * archivo permitido y tamaño máximo (2MB). Estas validaciones ocurren
 * antes de intentar subir el archivo a Supabase Storage, así que no hace
 * falta un servicio de storage real ni mockeado para estos casos: basta
 * con una instancia "local" (sin credenciales de Supabase) que nunca
 * llega a usarse.
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    private ProductoService crearService() {
        // Sin URL/clave de Supabase -> SupabaseStorageService cae en modo
        // local automáticamente; no importa para estos tests porque la
        // validación falla antes de llegar a subir/eliminar el archivo.
        SupabaseStorageService storageService = new SupabaseStorageService(null, null, "productos");
        return new ProductoService(productoRepository, storageService);
    }

    @Test
    void sinArchivo_devuelveNull() throws Exception {
        ProductoService service = crearService();

        assertThat(service.guardarImagen(null)).isNull();

        MultipartFile vacio = new MockMultipartFile("imagenArchivo", new byte[0]);
        assertThat(service.guardarImagen(vacio)).isNull();
    }

    @Test
    void tipoDeArchivoNoPermitido_lanzaExcepcion() {
        ProductoService service = crearService();

        MultipartFile pdf = new MockMultipartFile(
                "imagenArchivo", "documento.pdf", "application/pdf", "contenido".getBytes());

        assertThatThrownBy(() -> service.guardarImagen(pdf))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JPG, JPEG o PNG");
    }

    @Test
    void archivoDemasiadoGrande_lanzaExcepcion() {
        ProductoService service = crearService();

        byte[] contenidoGrande = new byte[3 * 1024 * 1024]; // 3MB > límite de 2MB
        MultipartFile imagenGrande = new MockMultipartFile(
                "imagenArchivo", "foto.png", "image/png", contenidoGrande);

        assertThatThrownBy(() -> service.guardarImagen(imagenGrande))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("2MB");
    }

    @Test
    void listarPaginado_delegaEnElRepositorioYDevuelveLaPagina() {
        ProductoService service = crearService();
        Pageable pageable = PageRequest.of(0, 20);
        Page<com.Happypaws.demo.model.Producto> pagina = new PageImpl<>(List.of(), pageable, 0);

        when(productoRepository.findAll(pageable)).thenReturn(pagina);

        Page<com.Happypaws.demo.model.Producto> resultado = service.listar(pageable);

        assertThat(resultado).isSameAs(pagina);
    }

    @Test
    void contar_delegaEnElRepositorio() {
        ProductoService service = crearService();

        when(productoRepository.count()).thenReturn(42L);

        assertThat(service.contar()).isEqualTo(42L);
    }
}
