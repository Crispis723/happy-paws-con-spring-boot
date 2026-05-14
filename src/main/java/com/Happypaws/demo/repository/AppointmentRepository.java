package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.model.Appointment.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByVeterinarioId(Long veterinarioId);

    List<Appointment> findByVeterinarioIdAndEstado(Long veterinarioId, EstadoCita estado);

    List<Appointment> findByMascotaId(Long mascotaId);

    List<Appointment> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Appointment> findByVeterinarioIdAndFechaHoraBetween(Long veterinarioId, LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT a FROM Appointment a WHERE a.veterinario.id = :veterinarioId ORDER BY a.fechaHora DESC")
    List<Appointment> findByVeterinarioRecientes(@Param("veterinarioId") Long veterinarioId);

    @Query("SELECT a FROM Appointment a WHERE a.estado = :estado ORDER BY a.fechaHora ASC")
    List<Appointment> findByEstadoOrdenado(@Param("estado") EstadoCita estado);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.veterinario.id = :veterinarioId AND a.estado = :estado")
    long countByVeterinarioAndEstado(@Param("veterinarioId") Long veterinarioId, @Param("estado") EstadoCita estado);

    @Query("SELECT a FROM Appointment a WHERE a.veterinario.id = :veterinarioId AND DATE(a.fechaHora) = CURRENT_DATE")
    List<Appointment> findTodayAppointments(@Param("veterinarioId") Long veterinarioId);

    @Query("SELECT a FROM Appointment a WHERE a.mascota.owner.id = :ownerId ORDER BY a.fechaHora DESC")
    List<Appointment> findByOwnerId(@Param("ownerId") Long ownerId);
}
