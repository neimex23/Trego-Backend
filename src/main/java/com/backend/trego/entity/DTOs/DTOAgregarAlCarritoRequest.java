package com.backend.trego.entity.DTOs;

// Body para agregar un producto al carrito: el DTOProducto (con cantidad,
// observaciones e ingredientes ya ajustados en la UI) y su DTORestaurante.
public class DTOAgregarAlCarritoRequest {

    private DTOProducto producto;
    private DTORestaurante restaurante;

    public DTOAgregarAlCarritoRequest() {
    }

    public DTOAgregarAlCarritoRequest(DTOProducto producto, DTORestaurante restaurante) {
        this.producto = producto;
        this.restaurante = restaurante;
    }

    public DTOProducto getProducto() {
        return producto;
    }

    public void setProducto(DTOProducto producto) {
        this.producto = producto;
    }

    public DTORestaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(DTORestaurante restaurante) {
        this.restaurante = restaurante;
    }
}
