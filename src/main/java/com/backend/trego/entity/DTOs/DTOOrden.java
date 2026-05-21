package com.backend.trego.entity.DTOs;

import java.util.ArrayList;
import java.util.List;

// Entrada para crear una orden / preferencia de pago: productos del
// carrito más los ids de cliente y restaurante.
public class DTOOrden {
    private Integer idCliente;
    private Integer idRestaurante;
    private DTDireccion direccionEntrega;
    private List<DTOProductoOrden> productos = new ArrayList<>();

    public DTOOrden() {
    }

    public DTOOrden(Integer idCliente, Integer idRestaurante, DTDireccion direccionEntrega,
                    List<DTOProductoOrden> productos) {
        this.idCliente = idCliente;
        this.idRestaurante = idRestaurante;
        this.direccionEntrega = direccionEntrega;
        if (productos != null) {
            this.productos = productos;
        }
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(Integer idRestaurante) {
        this.idRestaurante = idRestaurante;
    }

    public DTDireccion getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(DTDireccion direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public List<DTOProductoOrden> getProductos() {
        return productos;
    }

    public void setProductos(List<DTOProductoOrden> productos) {
        this.productos = productos;
    }
}