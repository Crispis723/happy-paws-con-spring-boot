package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.CompraDetalle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraDetalleRepository extends JpaRepository<CompraDetalle, Long> {

    List<CompraDetalle> findByCompra_IdCompra(Long idCompra);
}
