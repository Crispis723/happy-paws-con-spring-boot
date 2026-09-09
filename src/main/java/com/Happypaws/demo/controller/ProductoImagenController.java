package com.Happypaws.demo.controller;

import com.Happypaws.demo.service.ProductoService;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Sirve las imágenes de productos que se guardan en Supabase Storage
 * (ver ProductoService.guardarImagen). Las vistas Thymeleaf ya referencian
 * las imágenes como /uploads/productos/{nombreArchivo}, así que este
 * endpoint reemplaza lo que antes era un simple resource handler apuntando
 * a disco local (que no sobrevivía a los redeploys de Render).
 *
 * Es público a propósito: las fotos de productos no son información
 * sensible (a diferencia del historial clínico), así que no requieren
 * autenticación. Ver SecurityConfig: "/uploads/**" está en permitAll().
 */
@Controller
public class ProductoImagenController {

    private final ProductoService productoService;

    public ProductoImagenController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/uploads/productos/{nombreArchivo}")
    public ResponseEntity<byte[]> imagen(@PathVariable String nombreArchivo) {
        try {
            byte[] contenido = productoService.descargarImagen(nombreArchivo);

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
