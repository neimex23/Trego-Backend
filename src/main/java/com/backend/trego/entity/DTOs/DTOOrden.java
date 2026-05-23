package com.backend.trego.entity.DTOs;


public class DTOOrden {
    private DTDireccion direccion;
    private DTOCarrito carrito;
    private Integer idRestaurante;
    
    public DTDireccion getDireccion() {
        return direccion;
    }
    public DTOCarrito getCarrito() {
        return carrito;
    }
    public Integer getIdRestaurante() {
        return idRestaurante;
    }
    public DTOOrden(DTDireccion direccion, DTOCarrito carrito, Integer idRestaurante) {
        this.direccion = direccion;
        this.carrito = carrito;
        this.idRestaurante = idRestaurante;
    }
}