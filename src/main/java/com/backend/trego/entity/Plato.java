package com.backend.trego.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

@Entity
public class Plato extends Producto {
    private Integer tiempoPreparacionMinutos;

    @ManyToMany
    private List<Ingrediente> ingredientes = new ArrayList<>();

    protected Plato() {
    }

    public Plato(String nombre, float precio, String descripcion, String urlImagen, Integer tiempoPreparacionMinutos) {
        super(nombre, precio, descripcion, urlImagen);
        this.tiempoPreparacionMinutos = tiempoPreparacionMinutos;
    }

    public Integer getTiempoPreparacionMinutos() {
        return tiempoPreparacionMinutos;
    }

    public void setTiempoPreparacionMinutos(Integer tiempoPreparacionMinutos) {
        this.tiempoPreparacionMinutos = tiempoPreparacionMinutos;
    }

    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public void addIngrediente(Ingrediente ingrediente) {
        this.ingredientes.add(ingrediente);
    }

    public void removeIngrediente(Ingrediente ingrediente) {
        this.ingredientes.remove(ingrediente);
    }
}
