package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.*;
import com.backend.trego.entity.Articulo;
import com.backend.trego.entity.Combo;
import com.backend.trego.entity.Plato;
import com.backend.trego.entity.Producto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO completo del catálogo (menú, alta/edición de productos).
 * Para carrito y pedidos usar {@link DTOProductoPedido}.
 *
 * El campo {@code tipo} determina cuál de {@code plato}, {@code articulo} o {@code combo}
 * debe estar poblado. Los otros dos se ignoran.
 */
public class DTOProducto {

    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private float precio;
    private String urlImagen;
    private EnumCategoriaProducto categoria;
    private Boolean disponible = true;
    private Integer idRestaurante;
    private List<DTOIngrediente> ingredientes = new ArrayList<>();
    private EnumTipoProducto tipo;
    private DTOOferta oferta;
    private DTOPlato plato;
    private DTOArticulo articulo;
    private DTOCombo combo;


    public DTOProducto() {
    }


    public DTOProducto(Integer idProducto, String nombre, String descripcion, float precio, String urlImagen,
            EnumCategoriaProducto categoria, Boolean disponible, Integer idRestaurante,
            List<DTOIngrediente> ingredientes, EnumTipoProducto tipo, DTOOferta oferta, DTOPlato plato,
            DTOArticulo articulo, DTOCombo combo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.urlImagen = urlImagen;
        this.categoria = categoria;
        this.disponible = disponible;
        this.idRestaurante = idRestaurante;
        if (ingredientes != null) {
            this.ingredientes = ingredientes;
        }
        this.tipo = tipo;
        this.oferta = oferta;
        this.plato = plato;
        this.articulo = articulo;
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

    public DTOOferta getOferta() {
        return oferta;
    }

    /**
     * Construye la entidad Producto correspondiente al tipo.
     * NOTA: este método solo arma los campos propios. La resolución de ingredientes
     * del Plato y de productos incluidos en el Combo (que requieren acceso al repo)
     * queda a cargo del service.
     */
    public Producto toProducto() {
        if (this.tipo == null) {
            throw new IllegalArgumentException("El tipo de producto es obligatorio");
        }
        Producto newProducto;
        switch (this.tipo) {
            case Plato:
                if (this.plato == null || this.plato.getTiempoPreparacionMinutos() == null) {
                    throw new IllegalArgumentException("Plato requiere DTOPlato con tiempoPreparacionMinutos");
                }
                newProducto = new Plato(this.nombre, this.precio, this.descripcion,
                        this.urlImagen, this.plato.getTiempoPreparacionMinutos());
                break;
            case Articulo:
                newProducto = new Articulo(this.nombre, this.precio, this.descripcion, this.urlImagen);
                break;
            case Combo:
                newProducto = new Combo(this.nombre, this.precio, this.descripcion, this.urlImagen);
                break;
            default:
                throw new IllegalArgumentException("Tipo de producto no reconocido: " + this.tipo);
        }
        if (this.disponible != null) {
            newProducto.setDisponible(this.disponible);
        }
        return newProducto;
    }
}
