package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOConfirmarPedidoRequest;
import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;
import com.backend.trego.exception.SinProductoException;
import com.backend.trego.service.PedidoService;
import com.backend.trego.service.RestauranteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Endpoints de pedidos.
@RestController
@RequestMapping("/api/pedido")
@CrossOrigin("*")
@Tag(name = "Pedidos", description = "Confirmación de pedidos y consulta del menú del restaurante seleccionado")
public class PedidoController {

    private final PedidoService pedidoService;
    private final RestauranteService restauranteService;

    public PedidoController(PedidoService pedidoService, RestauranteService restauranteService) {
        this.pedidoService = pedidoService;
        this.restauranteService = restauranteService;
    }

    @PostMapping("/confirmar")
    @Operation(summary = "Confirmar pedido",
            description = "Recibe el carrito, la dirección de entrega y el restaurante. Persiste el pedido en estado pendiente y devuelve la preferencia de pago de MercadoPago con la URL de checkout.")
    @ApiResponse(responseCode = "200", description = "Preferencia de pago generada correctamente")
    @ApiResponse(responseCode = "400", description = "Carrito vacío, restaurante inválido o dirección no asociada al cliente")
    public ResponseEntity<DTOPreferenciaMP> confirmarPedido(@RequestBody DTOConfirmarPedidoRequest request) {
        DTOPreferenciaMP preferencia = pedidoService.confirmarPedido(
                request.getCarrito(),
                request.getDireccion(),
                String.valueOf(request.getRestauranteId()));
        return ResponseEntity.ok(preferencia);
    }

    @PostMapping("/reembolsar")
    @Operation(summary = "Reembolsar pedido",
            description = "Recibe el DTOPedido a reembolsar. Recupera el pago asociado (idTransaccion de MercadoPago) y dispara el reembolso en MP usando una idempotencyKey")
    @ApiResponse(responseCode = "200", description = "Reembolso ejecutado correctamente; el pedido queda en estado Reembolsado")
    @ApiResponse(responseCode = "400", description = "DTOPedido inválido o el pedido no tiene un pago asociado")
    @ApiResponse(responseCode = "409", description = "El pedido ya estaba reembolsado")
    public ResponseEntity<DTOPedido> reembolsarPedido(@RequestBody DTOPedido pedidoDTO) {
        return ResponseEntity.ok(pedidoService.reembolsarPedido(pedidoDTO));
    }

    // Ver el menú de un restaurante. restauranteId identifica el restaurante que
    // seleccionó el cliente, para no mezclar menús de otros locales.
    @GetMapping("/restaurante/{restauranteId}/verMenu")
    @Operation(summary = "Ver menú del restaurante",
            description = "Devuelve los productos publicados por el restaurante. Permite filtrar por categoría y ordenar el resultado. Si el restaurante no tiene productos, retorna 200 con un mensaje informativo (no 404), según el diagrama de flujo del caso de uso.")
    @ApiResponse(responseCode = "200", description = "Menú obtenido. Puede contener un mensaje si el restaurante no tiene productos publicados.")
    @ApiResponse(responseCode = "404", description = "Restaurante inexistente")
    public ResponseEntity<?> verMenu(
            @Parameter(description = "Identificador del restaurante a consultar") @PathVariable Integer restauranteId,
            @Parameter(description = "Categoría por la cual filtrar los productos (opcional)") @RequestParam(required = false) String categoria,
            @Parameter(description = "Criterio de ordenamiento de los productos (opcional)") @RequestParam(required = false) String orden) {
        try {
            return ResponseEntity.ok(restauranteService.verRestaurante(restauranteId, categoria, orden));
        } catch (SinProductoException e) {
            // El diagrama exige 200 OK aun cuando no haya productos.
            return ResponseEntity.ok(Map.of("mensaje", e.getMessage()));
        }
    }
}
