package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.Desparasitacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesparasitacionRepository extends JpaRepository<Desparasitacion, Long> {

    List<Desparasitacion> findByMascotaIdMascotaOrderByFechaDesc(Long mascotaId);
}
