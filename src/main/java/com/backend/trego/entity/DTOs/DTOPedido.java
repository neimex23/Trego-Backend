package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.EnumEstadoPedido;

import java.util.Date;
import java.util.List;

// Pedido realizado por un cliente.
public class DTOPedido {

    private Integer idPedido;
    private Integer idCliente;
    private Integer idRestaurante;
    private List<DTOProductoOrden> productos;
    private DTODireccion direccionEntrega;
    private Double total;
    private EnumEstadoPedido estado;
    private Date fechaCreacion;
    private Date horaEntregaEstimada;
    private Integer tiempoPreparacion;

    public DTOPedido() {
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
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

    public List<DTOProductoOrden> getProductos() {
        return productos;
    }

    public void setProductos(List<DTOProductoOrden> productos) {
        this.productos = productos;
    }

    public DTODireccion getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(DTODireccion direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public EnumEstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EnumEstadoPedido estado) {
        this.estado = estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getHoraEntregaEstimada() {
        return horaEntregaEstimada;
    }

    public void setHoraEntregaEstimada(Date horaEntregaEstimada) {
        this.horaEntregaEstimada = horaEntregaEstimada;
    }

    public Integer getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public void setTiempoPreparacion(Integer tiempoPreparacion) {
        this.tiempoPreparacion = tiempoPreparacion;
    }
}
