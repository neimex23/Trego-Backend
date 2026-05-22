package com.backend.trego.entity.DTOs;

// Cuerpo de la petición para confirmar un pedido (POST /api/pedido/confirmar).
// El cliente se toma del JWT, por eso aquí solo viajan el carrito, la dirección
// de entrega y el restaurante.
public class DTOConfirmarPedidoRequest {

    private DTOCarrito carrito;
    private DTODireccion direccion;
    private Integer restauranteId;

    public DTOConfirmarPedidoRequest() {
    }

    public DTOCarrito getCarrito() {
        return carrito;
    }

    public void setCarrito(DTOCarrito carrito) {
        this.carrito = carrito;
    }

    public DTODireccion getDireccion() {
        return direccion;
    }

    public void setDireccion(DTODireccion direccion) {
        this.direccion = direccion;
    }

    public Integer getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(Integer restauranteId) {
        this.restauranteId = restauranteId;
    }
}
