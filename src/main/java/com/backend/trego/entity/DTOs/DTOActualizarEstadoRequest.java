package com.backend.trego.entity.DTOs;
import com.backend.trego.entity.Enums.EnumEstadoPedido;

// Payload para PATCH /api/pedido/estado. Lleva el pedido a transicionar y el
// nuevo estado como String (se parsea contra EnumEstadoPedido en el service).
public class DTOActualizarEstadoRequest {

    private DTOPedido pedido;
    private EnumEstadoPedido estado;

    public DTOActualizarEstadoRequest() {
    }

    public DTOActualizarEstadoRequest(DTOPedido pedido, EnumEstadoPedido estado) {
        this.pedido = pedido;
        this.estado = estado;
    }

    public DTOPedido getPedido() {
        return pedido;
    }

    public EnumEstadoPedido getEstado() {
        return estado;
    }
}
