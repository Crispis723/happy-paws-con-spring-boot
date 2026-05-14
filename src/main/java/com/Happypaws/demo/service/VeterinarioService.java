package com.Happypaws.demo.service;

import com.Happypaws.demo.model.Veterinario;
import com.Happypaws.demo.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;

    public VeterinarioService(VeterinarioRepository veterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
    }

    /**
     * Obtener todos los veterinarios activos
     */
    public List<Veterinario> listarActivos() {
        return veterinarioRepository.findByEsActivoTrueOrderByNombre();
    }

    /**
     * Obtener todos los veterinarios
     */
    public List<Veterinario> listarTodos() {
        return veterinarioRepository.findAll();
    }

    /**
     * Buscar veterinario por ID
     */
    public Optional<Veterinario> buscarPorId(Long id) {
        return veterinarioRepository.findById(id);
    }

    /**
     * Buscar por email
     */
    public Optional<Veterinario> buscarPorEmail(String email) {
        return veterinarioRepository.findByEmail(email);
    }

    /**
     * Buscar por cédula
     */
    public Optional<Veterinario> buscarPorCedula(String cedula) {
        return veterinarioRepository.findByCedula(cedula);
    }

    /**
     * Guardar nuevo veterinario
     */
    public Veterinario guardar(Veterinario veterinario) {
        // Validar que no exista email duplicado
        if (veterinario.getId() == null && 
            veterinarioRepository.findByEmail(veterinario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Validar que no exista cédula duplicada
        if (veterinario.getId() == null && 
            veterinarioRepository.findByCedula(veterinario.getCedula()).isPresent()) {
            throw new IllegalArgumentException("La cédula ya está registrada");
        }

        return veterinarioRepository.save(veterinario);
    }

    /**
     * Actualizar veterinario
     */
    public Veterinario actualizar(Long id, Veterinario veterinarioActualizado) {
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veterinario no encontrado"));

        veterinario.setNombre(veterinarioActualizado.getNombre());
        veterinario.setEmail(veterinarioActualizado.getEmail());
        veterinario.setTelefono(veterinarioActualizado.getTelefono());
        veterinario.setCedula(veterinarioActualizado.getCedula());
        veterinario.setEspecialidad(veterinarioActualizado.getEspecialidad());
        veterinario.setBiografia(veterinarioActualizado.getBiografia());
        veterinario.setLicencia(veterinarioActualizado.getLicencia());
        veterinario.setFotoUrl(veterinarioActualizado.getFotoUrl());
        veterinario.setHorarioDisponible(veterinarioActualizado.getHorarioDisponible());

        return veterinarioRepository.save(veterinario);
    }

    /**
     * Eliminar veterinario (cambiar estado a inactivo)
     */
    public void eliminar(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veterinario no encontrado"));
        
        veterinario.setEsActivo(false);
        veterinarioRepository.save(veterinario);
    }

    /**
     * Activar veterinario
     */
    public void activar(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veterinario no encontrado"));
        
        veterinario.setEsActivo(true);
        veterinarioRepository.save(veterinario);
    }

    /**
     * Buscar veterinarios por especialidad
     */
    public List<Veterinario> buscarPorEspecialidad(String especialidad) {
        return veterinarioRepository.findByEspecialidad(especialidad);
    }

    /**
     * Buscar veterinarios activos
     */
    public List<Veterinario> buscar(String busqueda) {
        return veterinarioRepository.buscarActivos(busqueda);
    }

    /**
     * Obtener total de veterinarios activos
     */
    public long contarActivos() {
        return veterinarioRepository.findByEsActivoTrue().size();
    }

    /**
     * Verificar si existe email
     */
    public boolean existeEmail(String email) {
        return veterinarioRepository.findByEmail(email).isPresent();
    }

    /**
     * Verificar si existe cédula
     */
    public boolean existeCedula(String cedula) {
        return veterinarioRepository.findByCedula(cedula).isPresent();
    }
}
