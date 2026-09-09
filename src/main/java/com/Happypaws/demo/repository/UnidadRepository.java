package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;   

public interface UnidadRepository extends JpaRepository<Unidad, Long> {
    
}
