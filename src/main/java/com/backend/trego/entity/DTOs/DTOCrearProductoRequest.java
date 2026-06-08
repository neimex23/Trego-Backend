package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.EnumTipoProducto;

import java.util.ArrayList;
import java.util.List;

// Request para dar de alta un producto en el restaurante autenticado.
public class DTOCrearProductoRequest {

    private String nombre;
    private String descripcion;
    private float precio;
    private String urlImagen;
    private Boolean disponible = true;
    private Integer idSubCategoria;
    private EnumTipoProducto tipo;
    private List<DTOIngrediente> ingredientes = new ArrayList<>();
    private DTOPlato plato;
    private DTOArticulo articulo;
    private DTOCombo combo;

    protected DTOCrearProductoRequest() {
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

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public Integer getIdSubCategoria() {
        return idSubCategoria;
    }

    public void setIdSubCategoria(Integer idSubCategoria) {
        this.idSubCategoria = idSubCategoria;
    }

    public EnumTipoProducto getTipo() {
        return tipo;
    }

    public void setTipo(EnumTipoProducto tipo) {
        this.tipo = tipo;
    }

    public List<DTOIngrediente> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<DTOIngrediente> ingredientes) {
        this.ingredientes = ingredientes != null ? ingredientes : new ArrayList<>();
    }

    public DTOPlato getPlato() {
        return plato;
    }

    public void setPlato(DTOPlato plato) {
        this.plato = plato;
    }

    public DTOArticulo getArticulo() {
        return articulo;
    }

    public void setArticulo(DTOArticulo articulo) {
        this.articulo = articulo;
    }

    public DTOCombo getCombo() {
        return combo;
    }

    public void setCombo(DTOCombo combo) {
        this.combo = combo;
    }
}
