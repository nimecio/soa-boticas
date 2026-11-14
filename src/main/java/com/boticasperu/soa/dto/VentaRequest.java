
package com.boticasperu.soa.dto;

import java.util.List;

public class VentaRequest {

    private Integer clienteId;
    private List<DetalleVentaRequest> detalles;

    public VentaRequest() {
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public List<DetalleVentaRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaRequest> detalles) {
        this.detalles = detalles;
    }
}
