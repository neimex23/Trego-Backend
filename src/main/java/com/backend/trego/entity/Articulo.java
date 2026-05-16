package com.backend.trego.entity;

import jakarta.persistence.Entity;

@Entity
public class Articulo extends Producto {

    protected Articulo() {
    }

    public Articulo(String nombre, float precio, String descripcion, String urlImagen) {
        super(nombre, precio, descripcion, urlImagen);
    }
}
