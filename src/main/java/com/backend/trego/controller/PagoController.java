package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;
import com.backend.trego.service.PagoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints REST para la integración con la pasarela de pagos..
 */
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/preferencia")
    public ResponseEntity<DTOPreferenciaMP> crearPreferencia(@RequestBody DTOPedido pedidoDTO) {
        // TODO: implementar
        return ResponseEntity.ok(pagoService.crearPreferencia(pedidoDTO));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> procesarWebHook(@RequestBody String payload) {
        // TODO: implementar
        pagoService.procesarWebHook(payload);
        return ResponseEntity.ok().build();
    }
}
