package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Ingrediente;
import com.backend.trego.entity.Plato;
import com.backend.trego.entity.Producto;

import java.util.ArrayList;
import java.util.List;

/**
 * Vista reducida del producto usada en carrito/pedidos.
 * Para el catálogo completo usar {@link DTOProducto}.
 */
public class DTOProductoSimplificado {
    private Integer idProducto;
    private Integer idRestaurante;
    private String nombre;
    private Float precio;
    private String urlImagen;
    private DTOOferta oferta;
    // Solo aplica a Plato. 0 para Articulo/Combo.
    private Integer tiempoPreparacion = 0;

    // Solo se usa en el contexto de estadísticas (productos más vendidos). 0 por defecto.
    private Integer cantidadVendida = 0;

    private List<DTOIngrediente> ingredientes = new ArrayList<>();

    public Integer getIdProducto() {
        return idProducto;
    }

    public Integer getIdRestaurante() {
        return idRestaurante;
    }

    public String getNombre() {
        return nombre;
    }

    public Float getPrecio() {
        return precio;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public DTOOferta getOferta() {
        return oferta;
    }

    public List<DTOIngrediente> getIngredientes() {
        return ingredientes;
    }

    public Integer getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public Integer getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Integer cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public DTOProductoSimplificado() {
    }

    public DTOProductoSimplificado(Integer idProducto, Integer idRestaurante, String nombre, float precio,
            String urlImagen, DTOOferta oferta, Integer tiempoPreparacion) {
        this.idProducto = idProducto;
        this.idRestaurante = idRestaurante;
        this.nombre = nombre;
        this.precio = precio;
        this.urlImagen = urlImagen;
        this.oferta = oferta;
        if (tiempoPreparacion != null) {
            this.tiempoPreparacion = tiempoPreparacion;
        }
    }

    public static DTOProductoSimplificado desde(Producto producto) {
        if (producto == null) {
            return null;
        }
        Integer idRestaurante = producto.getRestaurante() != null
                ? producto.getRestaurante().getIdUsuario()
                : null;

        DTOOferta dtoOferta = producto.isOfertaActiva()
                ? DTOOferta.desde(producto.getOferta())
                : null;

        Integer tiempoPreparacion = (producto instanceof Plato plato)
                ? plato.getTiempoPreparacionMinutos()
                : null;

        DTOProductoSimplificado dto = new DTOProductoSimplificado(
                producto.getIdProducto(),
                idRestaurante,
                producto.getNombre(),
                producto.getPrecio(),
                producto.getUrlImagen(),
                dtoOferta,
                tiempoPreparacion);

        // Los ingredientes solo existen en Plato
        if (producto instanceof Plato plato) {
            for (Ingrediente ing : plato.getIngredientes()) {
                dto.ingredientes.add(new DTOIngrediente(
                        ing.getIdIngrediente(),
                        ing.getNombre(),
                        idRestaurante));
            }
        }

        return dto;
    }

}
