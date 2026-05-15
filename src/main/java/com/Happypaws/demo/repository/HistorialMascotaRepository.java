package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.HistorialMascota;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialMascotaRepository extends JpaRepository<HistorialMascota, Long> {

    List<HistorialMascota> findByMascotaIdOrderByFechaRegistroDesc(Long mascotaId);
}
