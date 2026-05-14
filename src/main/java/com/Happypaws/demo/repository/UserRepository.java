package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByEsActivoTrue();

    List<User> findByRoleId(Long roleId);

    @Query("SELECT u FROM User u WHERE u.esActivo = true AND u.role.id = :roleId")
    List<User> findByRoleAndActive(@Param("roleId") Long roleId);

    @Query("SELECT u FROM User u WHERE u.esActivo = true AND " +
           "(LOWER(u.name) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<User> buscarActivos(@Param("busqueda") String busqueda);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.id = :roleId")
    long countByRoleId(@Param("roleId") Long roleId);
}
