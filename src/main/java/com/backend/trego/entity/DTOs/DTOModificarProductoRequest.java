package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.EnumTipoProducto;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

// Request para modificar un producto del restaurante autenticado.
public class DTOModificarProductoRequest {

    private Integer idProducto;
    private String nombre;
    private String descripcion;
    @Positive(message = "El precio debe ser mayor a cero")
    private float precio;
    private String urlImagen;
    private Boolean disponible;
    private Integer idSubCategoria;
    private EnumTipoProducto tipo;
    private List<DTOIngrediente> ingredientes = new ArrayList<>();
    private DTOPlato plato;
    private DTOCombo combo;

    protected DTOModificarProductoRequest() {
    }

    public DTOModificarProductoRequest(Integer idProducto, String nombre, String descripcion, float precio,
            String urlImagen, Boolean disponible, Integer idSubCategoria, EnumTipoProducto tipo,
            List<DTOIngrediente> ingredientes, DTOPlato plato, DTOCombo combo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.urlImagen = urlImagen;
        this.disponible = disponible;
        this.idSubCategoria = idSubCategoria;
        this.tipo = tipo;
        if (ingredientes != null) {
            this.ingredientes = ingredientes;
        }
        this.plato = plato;
        this.combo = combo;
    }

    public Integer getIdProducto() {
        return idProducto;
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

    public DTOCombo getCombo() {
        return combo;
    }
}
