package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOConfirmarPedidoRequest;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;
import com.backend.trego.exception.SinProductoException;
import com.backend.trego.service.PedidoService;
import com.backend.trego.service.RestauranteService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Endpoints de pedidos.
@RestController
@RequestMapping("/api/pedido")
@CrossOrigin("*")
public class PedidoController {

    private final PedidoService pedidoService;
    private final RestauranteService restauranteService;

    public PedidoController(PedidoService pedidoService, RestauranteService restauranteService) {
        this.pedidoService = pedidoService;
        this.restauranteService = restauranteService;
    }

    @PostMapping("/confirmar")
    public ResponseEntity<DTOPreferenciaMP> confirmarPedido(@RequestBody DTOConfirmarPedidoRequest request) {
        DTOPreferenciaMP preferencia = pedidoService.confirmarPedido(
                request.getCarrito(),
                request.getDireccion(),
                String.valueOf(request.getRestauranteId()));
        return ResponseEntity.ok(preferencia);
    }

    // Ver el menú de un restaurante. restauranteId identifica el restaurante que
    // seleccionó el cliente, para no mezclar menús de otros locales.
    @GetMapping("/restaurante/{restauranteId}/verMenu")
    public ResponseEntity<?> verMenu(
            @PathVariable Integer restauranteId,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String orden) {
        try {
            return ResponseEntity.ok(restauranteService.verRestaurante(restauranteId, categoria, orden));
        } catch (SinProductoException e) {
            // El diagrama exige 200 OK aun cuando no haya productos.
            return ResponseEntity.ok(Map.of("mensaje", e.getMessage()));
        }
    }
}
