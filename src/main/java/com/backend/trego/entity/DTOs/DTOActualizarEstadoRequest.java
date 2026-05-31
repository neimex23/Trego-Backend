package com.backend.trego.entity.DTOs;

// Payload para PATCH /api/pedido/estado. Lleva el pedido a transicionar y el
// nuevo estado como String (se parsea contra EnumEstadoPedido en el service).
public class DTOActualizarEstadoRequest {

    private DTOPedido pedido;
    private String estado;

    public DTOActualizarEstadoRequest() {
    }

    public DTOActualizarEstadoRequest(DTOPedido pedido, String estado) {
        this.pedido = pedido;
        this.estado = estado;
    }

    public DTOPedido getPedido() {
        return pedido;
    }

    public void setPedido(DTOPedido pedido) {
        this.pedido = pedido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
