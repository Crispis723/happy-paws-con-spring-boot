package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
	List<Appointment> findByClienteId(Long clienteId);
	List<Appointment> findByMascotaIdOrderByFechaDesc(Long mascotaId);
}
