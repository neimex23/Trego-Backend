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
    private DTOProducto producto;

    protected DTOProductoPedido() {
    }

    public DTOProductoPedido(Integer cantidadDisponible, List<DTOIngrediente> ingredientes, String observaciones,
            Integer cantidad, Double subtotal, DTOProducto producto) {
        this.cantidadDisponible = cantidadDisponible;
        if (ingredientes != null) {
            this.ingredientesAQuitar = ingredientes;
        }
        this.observaciones = observaciones;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.producto = producto;
    }

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

    public DTOProducto getProducto() {
        return producto;
    }

    public double getTiempoPreparacion() {
        if (producto == null || producto.getPlato() == null
                || producto.getPlato().getTiempoPreparacionMinutos() == null) {
            return 0;
        }
        return producto.getPlato().getTiempoPreparacionMinutos();
    }

    public static DTOProductoPedido desde(LineaCarrito linea) {
        if (linea == null) {
            return null;
        }
        Integer idRestaurante = linea.getProducto() != null && linea.getProducto().getRestaurante() != null
                ? linea.getProducto().getRestaurante().getIdUsuario()
                : null;
        List<DTOIngrediente> ingredientesDto = new ArrayList<>();
        if (linea.getIngredientesAQuitar() != null) {
            for (var ing : linea.getIngredientesAQuitar()) {
                ingredientesDto.add(new DTOIngrediente(
                        ing.getIdIngrediente(), ing.getNombre(), idRestaurante));
            }
        }
        return new DTOProductoPedido(
                null,
                ingredientesDto,
                linea.getObservaciones(),
                linea.getCantidad(),
                linea.getSubtotal(),
                DTOProducto.desde(linea.getProducto()));
    }
}
