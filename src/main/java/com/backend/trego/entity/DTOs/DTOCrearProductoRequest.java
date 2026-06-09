package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.EnumTipoProducto;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

// Request para dar de alta un producto en el restaurante autenticado.
public class DTOCrearProductoRequest {

    private String nombre;
    private String descripcion;
    @Positive(message = "El precio debe ser mayor a cero")
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

    public DTOCrearProductoRequest(String nombre, String descripcion, float precio, String urlImagen,
            Boolean disponible, Integer idSubCategoria, EnumTipoProducto tipo,
            List<DTOIngrediente> ingredientes, DTOPlato plato, DTOArticulo articulo, DTOCombo combo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.urlImagen = urlImagen;
        if (disponible != null) {
            this.disponible = disponible;
        }
        this.idSubCategoria = idSubCategoria;
        this.tipo = tipo;
        if (ingredientes != null) {
            this.ingredientes = ingredientes;
        }
        this.plato = plato;
        this.articulo = articulo;
        this.combo = combo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public float getPrecio() {
        return precio;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public Integer getIdSubCategoria() {
        return idSubCategoria;
    }

    public EnumTipoProducto getTipo() {
        return tipo;
    }

    public List<DTOIngrediente> getIngredientes() {
        return ingredientes;
    }

    public DTOPlato getPlato() {
        return plato;
    }

    public DTOArticulo getArticulo() {
        return articulo;
    }

    public DTOCombo getCombo() {
        return combo;
    }
}
