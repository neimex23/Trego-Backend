package com.backend.trego.entity;

import com.backend.trego.entity.DTOs.DTOProducto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Representa una línea (ítem) dentro de un Carrito. Cada línea contiene un
 * producto y la cantidad de ese producto. El subtotal se calcula como
 * cantidad * precio del producto.
 */
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

    /**
     * Subtotal de la línea: precio del producto x cantidad.
     */
    public double getSubtotal() {
        if (producto == null) {
            return 0.0;
        }
        return producto.getPrecio() * cantidad;
    }

    /**
     * Convierte esta línea a un DTOProducto poblando los campos auxiliares
     * cantidad y observaciones del carrito. Solo se rellenan los campos
     * básicos del producto; tipo/plato/articulo/combo quedan en null y los
     * llenará quien lo requiera.
     */
    public DTOProducto toDTO() {
        DTOProducto dto = new DTOProducto();
        if (this.producto != null) {
            dto.setIdProducto(this.producto.getIdProducto());
            dto.setNombre(this.producto.getNombre());
            dto.setDescripcion(this.producto.getDescripcion());
            dto.setPrecio(this.producto.getPrecio());
            dto.setUrlImagen(this.producto.getUrlImagen());
        }
        dto.setCantidad(this.cantidad);
        dto.setObservaciones(this.observaciones);
        return dto;
    }
}
