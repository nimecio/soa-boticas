
package com.boticasperu.soa.service;

import com.boticasperu.soa.dto.VentaRequest;
import com.boticasperu.soa.dto.DetalleVentaRequest;
import com.boticasperu.soa.dto.VentaResponse;
import com.boticasperu.soa.dto.DetalleVentaResponse;

import com.boticasperu.soa.entity.Venta;
import com.boticasperu.soa.entity.Producto;
import com.boticasperu.soa.entity.DetalleVenta;

import com.boticasperu.soa.repository.VentaRepository;
import com.boticasperu.soa.repository.DetalleVentaRepository;
import com.boticasperu.soa.repository.ProductoRepository;
import com.boticasperu.soa.repository.ClienteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;

    public VentaService(
            VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            ProductoRepository productoRepository,
            ClienteRepository clienteRepository) {

        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
    }

    // REGISTRAR UNA VENTA
    @Transactional
    public Venta registrarVenta(VentaRequest request) {

        // Validar solicitud
        if (request == null || request.getDetalles() == null
                || request.getDetalles().isEmpty()) {

            throw new IllegalArgumentException(
                    "La venta debe contener al menos un producto");
        }

        // Validar cliente
        if (request.getClienteId() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un cliente");
        }

        var cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente no encontrado: " + request.getClienteId()));

        if (!"ACTIVO".equalsIgnoreCase(cliente.getEstado())) {
            throw new IllegalArgumentException(
                    "El cliente no está activo");
        }

        // Validar productos y cantidades repetidas
        Map<Long, Integer> cantidades = new HashMap<>();

        for (DetalleVentaRequest item : request.getDetalles()) {

            if (item == null || item.getProductoId() == null
                    || item.getCantidad() == null
                    || item.getCantidad() <= 0) {

                throw new IllegalArgumentException(
                        "El producto y la cantidad deben ser válidos");
            }

            cantidades.merge(
                    item.getProductoId(),
                    item.getCantidad(),
                    Integer::sum
            );
        }

        // Crear venta
        Venta venta = new Venta();
        venta.setClienteId(request.getClienteId());
        venta.setTotal(BigDecimal.ZERO);

        venta = ventaRepository.save(venta);

        BigDecimal totalVenta = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();

        // Procesar productos
        for (Map.Entry<Long, Integer> entrada : cantidades.entrySet()) {

            Long productoId = entrada.getKey();
            Integer cantidad = entrada.getValue();

          Producto producto = productoRepository
        .buscarPorIdConBloqueo(productoId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Producto no encontrado: " + productoId));

            // Validar estado del producto
            if (!"ACTIVO".equalsIgnoreCase(producto.getEstado())) {
                throw new IllegalArgumentException(
                        "El producto no está activo: " + producto.getNombre());
            }

            // Validar stock
            if (producto.getStock() == null
                    || producto.getStock() < cantidad) {

                throw new IllegalArgumentException(
                        "Stock insuficiente para: " + producto.getNombre());
            }

            // Validar precio
            BigDecimal precio = producto.getPrecio();

            if (precio == null
                    || precio.compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        "El precio del producto debe ser mayor que cero");
            }

            // Calcular subtotal
            BigDecimal subtotal = precio.multiply(
                    BigDecimal.valueOf(cantidad));

            // Crear detalle de venta
            DetalleVenta detalle = new DetalleVenta();

            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(subtotal);

            detalles.add(detalle);

            // Acumular total
            totalVenta = totalVenta.add(subtotal);

            // Descontar stock
            producto.setStock(producto.getStock() - cantidad);
            productoRepository.save(producto);
        }

        // Actualizar total de venta
        venta.setTotal(totalVenta);
        ventaRepository.save(venta);

        // Guardar detalles
        detalleVentaRepository.saveAll(detalles);

        return venta;
    }

    // LISTAR TODAS LAS VENTAS
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    // BUSCAR VENTA POR ID
    public Venta buscarVenta(Integer id) {

        return ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Venta no encontrada: " + id));
    }

    // OBTENER VENTA COMPLETA CON SUS DETALLES
    @Transactional(readOnly = true)
    public VentaResponse obtenerVentaCompleta(Integer id) {

        Venta venta = buscarVenta(id);

        List<DetalleVenta> detalles =
                detalleVentaRepository.findByVenta_Id(id);

        List<DetalleVentaResponse> detallesResponse =
                new ArrayList<>();

        for (DetalleVenta detalle : detalles) {

            DetalleVentaResponse item =
                    new DetalleVentaResponse(
                            detalle.getProducto().getNombre(),
                            detalle.getCantidad(),
                            detalle.getPrecioUnitario(),
                            detalle.getSubtotal()
                    );

            detallesResponse.add(item);
        }

        return new VentaResponse(
                venta.getId(),
                venta.getClienteId(),
                venta.getFecha(),
                venta.getEstado(),
                venta.getTotal(),
                detallesResponse
        );
    }
}
