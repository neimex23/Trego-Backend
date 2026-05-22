package com.backend.trego.entity.DTOs;

/**
 * Línea del carrito tal como viaja hacia/desde el front: la referencia al
 * producto (idProducto + datos básicos para mostrar) junto con la cantidad,
 * las observaciones y el subtotal de la línea.
 *
 * A diferencia de DTOProducto (que describe el producto en sí), este DTO
 * representa un ítem del carrito, por eso es el que se usa para agregar,
 * modificar la cantidad y eliminar productos del carrito.
 */
public class DTOProductoCarrito {

    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private float precio;
    private String urlImagen;

    private Integer cantidad;
    private String observaciones;
    private Double subtotal;

    public DTOProductoCarrito() {
    }

    public DTOProductoCarrito(Integer idProducto, Integer cantidad, String observaciones) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
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

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
