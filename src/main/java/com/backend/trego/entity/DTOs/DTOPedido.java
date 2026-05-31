package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.ProductoPedido;
import com.backend.trego.entity.Enums.EnumEstadoPedido;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DTOPedido {

    private Integer idPedido;
    private Integer idCliente;
    private String nombreCliente;
    private Integer idRestaurante;
    private List<DTOProductoPedido> productos;
    private DTODireccion direccionEntrega;
    private Double total;

    private EnumEstadoPedido estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaExpiracion;
    private LocalDateTime horaEntregaEstimada;
    private Integer tiempoPreparacion;

    public DTOPedido() {
    }

    public DTOPedido(Integer idPedido, Double total, List<DTOProductoPedido> productos) {
        this.idPedido = idPedido;
        this.total = total;
        this.productos = productos;
    }

    public DTOPedido(Integer idPedido, Integer idCliente, String nombreCliente, Integer idRestaurante, List<DTOProductoPedido> productos,
            DTODireccion direccionEntrega, Double total, EnumEstadoPedido estado, LocalDateTime fechaCreacion,
            LocalDateTime fechaExpiracion, LocalDateTime horaEntregaEstimada, Integer tiempoPreparacion) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.idRestaurante = idRestaurante;
        this.productos = productos;
        this.direccionEntrega = direccionEntrega;
        this.total = total;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaExpiracion = fechaExpiracion;
        this.horaEntregaEstimada = horaEntregaEstimada;
        this.tiempoPreparacion = tiempoPreparacion;
    }

    public static DTOPedido desde(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        List<DTOProductoPedido> lineas = pedido.getProductos().stream()
                .map(DTOPedido::mapearLinea)
                .collect(Collectors.toList());
        Integer idCliente = pedido.getCliente() != null ? pedido.getCliente().getIdUsuario() : null;
        Integer idRestaurante = pedido.getRestaurante() != null ? pedido.getRestaurante().getIdUsuario() : null;
        String nombreCliente = pedido.getCliente() != null ? pedido.getCliente().getNombre() : null;
        return new DTOPedido(
                pedido.getIdPedido(),
                idCliente,
                nombreCliente,
                idRestaurante,
                lineas,
                pedido.getDireccionEntrega(),
                (double) pedido.getTotal(),
                pedido.getEstado(),
                pedido.getFechaCreacion(),
                pedido.getFechaExpiracion(),
                pedido.getHorarioEntrega(),
                pedido.getTiempoPreparacion());
    }

    private static DTOProductoPedido mapearLinea(ProductoPedido pp) {
        var producto = pp.getProducto();
        DTOProductoSimplificado simplificado = producto != null
                ? DTOProductoSimplificado.desde(producto)
                : null;
        return new DTOProductoPedido(
                null,
                null,
                pp.getComentarioCliente(),
                pp.getCantidad(),
                (double) pp.getPrecioSuma(),
                simplificado);
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

    public List<DTOProductoPedido> getProductos() {
        return productos;
    }

    public DTODireccion getDireccionEntrega() {
        return direccionEntrega;
    }

    public Double getTotal() {
        return total;
    }

    public EnumEstadoPedido getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public LocalDateTime getHoraEntregaEstimada() {
        return horaEntregaEstimada;
    }

    public Integer getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }
}
