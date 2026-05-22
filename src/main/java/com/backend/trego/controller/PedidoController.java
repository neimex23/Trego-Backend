package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOConfirmarPedidoRequest;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;
import com.backend.trego.service.PedidoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Endpoints de pedidos.
@RestController
@RequestMapping("/api/pedido")
@CrossOrigin("*")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/confirmar")
    public ResponseEntity<DTOPreferenciaMP> confirmarPedido(@RequestBody DTOConfirmarPedidoRequest request) {
        DTOPreferenciaMP preferencia = pedidoService.confirmarPedido(
                request.getCarrito(),
                request.getDireccion(),
                String.valueOf(request.getRestauranteId()));
        return ResponseEntity.ok(preferencia);
    }
}
