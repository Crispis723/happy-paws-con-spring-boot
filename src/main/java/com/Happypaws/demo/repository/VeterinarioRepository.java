package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    Optional<Veterinario> findByEmail(String email);

    Optional<Veterinario> findByCedula(String cedula);

    List<Veterinario> findByEsActivoTrue();

    List<Veterinario> findByEsActivoTrueOrderByNombre();

    @Query("SELECT v FROM Veterinario v WHERE v.esActivo = true AND " +
           "(LOWER(v.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(v.especialidad) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<Veterinario> buscarActivos(@Param("busqueda") String busqueda);

    @Query("SELECT v FROM Veterinario v WHERE v.esActivo = true")
    List<Veterinario> findAllActivos();

    List<Veterinario> findByEspecialidad(String especialidad);
}
