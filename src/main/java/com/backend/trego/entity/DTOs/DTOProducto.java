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
    // Entrada en alta/edición: id de la SubCategoria a la que se cuelga el producto.
    private Integer idSubCategoria;
    // Salida en listado: subcategoria resuelta a DTO.
    private DTOSubCategoria subCategoria;


    public DTOProducto() {
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

    public List<DTOIngrediente> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<DTOIngrediente> ingredientes) {
        this.ingredientes = ingredientes != null ? ingredientes : new ArrayList<>();
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

    public DTOOferta getOferta() {
        return oferta;
    }

    public Integer getIdSubCategoria() {
        return idSubCategoria;
    }

    public void setIdSubCategoria(Integer idSubCategoria) {
        this.idSubCategoria = idSubCategoria;
    }

    public DTOSubCategoria getSubCategoria() {
        return subCategoria;
    }

    public void setSubCategoria(DTOSubCategoria subCategoria) {
        this.subCategoria = subCategoria;
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
