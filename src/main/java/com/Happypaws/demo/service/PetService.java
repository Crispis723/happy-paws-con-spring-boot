package com.Happypaws.demo.service;

import com.Happypaws.demo.model.Pet;
import com.Happypaws.demo.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetService {

	private final PetRepository repository;

	public PetService(PetRepository repository) {
		this.repository = repository;
	}

	public List<Pet> listar() {
		return repository.findAll();
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
		repository.deleteById(id);
	}

    public List<Pet> buscarPorNombre(String nombre) {
        return repository.findByNombre(nombre);
    }

	 public boolean existePorId(Long id) {
        return repository.existsById(id);
	 }

	 public boolean validarPet(Pet pet) {
		  return pet != null
					 && pet.getNombre() != null && !pet.getNombre().isBlank()
					 && pet.getEspecie() != null && !pet.getEspecie().isBlank()
					 && pet.getEdad() != null && pet.getEdad() >= 0
				&& pet.getCliente() != null
				&& pet.getCliente().getId() != null;
	 }


}
