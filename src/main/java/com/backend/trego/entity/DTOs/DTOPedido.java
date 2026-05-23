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
    private Integer idRestaurante;
    private List<DTOProducto> productos;
    private DTDireccion direccionEntrega;
    private Double total;

    private EnumEstadoPedido estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaExpiracion;
    private LocalDateTime horaEntregaEstimada;
    private Integer tiempoPreparacion;

    public DTOPedido() {
    }

    public DTOPedido(Integer idPedido, Double total) {
        this.idPedido = idPedido;
        this.total = total;
    }

    public DTOPedido(Integer idPedido, Double total, List<DTOProducto> productos) {
        this.idPedido = idPedido;
        this.total = total;
        this.productos = productos;
    }

    public DTOPedido(Integer idPedido, Integer idCliente, Integer idRestaurante, List<DTOProducto> productos,
            DTDireccion direccionEntrega, Double total, EnumEstadoPedido estado, LocalDateTime fechaCreacion,
            LocalDateTime fechaExpiracion, LocalDateTime horaEntregaEstimada, Integer tiempoPreparacion) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
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
        List<DTOProducto> lineas = pedido.getProductos().stream()
                .map(DTOPedido::mapearLinea)
                .collect(Collectors.toList());
        Integer idCliente = pedido.getCliente() != null ? pedido.getCliente().getIdUsuario() : null;
        Integer idRestaurante = pedido.getRestaurante() != null ? pedido.getRestaurante().getIdUsuario() : null;
        return new DTOPedido(
                pedido.getIdPedido(),
                idCliente,
                idRestaurante,
                lineas,
                pedido.getDireccionEntrega(),
                (double) pedido.getTotal(),
                pedido.getEstado(),
                pedido.getFechaCreacion(),
                pedido.getFechaExpiracion(),
                pedido.getHorarioEntrega(),
                null);
    }

    private static DTOProducto mapearLinea(ProductoPedido pp) {
        var producto = pp.getProducto();
        if (producto == null) {
            return new DTOProducto();
        }
        return new DTOProducto(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getUrlImagen(),
                pp.getCantidad(),
                pp.getComentarioCliente(),
                (double) pp.getPrecioSuma());
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

    public List<DTOProducto> getProductos() {
        return productos;
    }

    public void setProductos(List<DTOProducto> productos) {
        this.productos = productos;
    }

    public DTDireccion getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(DTDireccion direccionEntrega) {
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public LocalDateTime getHoraEntregaEstimada() {
        return horaEntregaEstimada;
    }

    public void setHoraEntregaEstimada(LocalDateTime horaEntregaEstimada) {
        this.horaEntregaEstimada = horaEntregaEstimada;
    }

    public Integer getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public void setTiempoPreparacion(Integer tiempoPreparacion) {
        this.tiempoPreparacion = tiempoPreparacion;
    }
}
