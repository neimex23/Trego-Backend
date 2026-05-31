package com.backend.trego.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

@Entity
public class Combo extends Producto {

    @ManyToMany
    private List<Producto> productosIncluidos = new ArrayList<>();

    protected Combo() {
    }

    public Combo(String nombre, float precio, String descripcion, String urlImagen) {
        super(nombre, precio, descripcion, urlImagen);
    }

    public List<Producto> getProductosIncluidos() {
        return productosIncluidos;
    }

    public void addProductoIncluido(Producto producto) {
        this.productosIncluidos.add(producto);
    }

    public void removeProductoIncluido(Producto producto) {
        this.productosIncluidos.remove(producto);
    }

    public void setProductosIncluidos(List<Producto> productosIncluidos) {
        this.productosIncluidos = productosIncluidos;
    }
}
