package com.backend.trego.entity.DTOs;

// Cuerpo de la petición para confirmar un pedido (POST /api/pedido/confirmar).
// El cliente se toma del JWT, por eso aquí solo viajan el carrito, la dirección
// de entrega y el restaurante.
public class DTOConfirmarPedidoRequest {

    private DTOCarrito carrito;
    private DTDireccion direccion;
    private Integer restauranteId;

    public DTOCarrito getCarrito() {
        return carrito;
    }

    public DTDireccion getDireccion() {
        return direccion;
    }

    public Integer getRestauranteId() {
        return restauranteId;
    }

    public DTOConfirmarPedidoRequest() {
    }   

    public DTOConfirmarPedidoRequest(DTOCarrito carrito, DTDireccion direccion, Integer restauranteId) {
        this.carrito = carrito;
        this.direccion = direccion;
        this.restauranteId = restauranteId;
    }


}
