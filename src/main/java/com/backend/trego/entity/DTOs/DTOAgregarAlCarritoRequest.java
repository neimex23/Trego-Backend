package com.backend.trego.entity.DTOs;

// Body para agregar un producto al carrito: la línea a agregar
// (DTOProductoCarrito, con idProducto, cantidad y observaciones) y el
// DTORestaurante al que pertenece el producto.
public class DTOAgregarAlCarritoRequest {

    private DTOProductoCarrito producto;
    private DTORestaurante restaurante;

    public DTOAgregarAlCarritoRequest() {
    }

    public DTOAgregarAlCarritoRequest(DTOProductoCarrito producto, DTORestaurante restaurante) {
        this.producto = producto;
        this.restaurante = restaurante;
    }

    public DTOProductoCarrito getProducto() {
        return producto;
    }

    public void setProducto(DTOProductoCarrito producto) {
        this.producto = producto;
    }

    public DTORestaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(DTORestaurante restaurante) {
        this.restaurante = restaurante;
    }
}
