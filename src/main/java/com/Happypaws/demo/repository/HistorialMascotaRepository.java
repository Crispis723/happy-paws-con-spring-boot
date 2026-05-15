package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.HistorialMascota;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialMascotaRepository extends JpaRepository<HistorialMascota, Long> {

    List<HistorialMascota> findByMascotaIdOrderByFechaRegistroDesc(Long mascotaId);
    List<HistorialMascota> findByMascotaClienteIdAndFechaExpiracionBetweenOrderByFechaExpiracionAsc(
            Long clienteId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );
}
