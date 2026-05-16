package com.backend.trego.entity;

import com.backend.trego.entity.Enums.EnumCategoriaProducto;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SubCategoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSubCategoria;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private EnumCategoriaProducto categoria;

    private String urlImagen;

    protected SubCategoria() {
    }

    public SubCategoria(String nombre, EnumCategoriaProducto categoria, String urlImagen) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.urlImagen = urlImagen;
    }

    public int getIdSubCategoria() {
        return idSubCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public EnumCategoriaProducto getCategoria() {
        return categoria;
    }

    public void setCategoria(EnumCategoriaProducto categoria) {
        this.categoria = categoria;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }
}
