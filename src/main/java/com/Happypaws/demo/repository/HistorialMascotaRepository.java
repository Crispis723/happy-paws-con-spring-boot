package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.HistorialMascota;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialMascotaRepository extends JpaRepository<HistorialMascota, Long> {

    List<HistorialMascota> findByMascotaIdMascotaOrderByFechaRegistroDesc(Long mascotaId);
    List<HistorialMascota> findByMascotaClienteIdClienteAndFechaExpiracionBetweenOrderByFechaExpiracionAsc(
            Long clienteId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );
}
