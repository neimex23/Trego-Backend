package com.backend.trego.entity;

import com.backend.trego.entity.DTOs.DTOProductoPedido;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

    private Integer cantidad;

    private float precioUnitario;

    private String observaciones;

    @ManyToMany
    @JoinTable(
            name = "linea_carrito_ingredientes_a_quitar",
            joinColumns = @JoinColumn(name = "linea_carrito_id"),
            inverseJoinColumns = @JoinColumn(name = "ingrediente_id"))
    private List<Ingrediente> ingredientesAQuitar = new ArrayList<>();

    protected LineaCarrito() {
    }

    public LineaCarrito(Carrito carrito, Producto producto, Integer cantidad, float precioUnitario) {
        this.carrito = carrito;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public LineaCarrito(Carrito carrito, Producto producto, Integer cantidad, String observaciones, float precioUnitario) {
        this.carrito = carrito;
        this.producto = producto;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
        this.precioUnitario = precioUnitario;
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

    public List<Ingrediente> getIngredientesAQuitar() {
        return ingredientesAQuitar;
    }

    public void setIngredientesAQuitar(List<Ingrediente> ingredientesAQuitar) {
        this.ingredientesAQuitar = ingredientesAQuitar != null ? ingredientesAQuitar : new ArrayList<>();
    }

    public float getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(float precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return precioUnitario * cantidad;
    }

    public DTOProductoPedido toDTO() {
        return DTOProductoPedido.desde(this);
    }
}
