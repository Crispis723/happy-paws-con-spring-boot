package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
	List<Appointment> findByClienteIdCliente(Long clienteId);
	List<Appointment> findByMascotaIdMascotaOrderByFechaHoraDesc(Long mascotaId);
	Optional<Appointment> findByVeterinarioIdUsuarioAndFechaHora(Long veterinarioId, LocalDateTime fechaHora);
	
	/**
	 * Busca citas dentro de un rango de tiempo específico
	 * Útil para enviar recordatorios automáticos
	 */
	@Query("SELECT a FROM Appointment a WHERE a.fechaHora >= :inicio AND a.fechaHora <= :fin")
	List<Appointment> findAppointmentsInTimeRange(
		@Param("inicio") LocalDateTime inicio,
		@Param("fin") LocalDateTime fin
	);
}
