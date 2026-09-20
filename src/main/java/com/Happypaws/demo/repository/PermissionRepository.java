package com.Happypaws.demo.repository;
import com.Happypaws.demo.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
public interface PermissionRepository extends JpaRepository<Permission, Long> { Optional<Permission> findByName(String name); Optional<Permission> findByCode(String code);
    List<Permission> findAllByOrderByModule_DisplayOrderAscActionAscNameAsc(); }
