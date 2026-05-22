package com.backend.trego.entity.DTOs;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO del carrito de compras. Contiene la lista de líneas del carrito
 * (cada DTOProductoCarrito trae su cantidad, observaciones y subtotal) y el
 * total acumulado.
 */
public class DTOCarrito {

    private Integer idCarrito;
    private String uidCliente;
    private Integer idRestaurante;
    private List<DTOProductoCarrito> productos = new ArrayList<>();
    private Double total;

    public DTOCarrito() {
    }

    public Integer getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Integer idCarrito) {
        this.idCarrito = idCarrito;
    }

    public String getUidCliente() {
        return uidCliente;
    }

    public void setUidCliente(String uidCliente) {
        this.uidCliente = uidCliente;
    }

    public Integer getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(Integer idRestaurante) {
        this.idRestaurante = idRestaurante;
    }

    public List<DTOProductoCarrito> getProductos() {
        return productos;
    }

    public void setProductos(List<DTOProductoCarrito> productos) {
        this.productos = productos;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
