package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.ControlHospitalizacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ControlHospitalizacionRepository extends JpaRepository<ControlHospitalizacion, Long> {

    List<ControlHospitalizacion> findByHospitalizacionIdHospitalizacionOrderByFechaHoraDesc(Long hospitalizacionId);
}
