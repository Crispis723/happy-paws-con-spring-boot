package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.AppointmentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentVersionRepository extends JpaRepository<AppointmentVersion, Long> {

    List<AppointmentVersion> findByAppointmentIdOrderByVersionNumberDesc(Long appointmentId);

    long countByAppointmentId(Long appointmentId);
}