package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.Venta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
	List<Venta> findByClienteId(Long clienteId);
}