package com.Happypaws.demo.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifica que SupabaseStorageService rechace rutas que intenten escapar
 * del bucket (path traversal), tanto en modo local (sin credenciales de
 * Supabase, que es el modo que se usa en estos tests) como en la
 * validación compartida con el modo remoto.
 *
 * El "path" pasado a subir/descargar/eliminar puede originarse en un
 * @PathVariable expuesto públicamente (ver ProductoImagenController), así
 * que estos casos cubren el escenario en el que un atacante intenta
 * inyectar "..", una ruta absoluta, o barras invertidas.
 */
class SupabaseStorageServiceTest {

    private SupabaseStorageService crearServiceLocal() {
        // Sin URL/clave de Supabase -> cae en modo local automáticamente.
        return new SupabaseStorageService(null, null, "test-bucket");
    }

    @Test
    void subir_conRutaValida_funcionaEnModoLocal() throws Exception {
        SupabaseStorageService service = crearServiceLocal();

        String resultado = service.subir("productos/archivo.png", "contenido".getBytes(), "image/png");

        assertThat(resultado).isEqualTo("productos/archivo.png");
        assertThat(service.descargar("productos/archivo.png")).isEqualTo("contenido".getBytes());

        service.eliminar("productos/archivo.png");
    }

    @Test
    void subir_conPathTraversal_esRechazado() {
        SupabaseStorageService service = crearServiceLocal();

        assertThatThrownBy(() -> service.subir("productos/../../../etc/passwd", "x".getBytes(), "image/png"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void descargar_conPathTraversal_esRechazado() {
        SupabaseStorageService service = crearServiceLocal();

        assertThatThrownBy(() -> service.descargar("../secreto.txt"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void descargar_conRutaAbsoluta_esRechazado() {
        SupabaseStorageService service = crearServiceLocal();

        assertThatThrownBy(() -> service.descargar("/etc/passwd"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void eliminar_conPathTraversal_esRechazado() {
        SupabaseStorageService service = crearServiceLocal();

        assertThatThrownBy(() -> service.eliminar("productos/../../otro-bucket/archivo.png"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void eliminar_conRutaNula_noLanzaExcepcion() {
        SupabaseStorageService service = crearServiceLocal();

        // eliminar() nunca debe bloquear una operación de negocio solo
        // porque el archivo ya no existe o no fue provisto.
        service.eliminar(null);
        service.eliminar("");
    }
}
