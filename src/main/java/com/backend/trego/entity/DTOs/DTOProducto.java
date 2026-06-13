package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Articulo;
import com.backend.trego.entity.Combo;
import com.backend.trego.entity.Ingrediente;
import com.backend.trego.entity.Plato;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.Enums.EnumCategoriaProducto;
import com.backend.trego.entity.Enums.EnumTipoProducto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO de salida del catálogo (menú, listados, respuestas de alta/edición).
 * Para requests usar {@link DTOCrearProductoRequest} o
 * {@link DTOModificarProductoRequest}.
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
    private boolean ofertaActiva;
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
            DTOArticulo articulo, DTOCombo combo, DTOSubCategoria subCategoria, Boolean ofertaActiva) {
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
        if (this.getOferta() == null){
            this.ofertaActiva = false;
        } else{
            this.ofertaActiva = ofertaActiva;
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

    public static DTOProducto desde(Producto producto) {
        if (producto == null) {
            return null;
        }

        EnumCategoriaProducto categoria = producto.getSubCategoria() != null
                ? producto.getSubCategoria().getCategoria()
                : null;

        Integer idRestaurante = producto.getRestaurante() != null
                ? producto.getRestaurante().getIdUsuario()
                : null;

        return new DTOProducto(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getUrlImagen(),
                categoria,
                producto.getDisponible(),
                idRestaurante,
                mapearIngredientes(producto, idRestaurante),
                tipoDe(producto),
                DTOOferta.desde(producto.getOferta()),
                mapearPlato(producto),
                mapearArticulo(producto),
                mapearCombo(producto),
                DTOSubCategoria.desde(producto.getSubCategoria()),
                producto.isOfertaActiva());
    }

    private static EnumTipoProducto tipoDe(Producto producto) {
        if (producto instanceof Plato) {
            return EnumTipoProducto.Plato;
        }
        if (producto instanceof Articulo) {
            return EnumTipoProducto.Articulo;
        }
        if (producto instanceof Combo) {
            return EnumTipoProducto.Combo;
        }
        return null;
    }

    private static List<DTOIngrediente> mapearIngredientes(Producto producto, Integer idRestaurante) {
        List<DTOIngrediente> ingredientesDto = new ArrayList<>();
        if (producto instanceof Plato plato && plato.getIngredientes() != null) {
            for (Ingrediente ing : plato.getIngredientes()) {
                ingredientesDto.add(new DTOIngrediente(
                        ing.getIdIngrediente(),
                        ing.getNombre(),
                        idRestaurante));
            }
        }
        return ingredientesDto;
    }

    private static DTOPlato mapearPlato(Producto producto) {
        if (producto instanceof Plato plato) {
            return new DTOPlato(plato.getTiempoPreparacionMinutos());
        }
        return null;
    }

    private static DTOArticulo mapearArticulo(Producto producto) {
        if (producto instanceof Articulo) {
            return new DTOArticulo();
        }
        return null;
    }

    private static DTOCombo mapearCombo(Producto producto) {
        if (producto instanceof Combo combo) {
            List<DTOCombo.ProductoIncluido> productosIncluidos = combo.getProductosIncluidos().stream()
                    .map(prod -> new DTOCombo.ProductoIncluido(prod.getIdProducto(), prod.getNombre()))
                    .collect(Collectors.toList());
            return new DTOCombo(productosIncluidos);
        }
        return null;
    }
}
