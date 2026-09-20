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

	@Query("SELECT a FROM Appointment a JOIN FETCH a.mascota JOIN FETCH a.cliente LEFT JOIN FETCH a.veterinario")
	List<Appointment> findAllWithRelations();

	@Query("SELECT a FROM Appointment a JOIN FETCH a.mascota JOIN FETCH a.cliente LEFT JOIN FETCH a.veterinario "
			+ "WHERE a.cliente.idCliente = :clienteId")
	List<Appointment> findByClienteIdClienteWithRelations(@Param("clienteId") Long clienteId);
	
	/**
	 * Busca citas dentro de un rango de tiempo específico, excluyendo las
	 * que ya no van a ocurrir (canceladas o marcadas como "no asistió").
	 * Útil para enviar recordatorios automáticos: sin este filtro, se le
	 * seguiría recordando al cliente una cita que él mismo canceló.
	 */
	@Query("SELECT a FROM Appointment a JOIN FETCH a.mascota JOIN FETCH a.cliente LEFT JOIN FETCH a.veterinario "
			+ "WHERE a.fechaHora >= :inicio AND a.fechaHora <= :fin "
			+ "AND a.estado NOT IN (com.Happypaws.demo.model.EstadoCita.CANCELADA, com.Happypaws.demo.model.EstadoCita.NO_ASISTIO)")
	List<Appointment> findAppointmentsInTimeRange(
		@Param("inicio") LocalDateTime inicio,
		@Param("fin") LocalDateTime fin
	);
}
