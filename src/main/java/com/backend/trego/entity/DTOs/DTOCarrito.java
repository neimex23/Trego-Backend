package com.backend.trego.entity.DTOs;

import java.util.ArrayList;
import java.util.List;

public class DTOCarrito {

    private Integer idCarrito;
    private String uidCliente;
    private Integer idRestaurante;
    private List<DTOProducto> productos = new ArrayList<>();
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

    public List<DTOProducto> getProductos() {
        return productos;
    }

    public void setProductos(List<DTOProducto> productos) {
        this.productos = productos;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
