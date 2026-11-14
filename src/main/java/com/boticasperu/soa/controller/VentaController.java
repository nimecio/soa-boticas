
package com.boticasperu.soa.controller;

import com.boticasperu.soa.dto.VentaRequest;
import com.boticasperu.soa.entity.Venta;
import com.boticasperu.soa.service.VentaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.boticasperu.soa.dto.VentaResponse;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<Venta> registrarVenta(
            @RequestBody VentaRequest request) {

        Venta venta = ventaService.registrarVenta(request);

        return ResponseEntity.ok(venta);
    }

    @GetMapping
    public List<Venta> listarVentas() {
        return ventaService.listarVentas();
    }

   @GetMapping("/{id}")
public ResponseEntity<VentaResponse> buscarVenta(
        @PathVariable Integer id) {

    return ResponseEntity.ok(
            ventaService.obtenerVentaCompleta(id)
    );
}
}
