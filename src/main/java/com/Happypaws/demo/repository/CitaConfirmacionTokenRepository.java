package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.CitaConfirmacionToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CitaConfirmacionTokenRepository extends JpaRepository<CitaConfirmacionToken, Long> {

    Optional<CitaConfirmacionToken> findByToken(String token);

    @Modifying
    @Query("UPDATE CitaConfirmacionToken t SET t.used = true WHERE t.cita.idCita = :citaId AND t.used = false")
    void invalidarTokensAnteriores(@Param("citaId") Long citaId);
}
