package com.Happypaws.demo.repository;

import com.Happypaws.demo.model.VentaDetalle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Long> {

    List<VentaDetalle> findByVenta_IdVenta(Long idVenta);
}
