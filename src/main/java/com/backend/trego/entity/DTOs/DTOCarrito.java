package com.backend.trego.entity.DTOs;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO del carrito de compras. Cada línea es un {@link DTOProductoPedido}
 * (producto simplificado + cantidad, observaciones y subtotal).
 */
public class DTOCarrito {

    private Integer idCarrito;
    private String uidCliente;
    private Integer idRestaurante;
    private List<DTOProductoPedido> productos = new ArrayList<>();
    private Double total;

    public DTOCarrito() {
    }

    public DTOCarrito(Integer idCarrito, String uidCliente, Integer idRestaurante,
            List<DTOProductoPedido> productos, Double total) {
        this.idCarrito = idCarrito;
        this.uidCliente = uidCliente;
        this.idRestaurante = idRestaurante;
        if (productos != null) {
            this.productos = productos;
        }
        this.total = total;
    }

    public Integer getIdCarrito() {
        return idCarrito;
    }

    public String getUidCliente() {
        return uidCliente;
    }


    public Integer getIdRestaurante() {
        return idRestaurante;
    }

    public List<DTOProductoPedido> getProductos() {
        return productos;
    }

    public Double getTotal() {
        return total;
    }
}
