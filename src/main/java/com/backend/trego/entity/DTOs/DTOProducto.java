package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.EnumCategoriaProducto;
import com.backend.trego.entity.Enums.EnumTipoProducto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO de salida del catálogo (menú, listados, respuestas de alta/edición).
 * Para requests usar {@link DTOCrearProductoRequest} o {@link DTOModificarProductoRequest}.
 * Para carrito y pedidos usar {@link DTOProductoPedido}.
 */
public class DTOProducto {

    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private float precio;
    private String urlImagen;
    private EnumCategoriaProducto categoria;
    private Boolean disponible;
    private Integer idRestaurante;
    private List<DTOIngrediente> ingredientes = new ArrayList<>();
    private EnumTipoProducto tipo;
    private DTOOferta oferta;
    private DTOPlato plato;
    private DTOArticulo articulo;
    private DTOCombo combo;
    private Integer idSubCategoria;
    private DTOSubCategoria subCategoria;

    protected DTOProducto() {
    }

    public DTOProducto(Integer idProducto, String nombre, String descripcion, float precio, String urlImagen,
            EnumCategoriaProducto categoria, Boolean disponible, Integer idRestaurante,
            List<DTOIngrediente> ingredientes, EnumTipoProducto tipo, DTOOferta oferta, DTOPlato plato,
            DTOArticulo articulo, DTOCombo combo, DTOSubCategoria subCategoria) {
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
        this.subCategoria = subCategoria;
        if (subCategoria != null) {
            this.idSubCategoria = subCategoria.getIdSubCategoria();
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

    public Integer getIdSubCategoria() {
        return idSubCategoria;
    }

    public DTOSubCategoria getSubCategoria() {
        return subCategoria;
    }
}
