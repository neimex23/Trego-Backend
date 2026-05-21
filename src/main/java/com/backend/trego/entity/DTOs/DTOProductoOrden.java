package com.backend.trego.entity.DTOs;

import java.math.BigDecimal;

// Producto dentro de una orden, usado para armar la preferencia de MercadoPago.
public class DTOProductoOrden {
    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private int cantidad;
    private BigDecimal precio;

    public DTOProductoOrden() {
    }

    public DTOProductoOrden(Integer idProducto, String nombre, String descripcion, int cantidad, BigDecimal precio) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
