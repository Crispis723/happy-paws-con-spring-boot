package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.Vacuna;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacunaRepository extends JpaRepository<Vacuna, Long> {

    List<Vacuna> findByMascotaIdMascotaOrderByFechaDesc(Long mascotaId);
}
