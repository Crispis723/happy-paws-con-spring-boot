package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.Hospitalizacion;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HospitalizacionRepository extends JpaRepository<Hospitalizacion, Long> {

    @Query("SELECT h FROM Hospitalizacion h WHERE h.fechaAlta IS NULL "
            + "AND h.estado NOT IN (com.Happypaws.demo.model.EstadoHospitalizacion.DADO_DE_ALTA, "
            + "com.Happypaws.demo.model.EstadoHospitalizacion.FALLECIDO) "
            + "ORDER BY h.fechaIngreso DESC")
    List<Hospitalizacion> findActivas();

    Page<Hospitalizacion> findByFechaAltaIsNotNullOrderByFechaAltaDesc(Pageable pageable);

    List<Hospitalizacion> findByMascotaIdMascotaOrderByFechaIngresoDesc(Long mascotaId);
}
