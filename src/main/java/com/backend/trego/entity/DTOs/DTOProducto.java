package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.*;
import com.backend.trego.entity.Articulo;
import com.backend.trego.entity.Combo;
import com.backend.trego.entity.Plato;
import com.backend.trego.entity.Producto;

import java.util.ArrayList;
import java.util.List;


public class DTOProducto {

    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private float precio;
    private String urlImagen;
    private EnumCategoriaProducto categoria;
    private Integer idRestaurante;
    private List<DTOIngrediente> ingredientes = new ArrayList<>();
    private Boolean disponible = true;

    private EnumTipoProducto tipo;
    private DTOPlato plato;
    private DTOArticulo articulo;
    private DTOCombo combo;

    public DTOProducto() {
    }

    public DTOProducto(Integer idProducto, String nombre, String descripcion, float precio, String urlImagen,
            EnumCategoriaProducto categoria, Integer idRestaurante, List<DTOIngrediente> ingredientes) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.urlImagen = urlImagen;
        this.categoria = categoria;
        this.idRestaurante = idRestaurante;
        this.ingredientes = ingredientes;
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

    public Producto toProducto() {
        Producto newProducto = null;
        switch (this.tipo) 
        {       case Plato:
                newProducto = new Plato(this.getNombre(), this.getPrecio(),   this.getDescripcion(), this.getUrlImagen(), this.getPlato().getTiempoPreparacionMinutos());
                break;
                case Articulo:
                    newProducto = new Articulo(this.getNombre(), this.getPrecio(),   this.getDescripcion(), this.getUrlImagen());
                    break;
                case Combo:
                    newProducto = new Combo(this.getNombre(), this.getPrecio(),   this.getDescripcion(), this.getUrlImagen());
                    break;
            default:
                throw new IllegalArgumentException("Tipo de producto no reconocido: " + this.tipo);
        }
        return newProducto;
    }

}
