
package com.boticasperu.soa.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VentaResponse {

    private Integer id;
    private Integer clienteId;
    private LocalDateTime fecha;
    private String estado;
    private BigDecimal total;
    private List<DetalleVentaResponse> detalles;

    public VentaResponse(
            Integer id,
            Integer clienteId,
            LocalDateTime fecha,
            String estado,
            BigDecimal total,
            List<DetalleVentaResponse> detalles) {

        this.id = id;
        this.clienteId = clienteId;
        this.fecha = fecha;
        this.estado = estado;
        this.total = total;
        this.detalles = detalles;
    }

    public Integer getId() {
        return id;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<DetalleVentaResponse> getDetalles() {
        return detalles;
    }
}
