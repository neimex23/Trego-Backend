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
    private Boolean disponible = true;

    private Integer idRestaurante;

    //ProductoPedido
    private Integer cantidadDisponible;
    private List<DTOIngrediente> ingredientes = new ArrayList<>();
    private String observaciones;

    // Datos propios de una línea del carrito (cantidad pedida y subtotal de la línea)
    private Integer cantidad;
    private Double subtotal;


    private EnumTipoProducto tipo;
    private DTOPlato plato;
    private DTOArticulo articulo;
    private DTOCombo combo;

    public DTOProducto(Integer idProducto, String nombre, String descripcion, float precio, String urlImagen,
            EnumCategoriaProducto categoria, Boolean disponible, Integer idRestaurante, Integer cantidadDisponible,
            List<DTOIngrediente> ingredientes, String observaciones, EnumTipoProducto tipo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.urlImagen = urlImagen;
        this.categoria = categoria;
        this.disponible = disponible;
        this.idRestaurante = idRestaurante;
        this.cantidadDisponible = cantidadDisponible;
        this.ingredientes = ingredientes;
        this.observaciones = observaciones;
        this.tipo = tipo;
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

    public String getObservaciones() {
        return observaciones;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public DTOProducto() {
    }

    // Constructor para representar una línea del carrito: datos básicos del
    // producto + la cantidad pedida, las observaciones y el subtotal de la línea.
    public DTOProducto(Integer idProducto, String nombre, String descripcion, float precio, String urlImagen,
            Integer cantidad, String observaciones, Double subtotal) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.urlImagen = urlImagen;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
        this.subtotal = subtotal;
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


    public Producto toProducto() {
        Producto newProducto = null;
        switch (this.tipo) 
        {       case Plato:
                newProducto = new Plato(this.getNombre(), this.getPrecio(),  this.getDescripcion(), this.getUrlImagen(), this.getPlato().getTiempoPreparacionMinutos());
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
