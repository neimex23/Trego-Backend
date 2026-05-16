package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOOrden;
import com.backend.trego.entity.DTOs.DTOProductoOrden;
import com.backend.trego.service.MercadoPagoService;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordenes")
public class OrdenController {

    private final MercadoPagoService mercadoPagoService;

    public OrdenController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> create(@RequestBody DTOOrden orden) {
        if (orden == null || orden.getProductos() == null || orden.getProductos().isEmpty()) {
            return ResponseEntity.badRequest().body("La lista de productos no puede estar vacía");
        }

        List<DTOProductoOrden> productos = orden.getProductos();

        boolean datosInvalidos = productos.stream().anyMatch(p ->
                p.getCantidad() <= 0
                        || p.getPrecio() == null
                        || p.getPrecio().compareTo(BigDecimal.ZERO) <= 0
        );

        if (datosInvalidos) {
            return ResponseEntity.badRequest()
                    .body("La cantidad y el precio de los productos deben ser mayores a cero");
        }

        try {
            // TODO: mapear DTOOrden -> Pedido y delegar a mercadoPagoService.crearOrden(pedido)
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al crear la orden: " + e.getMessage());
        }
    }
}
