package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
	List<Appointment> findByClienteIdCliente(Long clienteId);
	List<Appointment> findByMascotaIdMascotaOrderByFechaHoraDesc(Long mascotaId);
	Optional<Appointment> findByVeterinarioIdUsuarioAndFechaHora(Long veterinarioId, LocalDateTime fechaHora);
}
