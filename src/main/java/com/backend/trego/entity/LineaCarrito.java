package com.backend.trego.entity;

import com.backend.trego.entity.DTOs.DTOProducto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

// Un ítem del carrito: un producto con su cantidad. El subtotal es cantidad * precio.
@Entity
public class LineaCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLinea;

    @ManyToOne
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private int cantidad;

    private String observaciones;

    protected LineaCarrito() {
    }

    public LineaCarrito(Carrito carrito, Producto producto, int cantidad) {
        this.carrito = carrito;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public LineaCarrito(Carrito carrito, Producto producto, int cantidad, String observaciones) {
        this.carrito = carrito;
        this.producto = producto;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
    }

    public Integer getIdLinea() {
        return idLinea;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public double getSubtotal() {
        if (producto == null) {
            return 0.0;
        }
        return producto.getPrecio() * cantidad;
    }

    // Pasa la línea a DTOProducto: datos básicos del producto + la
    // cantidad, observaciones y subtotal propios de esta línea del carrito.
    public DTOProducto toDTO() {
        Integer idProducto = null;
        String nombre = null;
        String descripcion = null;
        float precio = 0f;
        String urlImagen = null;
        if (this.producto != null) {
            idProducto = this.producto.getIdProducto();
            nombre = this.producto.getNombre();
            descripcion = this.producto.getDescripcion();
            precio = this.producto.getPrecio();
            urlImagen = this.producto.getUrlImagen();
        }
        return new DTOProducto(idProducto, nombre, descripcion, precio, urlImagen,
                this.cantidad, this.observaciones, getSubtotal());
    }
}
