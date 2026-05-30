package com.backend.trego.entity.DTOs;

import java.util.ArrayList;
import java.util.List;

import com.backend.trego.entity.LineaCarrito;

public class DTOProductoPedido {
    private Integer cantidadDisponible;
    private List<DTOIngrediente> ingredientesAQuitar = new ArrayList<>();
    private String observaciones;
    private Integer cantidad;
    private Double subtotal;
    private DTOProductoSimplificado producto;

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public List<DTOIngrediente> getIngredientesAQuitar() {
        return ingredientesAQuitar;
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

    public Integer getIdProducto() {
        return producto != null ? producto.getIdProducto() : null;
    }

    public DTOProductoSimplificado getProducto() {
        return producto;
    }

    public double getTiempoPreparacion() {
        return producto != null ? producto.getTiempoPreparacion() : 0;
    }

    public DTOProductoPedido() {
    }

    public DTOProductoPedido(Integer cantidadDisponible, List<DTOIngrediente> ingredientes, String observaciones,
            Integer cantidad, Double subtotal, DTOProductoSimplificado producto) {
        this.cantidadDisponible = cantidadDisponible;
        if (ingredientes != null) {
            this.ingredientesAQuitar = ingredientes;
        }
        this.observaciones = observaciones;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.producto = producto;
    }

    public static DTOProductoPedido desde(LineaCarrito linea) {
        if (linea == null) {
            return null;
        }
        return new DTOProductoPedido(
                null,
                null,
                linea.getObservaciones(),
                linea.getCantidad(),
                linea.getSubtotal(),
                DTOProductoSimplificado.desde(linea.getProducto()));
    }


}
