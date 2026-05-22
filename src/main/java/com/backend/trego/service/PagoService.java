package com.backend.trego.service;

import com.backend.trego.entity.MPResponse;
import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

// Integración con MercadoPago: crea preferencias de pago y procesa los webhooks.
// La construcción de la preferencia se delega en MercadoPagoService, que ya
// tiene configurado el PreferenceClient.
@Service
public class PagoService {

    private final MercadoPagoService mercadoPagoService;
    private final OrdenesService ordenesService;

    public PagoService(MercadoPagoService mercadoPagoService, OrdenesService ordenesService) {
        this.mercadoPagoService = mercadoPagoService;
        this.ordenesService = ordenesService;
    }

    // Recibe el pedido ya creado (con id), recupera la entidad y genera la
    // preferencia en MercadoPago. Devuelve la preferencia con la URL de checkout.
    public DTOPreferenciaMP crearPreferencia(DTOPedido pedidoDTO) {
        if (pedidoDTO == null || pedidoDTO.getIdPedido() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido inválido para generar la preferencia");
        }

        Pedido pedido = ordenesService.obtenerOFallar(pedidoDTO.getIdPedido());

        MPResponse mpResponse = mercadoPagoService.crearOrden(pedido);

        return new DTOPreferenciaMP(
                mpResponse.getOrderid(),
                mpResponse.getInit_point(),
                mpResponse.getSandbox_init_point(),
                pedido.getIdPedido());
    }

    public void procesarWebHook(String payload) {
        // TODO: implementar
    }
}
