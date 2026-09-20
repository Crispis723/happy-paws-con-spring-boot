package com.Happypaws.demo.service;

import com.Happypaws.demo.model.Pet;
import com.Happypaws.demo.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class PetService {

	private static final Set<String> TIPOS_IMAGEN_PERMITIDOS = Set.of(
			"image/jpeg", "image/jpg", "image/png"
	);

	private static final long TAMANIO_MAXIMO_BYTES = 2L * 1024 * 1024; // 2MB

	/** Carpeta dentro del bucket de Supabase Storage para fotos de mascotas. */
	private static final String CARPETA_STORAGE = "mascotas";

	private final PetRepository repository;
	private final SupabaseStorageService storageService;

	public PetService(PetRepository repository, SupabaseStorageService storageService) {
		this.repository = repository;
		this.storageService = storageService;
	}

	public List<Pet> listar() {
		return repository.findAll();
	}

	/** Cuenta mascotas sin cargar la lista completa en memoria. */
	public long contar() {
		return repository.count();
	}

	public List<Pet> listarPorClienteId(Long clienteId) {
		return repository.findByClienteIdCliente(clienteId);
	}

	public Optional<Pet> buscarPorId(Long id) {
		return repository.findById(id);
	}

	public Pet guardar(Pet pet) {
		if (!validarPet(pet)) {
			throw new IllegalArgumentException("Datos de mascota no válidos");
		}
		return repository.save(pet);
	}

	public Pet actualizar(Pet pet) {
		if (!validarPet(pet)) {
			throw new IllegalArgumentException("Datos de mascota no válidos");
		}
		return repository.save(pet);
	}

	public void eliminar(Long id) {
		Optional<Pet> existente = repository.findById(id);
		repository.deleteById(id);
		existente.ifPresent(p -> eliminarFotoSiExiste(p.getFoto()));
	}

    public List<Pet> buscarPorNombre(String nombre) {
        return repository.findByNombre(nombre);
    }

	 public boolean existePorId(Long id) {
        return repository.existsById(id);
	 }

	 public boolean validarPet(Pet pet) {
		  boolean tieneEdadValida = (pet.getEdad() != null && pet.getEdad() >= 0)
				  || pet.getFechaNacimiento() != null;
		  return pet != null
					 && pet.getNombre() != null && !pet.getNombre().isBlank()
					 && pet.getEspecie() != null && !pet.getEspecie().isBlank()
					 && tieneEdadValida
				&& pet.getCliente() != null
				&& pet.getCliente().getIdCliente() != null;
	 }

	// =========================================================
	// FOTO DE LA MASCOTA (mismo patrón que ProductoService.guardarImagen)
	// =========================================================

	/**
	 * Valida y sube a Supabase Storage la foto de una mascota. Devuelve el
	 * nombre de archivo generado (sin el prefijo de carpeta), o null si no
	 * se proporcionó archivo.
	 */
	public String guardarFoto(MultipartFile archivo) throws IOException {
		if (archivo == null || archivo.isEmpty()) {
			return null;
		}

		String tipoContenido = archivo.getContentType();
		if (tipoContenido == null || !TIPOS_IMAGEN_PERMITIDOS.contains(tipoContenido)) {
			throw new IllegalArgumentException("La foto debe ser un archivo JPG, JPEG o PNG.");
		}

		if (archivo.getSize() > TAMANIO_MAXIMO_BYTES) {
			throw new IllegalArgumentException("La foto no puede superar los 2MB.");
		}

		String nombreOriginal = archivo.getOriginalFilename() != null ? archivo.getOriginalFilename() : "foto";
		String extension = "";
		int puntoIndex = nombreOriginal.lastIndexOf('.');
		if (puntoIndex >= 0) {
			extension = nombreOriginal.substring(puntoIndex);
		}

		String nombreArchivo = UUID.randomUUID() + extension;
		storageService.subir(CARPETA_STORAGE + "/" + nombreArchivo, archivo.getBytes(), tipoContenido);
		return nombreArchivo;
	}

	/** Descarga desde Supabase Storage el contenido de la foto de una mascota. */
	public byte[] descargarFoto(String nombreArchivo) throws IOException {
		return storageService.descargar(CARPETA_STORAGE + "/" + nombreArchivo);
	}

	/** Elimina de Supabase Storage la foto anterior de una mascota, si existe. */
	public void eliminarFotoSiExiste(String nombreArchivo) {
		if (nombreArchivo == null || nombreArchivo.isBlank()) {
			return;
		}
		storageService.eliminar(CARPETA_STORAGE + "/" + nombreArchivo);
	}
}

