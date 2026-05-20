package com.backend.trego.entity.DTOs;

/**
 * Cuerpo de la petición para agregar un producto al carrito.
 *
 * El front envía un DTOProducto (con su cantidad, observaciones e
 * ingredientes ya ajustados desde la UI) junto al DTORestaurante al que
 * pertenece.
 */
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
