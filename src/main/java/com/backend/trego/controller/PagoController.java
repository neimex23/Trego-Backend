package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOEstadoPago;
import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;
import com.backend.trego.service.PagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Integración con la pasarela de pagos (MercadoPago).
@RestController
@RequestMapping("/api/pagos")
@CrossOrigin("*")
@Tag(name = "Pagos", description = "Integración con MercadoPago: creación de preferencia, recepción del webhook y consulta del estado del pago")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/preferencia")
    @Operation(summary = "Crear preferencia de pago",
            description = "Genera una preferencia en MercadoPago a partir del pedido recibido y devuelve el identificador y la URL de checkout que debe abrir el cliente.")
    @ApiResponse(responseCode = "200", description = "Preferencia creada correctamente")
    @ApiResponse(responseCode = "500", description = "Error al comunicarse con MercadoPago")
    public ResponseEntity<DTOPreferenciaMP> crearPreferencia(@RequestBody DTOPedido pedidoDTO) {
        return ResponseEntity.ok(pagoService.crearPreferencia(pedidoDTO));
    }

    // Webhook que invoca MercadoPago. Debe responder 200 siempre (de lo contrario
    // MP reintenta la notificación); por eso los errores se capturan y se loguean.
    @PostMapping("/webhook")
    @Operation(summary = "Webhook de MercadoPago",
            description = "Endpoint invocado por MercadoPago para notificar cambios en el estado del pago. Actualiza el pedido asociado. Siempre responde 200 para evitar reintentos; los errores se loguean en el servidor.")
    @ApiResponse(responseCode = "200", description = "Notificación recibida (independiente del resultado del procesamiento)")
    public ResponseEntity<Void> procesarWebHook(@RequestBody(required = false) String payload) {
        try {
            pagoService.procesarWebHook(payload);
        } catch (Exception e) {
            System.err.println("Error procesando webhook de MercadoPago: " + e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    // El front consulta este endpoint al volver del checkout para saber si el
    // webhook ya confirmó el pago del pedido.
    @GetMapping("/estado/{idPedido}")
    @Operation(summary = "Consultar estado del pago",
            description = "Permite al front conocer si el webhook ya confirmó el pago del pedido. Se utiliza en la pantalla de retorno desde el checkout de MercadoPago.")
    @ApiResponse(responseCode = "200", description = "Estado actual del pago del pedido")
    @ApiResponse(responseCode = "404", description = "Pedido inexistente")
    public ResponseEntity<DTOEstadoPago> consultarEstado(
            @Parameter(description = "Identificador del pedido a consultar") @PathVariable Integer idPedido) {
        return ResponseEntity.ok(pagoService.consultarEstadoPedido(idPedido));
    }
}
