package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;

import org.springframework.stereotype.Service;

// Integración con MercadoPago: crea preferencias de pago y procesa los webhooks.
@Service
public class PagoService {

    public PagoService() {
        // TODO: inyectar cliente de MercadoPago y PedidoService
    }

    public DTOPreferenciaMP crearPreferencia(DTOPedido pedidoDTO) {
        // TODO: implementar
        return null;
    }

    public void procesarWebHook(String payload) {
        // TODO: implementar
    }
}
