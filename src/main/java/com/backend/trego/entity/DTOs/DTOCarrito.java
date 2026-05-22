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
    private List<DTOProducto> productos = new ArrayList<>();
    private Double total;
    
    public Integer getIdCarrito() {
        return idCarrito;
    }

    public String getUidCliente() {
        return uidCliente;
    }

    public Integer getIdRestaurante() {
        return idRestaurante;
    }

    public List<DTOProducto> getProductos() {
        return productos;
    }

    public Double getTotal() {
        return total;
    }

    public DTOCarrito(Integer idCarrito, String uidCliente, Integer idRestaurante, List<DTOProducto> productos,
            Double total) {
        this.idCarrito = idCarrito;
        this.uidCliente = uidCliente;
        this.idRestaurante = idRestaurante;
        this.productos = productos;
        this.total = total;
    }


}
