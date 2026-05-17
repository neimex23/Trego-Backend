package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTORestaurante;

import org.springframework.stereotype.Service;

/**
 * Servicio encargado del envío de notificaciones a clientes y restaurantes
 * (push, email, SMS, etc.).
 *
 * Las firmas siguen el Documento de Diseño (Tabla 5 - NotificacionesService).
 */
@Service
public class NotificacionesService {

    public NotificacionesService() {
        // TODO: inyectar cliente FCM / proveedor de email
    }

    /**
     * Notifica al cliente que su pedido fue confirmado, con el tiempo estimado.
     */
    public void notificarConfirmacionPedido(DTOPedido pedidoDTO, Integer tiempoEstimado) {
        // TODO: implementar
    }

    /**
     * Notifica al cliente que su pedido ya está en camino.
     */
    public void notificarPedidoEnCamino(DTOPedido pedidoDTO, Integer tiempoViaje) {
        // TODO: implementar
    }

    /**
     * Notifica al restaurante que su alta fue aprobada con éxito.
     */
    public void notificarAltaExitosa(DTORestaurante restauranteDTO) {
        // TODO: implementar
    }

    /**
     * Notifica al restaurante que su solicitud de alta fue rechazada, indicando el motivo.
     */
    public void solicitudRechazada(DTORestaurante restauranteDTO, String motivo) {
        // TODO: implementar
    }

    /**
     * Envía un código de verificación por email y devuelve el código generado.
     */
    public String codigoVerificacionEmail(String email) {
        // TODO: implementar
        return null;
    }
}
