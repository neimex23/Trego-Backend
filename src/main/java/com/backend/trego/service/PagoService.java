package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;

import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la integración con la pasarela de pagos
 * (MercadoPago) para crear preferencias y procesar webhooks.
 *
 * Las firmas siguen el Documento de Diseño (Tabla 3 - PagoService).
 */
@Service
public class PagoService {

    public PagoService() {
        // TODO: inyectar cliente de MercadoPago y PedidoService
    }

    /**
     * Crea una preferencia de pago en MercadoPago a partir de un pedido.
     */
    public DTOPreferenciaMP crearPreferencia(DTOPedido pedidoDTO) {
        // TODO: implementar
        return null;
    }

    /**
     * Procesa una notificación (webhook) recibida desde MercadoPago.
     */
    public void procesarWebHook(String payload) {
        // TODO: implementar
    }
}
