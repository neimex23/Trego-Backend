package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOActualizarEstadoRequest;
import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;
import com.backend.trego.entity.Enums.EnumEstadoPedido;
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
import java.util.List;

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

    @GetMapping("/listarPedidos")
    @Operation(summary = "Listar pedidos del restaurante",
            description = "Devuelve la lista de pedidos del restaurante autenticado, Si no se establece estado los regresados por defecto son los 'Pagado' Permite aplicar un filtro opcional por estado y por producto.")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida correctamente")
    public ResponseEntity<List<DTOPedido>> listarPedidos(
            @Parameter(description = "Filtro por estado del pedido") @RequestParam(required = false) EnumEstadoPedido estado,
            @Parameter(description = "Filtro por ID de un producto específico") @RequestParam(required = false) Integer idProducto) {
        return ResponseEntity.ok(pedidoService.listarPedidosConfirmados(idProducto, estado));
    }

    @GetMapping("/misPedidos")
    @Operation(summary = "Historial de compras del cliente",
            description = "Lista los pedidos del cliente autenticado (fecha, total, restaurante). "
                    + "Excluye pedidos en estado Solicitado o PagoRechazado. "
                    + "Los filtros por fecha, restaurante o nombre se aplican en el frontend.")
    @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no es un cliente")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public ResponseEntity<List<DTOPedido>> listarMisPedidos() {
        return ResponseEntity.ok(pedidoService.listarMisPedidosCliente());
    }

    @PostMapping("/confirmar")
    @Operation(summary = "Confirmar pedido por parte del cliente",
            description = "Recibe la dirección de envío (DTODireccion) y genera una preferencia de pago en MercadoPago con el Carrito Actual. Valida que el carrito no esté vacío, que el restaurante seleccionado sea válido.")
    @ApiResponse(responseCode = "200", description = "Preferencia de pago generada correctamente")
    @ApiResponse(responseCode = "400", description = "Carrito vacío, restaurante inválido o dirección no asociada al cliente")
    public ResponseEntity<DTOPreferenciaMP> confirmarPedido(@RequestBody DTODireccion dirreccionEnvio) {
        DTOPreferenciaMP preferencia = pedidoService.confirmarPedido(dirreccionEnvio);
        return ResponseEntity.ok(preferencia);
    }

	@PatchMapping("/confirmar/{pedidoId}")
    @Operation(summary = "Confirmar pedido de usuario por parte del restaurante", description = "El restaurante confirma un pedido. Calcula tiempos de entrega mediante API externa y notifica al cliente.")
    @ApiResponse(responseCode = "200", description = "Pedido confirmado con exito")
    @ApiResponse(responseCode = "409", description = "El pedido ha sido cancelado previamente")
    @ApiResponse(responseCode = "400", description = "El pedido no está en estado 'Pagado'")
    @ApiResponse(responseCode = "409", description = "El pedido ya está confirmado y en preparación")
    public ResponseEntity<DTOPedido> confirmarPedidoRestaurante(@PathVariable Integer pedidoId) {
        return ResponseEntity.ok(pedidoService.confirmarPedidoPendiente(pedidoId));
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
  // Nueva actualizar estado  
    @PatchMapping("/estado")
    @Operation(summary = "Actualizar estado del pedido", description = "Permite transicionar un pedido a En Camino o Entregado, validando los saltos de estado.")
    @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente")
    @ApiResponse(responseCode = "400", description = "Salto de estado incorrecto")
	public ResponseEntity<DTOPedido> actualizarEstado(@RequestBody DTOActualizarEstadoRequest request) {
        DTOPedido pedidoActualizado = pedidoService.actualizarEstadoPedido(request.getPedido(), request.getEstado());
    	return ResponseEntity.ok(pedidoActualizado);
	}
    
    
}
