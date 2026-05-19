package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.*;
import com.backend.trego.entity.Plato;
import com.backend.trego.entity.Producto;

import java.util.ArrayList;
import java.util.List;


public  abstract class DTOProducto {

    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String urlImagen;
    private EnumCategoriaProducto categoria;
    private Integer idRestaurante;
    private List<DTOIngrediente> ingredientes = new ArrayList<>();
    private Boolean disponible;

    public DTOProducto(Integer idProducto, String nombre, String descripcion, Double precio, String urlImagen,
            EnumCategoriaProducto categoria, Integer idRestaurante, List<DTOIngrediente> ingredientes,
            Boolean disponible) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.urlImagen = urlImagen;
        this.categoria = categoria;
        this.idRestaurante = idRestaurante;
        this.ingredientes = ingredientes;
        this.disponible = disponible;
    }

    private EnumTipoProducto tipo;

    public DTOProducto() {
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

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public EnumCategoriaProducto getCategoria() {
        return categoria;
    }

    public void setCategoria(EnumCategoriaProducto categoria) {
        this.categoria = categoria;
    }

    public Integer getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(Integer idRestaurante) {
        this.idRestaurante = idRestaurante;
    }

    public List<DTOIngrediente> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<DTOIngrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public EnumTipoProducto getTipo() {
        return tipo;
    }

    public void setTipo(EnumTipoProducto tipo) {
        this.tipo = tipo;
    }

    public abstract Producto toEntity();
}
