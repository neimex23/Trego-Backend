package com.backend.trego.entity.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Request para que un cliente cree un reclamo sobre un pedido entregado.
public class DTOCrearReclamoRequest {

    @NotNull(message = "El ID del pedido es obligatorio")
    private Integer idPedido;

    @NotBlank(message = "El motivo del reclamo es obligatorio")
    private String texto;

    protected DTOCrearReclamoRequest() {
    }

    public DTOCrearReclamoRequest(Integer idPedido, String texto) {
        this.idPedido = idPedido;
        this.texto = texto;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public String getTexto() {
        return texto;
    }
}
