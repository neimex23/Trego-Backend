package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.*;
import com.backend.trego.entity.Articulo;
import com.backend.trego.entity.Combo;
import com.backend.trego.entity.Plato;
import com.backend.trego.entity.Producto;

import java.util.ArrayList;
import java.util.List;

/** DTO completo del catálogo (menú, alta/edición de productos). Para carrito y pedidos usar {@link DTOProductoPedido}. */
public class DTOProducto {

    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private float precio;
    private String urlImagen;
    private EnumCategoriaProducto categoria;
    private Boolean disponible = true;
    private Integer idRestaurante;
    private Integer cantidadDisponible;
    private List<DTOIngrediente> ingredientes = new ArrayList<>();
    private EnumTipoProducto tipo;
    private DTOPlato plato;
    private DTOArticulo articulo;
    private DTOCombo combo;

    public DTOProducto() {
    }

    public DTOProducto(Integer idProducto, String nombre, String descripcion, float precio, String urlImagen,
            EnumCategoriaProducto categoria, Boolean disponible, Integer idRestaurante, Integer cantidadDisponible,
            List<DTOIngrediente> ingredientes, EnumTipoProducto tipo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.urlImagen = urlImagen;
        this.categoria = categoria;
        this.disponible = disponible;
        this.idRestaurante = idRestaurante;
        this.cantidadDisponible = cantidadDisponible;
        if (ingredientes != null) {
            this.ingredientes = ingredientes;
        }
        this.tipo = tipo;
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
        if (ingredientes != null) {
            this.ingredientes = ingredientes;
        }
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

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public EnumCategoriaProducto getCategoria() {
        return categoria;
    }

    public Integer getIdRestaurante() {
        return idRestaurante;
    }

    public List<DTOIngrediente> getIngredientes() {
        return ingredientes;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public EnumTipoProducto getTipo() {
        return tipo;
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

    public Producto toProducto() {
        Producto newProducto = null;
        switch (this.tipo) {
            case Plato:
                newProducto = new Plato(this.getNombre(), this.getPrecio(), this.getDescripcion(),
                        this.getUrlImagen(), this.getPlato().getTiempoPreparacionMinutos());
                break;
            case Articulo:
                newProducto = new Articulo(this.getNombre(), this.getPrecio(), this.getDescripcion(),
                        this.getUrlImagen());
                break;
            case Combo:
                newProducto = new Combo(this.getNombre(), this.getPrecio(), this.getDescripcion(),
                        this.getUrlImagen());
                break;
            default:
                throw new IllegalArgumentException("Tipo de producto no reconocido: " + this.tipo);
        }
        return newProducto;
    }
}
