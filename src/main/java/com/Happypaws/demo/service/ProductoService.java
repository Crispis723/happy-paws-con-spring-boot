package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.Producto;
import com.Happypaws.demo.repository.ProductoRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

private static final Set<String> TIPOS_PERMITIDOS = Set.of(
        "image/jpeg",
        "image/jpg",
        "image/png"
);

private static final long TAMANIO_MAXIMO_BYTES = 2L * 1024 * 1024; // 2MB

/**
 * Carpeta dentro del bucket de Supabase Storage donde se guardan las
 * imágenes de productos. Comparte bucket con el historial clínico
 * (configurado en supabase.storage.bucket); solo cambia el prefijo de
 * carpeta. Se eligió así para no requerir aprovisionar un segundo bucket;
 * si el catálogo crece mucho, conviene moverlas a un bucket público
 * propio para servirlas por CDN en vez de cruzar por este backend.
 */
private static final String CARPETA_STORAGE = "productos";

private final ProductoRepository repository;
private final SupabaseStorageService storageService;

public ProductoService(ProductoRepository repository, SupabaseStorageService storageService) {
    this.repository = repository;
    this.storageService = storageService;
}

// =========================================================
// LISTAR PRODUCTOS
// =========================================================

@Transactional(readOnly = true)
public List<Producto> listar() {
    return repository.findAll();
}

// =========================================================
// BUSCAR PRODUCTO POR ID
// =========================================================

@Transactional(readOnly = true)
public Optional<Producto> buscarPorId(Long id) {

    if (id == null) {
        return Optional.empty();
    }

    return repository.findById(id);
}

// =========================================================
// GUARDAR PRODUCTO
// =========================================================

@Transactional
public Producto guardar(Producto producto) {

    if (producto == null) {
        throw new IllegalArgumentException(
                "El producto no puede ser null."
        );
    }

    // Un producto nuevo no debería traer ID.
    producto.setIdProducto(null);

    return repository.save(producto);
}

// =========================================================
// ACTUALIZAR PRODUCTO
// =========================================================

@Transactional
public Producto actualizar(Producto producto) {

    if (producto == null) {
        throw new IllegalArgumentException(
                "El producto no puede ser null."
        );
    }

    if (producto.getIdProducto() == null) {
        throw new ResourceNotFoundException(
                "No se puede actualizar un producto sin ID."
        );
    }

    Long id = producto.getIdProducto();

    if (!repository.existsById(id)) {
        throw new ResourceNotFoundException(
                "Producto no encontrado con ID: " + id
        );
    }

    return repository.save(producto);
}

// =========================================================
// ELIMINAR PRODUCTO
// =========================================================

@Transactional
public void eliminar(Long id) {

    if (id == null) {
        throw new IllegalArgumentException(
                "El ID del producto no puede ser null."
        );
    }

    if (!repository.existsById(id)) {
        throw new ResourceNotFoundException(
                "Producto no encontrado con ID: " + id
        );
    }

    Optional<Producto> existente = repository.findById(id);

    repository.deleteById(id);

    existente.ifPresent(p -> eliminarImagenSiExiste(p.getImagen()));
}

// =========================================================
// IMAGEN DEL PRODUCTO
// =========================================================

/**
 * Valida y sube a Supabase Storage la imagen subida para un producto.
 * Devuelve el nombre de archivo generado (para guardarlo en
 * producto.imagen; NO incluye el prefijo de carpeta), o null si no se
 * proporcionó archivo.
 *
 * Se sube a Supabase Storage (no a disco local) porque el disco de
 * Render en el plan free es efímero: cualquier archivo guardado
 * localmente desaparece en el próximo redeploy o reinicio.
 */
public String guardarImagen(MultipartFile archivo) throws IOException {

    if (archivo == null || archivo.isEmpty()) {
        return null;
    }

    String tipoContenido = archivo.getContentType();

    if (tipoContenido == null || !TIPOS_PERMITIDOS.contains(tipoContenido)) {
        throw new IllegalArgumentException(
                "La imagen debe ser un archivo JPG, JPEG o PNG."
        );
    }

    if (archivo.getSize() > TAMANIO_MAXIMO_BYTES) {
        throw new IllegalArgumentException(
                "La imagen no puede superar los 2MB."
        );
    }

    String nombreOriginal = archivo.getOriginalFilename() != null
            ? archivo.getOriginalFilename()
            : "imagen";

    String extension = "";
    int puntoIndex = nombreOriginal.lastIndexOf('.');

    if (puntoIndex >= 0) {
        extension = nombreOriginal.substring(puntoIndex);
    }

    String nombreArchivo = UUID.randomUUID() + extension;

    storageService.subir(
            CARPETA_STORAGE + "/" + nombreArchivo,
            archivo.getBytes(),
            tipoContenido
    );

    return nombreArchivo;
}

/**
 * Descarga desde Supabase Storage el contenido de la imagen de un
 * producto, dado el nombre de archivo guardado en producto.imagen.
 */
public byte[] descargarImagen(String nombreArchivo) throws IOException {
    return storageService.descargar(CARPETA_STORAGE + "/" + nombreArchivo);
}

/**
 * Elimina de Supabase Storage el archivo de imagen anterior de un
 * producto, si existe. No lanza excepción si el archivo ya no está.
 */
public void eliminarImagenSiExiste(String nombreArchivo) {

    if (nombreArchivo == null || nombreArchivo.isBlank()) {
        return;
    }

    storageService.eliminar(CARPETA_STORAGE + "/" + nombreArchivo);
}


}
