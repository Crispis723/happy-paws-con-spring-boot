package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByNombre(String nombre);
    List<Pet> findByClienteIdCliente(Long clienteId);


}