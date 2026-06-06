package com.backend.trego.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class ProductoPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProductoPedido;

    private Integer cantidad;
    private float precioSuma;
    private String comentarioCliente;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToMany
    private List<Ingrediente> ingredientesAQuitar = new ArrayList<>();

    protected ProductoPedido() {
    }

    public ProductoPedido(Producto producto, Integer cantidad, float precioSuma, String comentarioCliente) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioSuma = precioSuma;
        this.comentarioCliente = comentarioCliente;
    }

    public Integer getIdProductoPedido() {
        return idProductoPedido;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecioSuma() {
        return precioSuma;
    }

    public void setPrecioSuma(float precioSuma) {
        this.precioSuma = precioSuma;
    }

    public String getComentarioCliente() {
        return comentarioCliente;
    }

    public void setComentarioCliente(String comentarioCliente) {
        this.comentarioCliente = comentarioCliente;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public List<Ingrediente> getIngredientesAQuitar() {
        return ingredientesAQuitar;
    }

    public void setIngredientesAQuitar(List<Ingrediente> ingredientesAQuitar) {
        this.ingredientesAQuitar = ingredientesAQuitar;
    }

    public void addIngredienteAQuitar(Ingrediente ingrediente) {
        this.ingredientesAQuitar.add(ingrediente);
    }

    public void removeIngredienteAQuitar(Ingrediente ingrediente) {
        this.ingredientesAQuitar.remove(ingrediente);
    }
}
