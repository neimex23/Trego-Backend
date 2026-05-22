package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.EnumEstadoPedido;

import java.util.Date;
import java.util.List;

// Pedido realizado por un cliente.
public class DTOPedido {

    private Integer idPedido;
    private Integer idCliente;
    private Integer idRestaurante;
    private List<DTOProducto> productos;
    private DTDireccion direccionEntrega;
    private Double total;

    private EnumEstadoPedido estado;
    private Date fechaCreacion;
    private Date horaEntregaEstimada;
    private Integer tiempoPreparacion;

    public DTOPedido() {
    }

    // Constructor mínimo usado al generar la preferencia de pago (id + total).
    public DTOPedido(Integer idPedido, Double total) {
        this.idPedido = idPedido;
        this.total = total;
    }

    // Constructor usado al generar la preferencia de pago con las líneas del pedido.
    public DTOPedido(Integer idPedido, Double total, List<DTOProducto> productos) {
        this.idPedido = idPedido;
        this.total = total;
        this.productos = productos;
    }

    public DTOPedido(Integer idPedido, Integer idCliente, Integer idRestaurante, List<DTOProducto> productos,
            DTDireccion direccionEntrega, Double total, EnumEstadoPedido estado, Date fechaCreacion,
            Date horaEntregaEstimada, Integer tiempoPreparacion) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.idRestaurante = idRestaurante;
        this.productos = productos;
        this.direccionEntrega = direccionEntrega;
        this.total = total;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.horaEntregaEstimada = horaEntregaEstimada;
        this.tiempoPreparacion = tiempoPreparacion;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public Integer getIdRestaurante() {
        return idRestaurante;
    }

    public List<DTOProducto> getProductos() {
        return productos;
    }

    public DTDireccion getDireccionEntrega() {
        return direccionEntrega;
    }

    public Double getTotal() {
        return total;
    }

    public EnumEstadoPedido getEstado() {
        return estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public Date getHoraEntregaEstimada() {
        return horaEntregaEstimada;
    }

    public Integer getTiempoPreparacion() {
        return tiempoPreparacion;
    }

}
