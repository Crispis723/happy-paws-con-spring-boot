package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.ControlHospitalizacion;
import com.Happypaws.demo.model.EstadoHospitalizacion;
import com.Happypaws.demo.model.Hospitalizacion;
import com.Happypaws.demo.repository.ControlHospitalizacionRepository;
import com.Happypaws.demo.repository.HospitalizacionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HospitalizacionService {

    private final HospitalizacionRepository repository;
    private final ControlHospitalizacionRepository controlRepository;

    public HospitalizacionService(HospitalizacionRepository repository,
                                   ControlHospitalizacionRepository controlRepository) {
        this.repository = repository;
        this.controlRepository = controlRepository;
    }

    @Transactional(readOnly = true)
    public List<Hospitalizacion> listarActivas() {
        return repository.findActivas();
    }

    @Transactional(readOnly = true)
    public Page<Hospitalizacion> listarHistorial(Pageable pageable) {
        return repository.findByFechaAltaIsNotNullOrderByFechaAltaDesc(pageable);
    }

    @Transactional(readOnly = true)
    public List<Hospitalizacion> listarPorMascotaId(Long mascotaId) {
        return repository.findByMascotaIdMascotaOrderByFechaIngresoDesc(mascotaId);
    }

    @Transactional(readOnly = true)
    public Optional<Hospitalizacion> buscarPorId(Long id) {
        return repository.findById(id);
    }

    /** Cuenta hospitalizaciones activas ahora mismo (para el dashboard). */
    @Transactional(readOnly = true)
    public long contarActivas() {
        return repository.findActivas().size();
    }

    @Transactional
    public Hospitalizacion ingresar(Hospitalizacion hospitalizacion) {
        if (hospitalizacion.getFechaIngreso() == null) {
            hospitalizacion.setFechaIngreso(LocalDateTime.now());
        }
        hospitalizacion.setEstado(EstadoHospitalizacion.EN_OBSERVACION);
        hospitalizacion.setFechaAlta(null);
        return repository.save(hospitalizacion);
    }

    /**
     * Registra un control durante el día (signos vitales + evolución) y
     * actualiza el estado clínico general de la hospitalización con el
     * que se reportó en este control, para que la ficha siempre muestre
     * el estado más reciente sin tener que abrir la lista de controles.
     */
    @Transactional
    public ControlHospitalizacion registrarControl(Long hospitalizacionId, ControlHospitalizacion control, EstadoHospitalizacion nuevoEstado) {
        Hospitalizacion hospitalizacion = repository.findById(hospitalizacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospitalización no encontrada"));

        if (!hospitalizacion.isActiva()) {
            throw new IllegalArgumentException(
                    "Esta hospitalización ya está cerrada; no se pueden registrar más controles.");
        }

        if (control.getFechaHora() == null) {
            control.setFechaHora(LocalDateTime.now());
        }
        control.setHospitalizacion(hospitalizacion);

        ControlHospitalizacion guardado = controlRepository.save(control);

        if (nuevoEstado != null) {
            hospitalizacion.setEstado(nuevoEstado);
            repository.save(hospitalizacion);
        }

        return guardado;
    }

    /**
     * Da de alta (o marca como fallecida) una hospitalización activa.
     * No se puede volver a abrir después: si la mascota necesita
     * hospitalizarse de nuevo, se crea un episodio nuevo, para no perder
     * el historial de cuándo empezó y terminó cada internamiento.
     */
    @Transactional
    public Hospitalizacion cerrar(Long id, EstadoHospitalizacion estadoFinal, String observacionesAlta) {
        Hospitalizacion hospitalizacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospitalización no encontrada"));

        if (!hospitalizacion.isActiva()) {
            throw new IllegalArgumentException("Esta hospitalización ya está cerrada.");
        }

        if (estadoFinal == null || !estadoFinal.esFinal()) {
            throw new IllegalArgumentException(
                    "El estado de cierre debe ser \"Dado de alta\" o \"Falleció\".");
        }

        hospitalizacion.setEstado(estadoFinal);
        hospitalizacion.setFechaAlta(LocalDateTime.now());
        hospitalizacion.setObservacionesAlta(observacionesAlta);

        return repository.save(hospitalizacion);
    }

    /**
     * Los últimos signos vitales conocidos de la hospitalización: los del
     * control más reciente si ya hay alguno registrado, o los de ingreso
     * si todavía no se ha hecho ningún control. Es lo que se muestra "en
     * grande" en la ficha (ver el mockup: Temperatura / Peso destacados).
     */
    public UltimosSignosVitales obtenerUltimosSignosVitales(Hospitalizacion hospitalizacion) {
        List<ControlHospitalizacion> controles = controlRepository
                .findByHospitalizacionIdHospitalizacionOrderByFechaHoraDesc(hospitalizacion.getId());

        if (!controles.isEmpty()) {
            ControlHospitalizacion ultimo = controles.get(0);
            return new UltimosSignosVitales(ultimo.getPeso(), ultimo.getTemperatura(), ultimo.getFechaHora());
        }

        return new UltimosSignosVitales(
                hospitalizacion.getPesoIngreso(),
                hospitalizacion.getTemperaturaIngreso(),
                hospitalizacion.getFechaIngreso()
        );
    }

    public record UltimosSignosVitales(java.math.BigDecimal peso, java.math.BigDecimal temperatura, LocalDateTime fecha) {
    }
}
