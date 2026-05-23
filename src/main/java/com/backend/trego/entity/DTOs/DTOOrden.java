package com.backend.trego.entity.DTOs;


public class DTOOrden {
    private DTODireccion direccion;
    private DTOCarrito carrito;
    private Integer idRestaurante;
    
    public DTODireccion getDireccion() {
        return direccion;
    }
    public DTOCarrito getCarrito() {
        return carrito;
    }
    public Integer getIdRestaurante() {
        return idRestaurante;
    }
    public DTOOrden(DTODireccion direccion, DTOCarrito carrito, Integer idRestaurante) {
        this.direccion = direccion;
        this.carrito = carrito;
        this.idRestaurante = idRestaurante;
    }
}