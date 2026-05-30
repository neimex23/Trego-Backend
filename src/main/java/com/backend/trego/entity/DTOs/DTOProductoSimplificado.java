package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Ingrediente;
import com.backend.trego.entity.Plato;
import com.backend.trego.entity.Producto;

import java.util.ArrayList;
import java.util.List;

public class DTOProductoSimplificado {
    private Integer idProducto;
    private Integer idRestaurante;
    private String nombre;
    private Float precio;
    private String urlImagen;
    private Float precioOferta;
    private Integer tiempoPreparacion = 0; // Valor por defecto si no se encuentra el producto o no tiene tiempo definido, seteado cuando es un plato

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

    public Float getPrecioOferta() {
        return precioOferta;
    }

    public List<DTOIngrediente> getIngredientes() {
        return ingredientes;
    }

    public Integer getTiempoPreparacion() {
        return tiempoPreparacion;
    }
    
    public DTOProductoSimplificado() {
    }

    public DTOProductoSimplificado(Integer idProducto, Integer idRestaurante, String nombre, float precio,
            String urlImagen, Float precioOferta, Integer tiempoPreparacion) {
        this.idProducto = idProducto;
        this.idRestaurante = idRestaurante;
        this.nombre = nombre;
        this.precio = precio;
        this.urlImagen = urlImagen;
        this.precioOferta = precioOferta;
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
        DTOProductoSimplificado dto = new DTOProductoSimplificado(
                producto.getIdProducto(),
                idRestaurante,
                producto.getNombre(),
                producto.getPrecio(),
                producto.getUrlImagen(),
                calcularPrecioOferta(producto),
                (producto instanceof Plato) ? ((Plato) producto).getTiempoPreparacionMinutos() : null);

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

    private static Float calcularPrecioOferta(Producto producto) {
        if (!producto.isOfertaActiva() || producto.getOferta() == null) {
            return null;
        }
        float descuento = producto.getOferta().getDescuento();
        return producto.getPrecio() * (1f - descuento / 100f);
    }

 
}
