package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOEstadoPago;
import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;
import com.backend.trego.service.PagoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Integración con la pasarela de pagos (MercadoPago).
@RestController
@RequestMapping("/api/pagos")
@CrossOrigin("*")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/preferencia")
    public ResponseEntity<DTOPreferenciaMP> crearPreferencia(@RequestBody DTOPedido pedidoDTO) {
        return ResponseEntity.ok(pagoService.crearPreferencia(pedidoDTO));
    }

    // Webhook que invoca MercadoPago. Debe responder 200 siempre (de lo contrario
    // MP reintenta la notificación); por eso los errores se capturan y se loguean.
    @PostMapping("/webhook")
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
    public ResponseEntity<DTOEstadoPago> consultarEstado(@PathVariable Integer idPedido) {
        return ResponseEntity.ok(pagoService.consultarEstadoPedido(idPedido));
    }
}
