package com.Happypaws.demo.controller;

import com.Happypaws.demo.service.PetService;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Sirve las fotos de mascotas guardadas en Supabase Storage (ver
 * PetService.guardarFoto), con el mismo patrón que ProductoImagenController.
 *
 * Es público a propósito: la foto de una mascota no es información
 * sensible (a diferencia del historial clínico), así que no requiere
 * autenticación. Ver SecurityConfig: "/uploads/**" está en permitAll().
 */
@Controller
public class PetFotoController {

    private final PetService petService;

    public PetFotoController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping("/uploads/mascotas/{nombreArchivo}")
    public ResponseEntity<byte[]> foto(@PathVariable String nombreArchivo) {
        try {
            byte[] contenido = petService.descargarFoto(nombreArchivo);

            return ResponseEntity.ok()
                    .contentType(mediaTypeDesdeNombre(nombreArchivo))
                    .cacheControl(CacheControl.maxAge(Duration.ofDays(7)).cachePublic())
                    .body(contenido);

        } catch (IOException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    private MediaType mediaTypeDesdeNombre(String nombreArchivo) {
        String nombre = nombreArchivo.toLowerCase();
        if (nombre.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (nombre.endsWith(".jpg") || nombre.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
