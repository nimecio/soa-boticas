
package com.boticasperu.soa.repository;

import com.boticasperu.soa.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleVentaRepository
        extends JpaRepository<DetalleVenta, Integer> {

    List<DetalleVenta> findByVenta_Id(Integer ventaId);
}
