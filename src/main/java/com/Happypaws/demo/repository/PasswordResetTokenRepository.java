package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;


public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.user.idUsuario = :userId AND t.used = false")
    void invalidarTokensAnteriores(@Param("userId") Long userId);
}
