package com.backend.trego.entity.DTOs;

// Body para agregar un producto al carrito: la línea a agregar
// (DTOProductoCarrito, con idProducto, cantidad y observaciones) y el
// DTORestaurante al que pertenece el producto.
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
    public DTORestaurante getRestaurante() {
        return restaurante;
    }
}
