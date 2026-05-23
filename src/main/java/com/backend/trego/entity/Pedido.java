package com.backend.trego.entity;

import com.backend.trego.entity.DTOs.DTDireccion;
import com.backend.trego.entity.Enums.EnumEstadoPedido;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPedido;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaExpiracion;
    private float total;

    @Enumerated(EnumType.STRING)
    private EnumEstadoPedido estado;

    @Embedded
    private DTDireccion direccionEntrega;

    private LocalDateTime horarioEntrega;

    private String razonCancelacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comentario_id")
    private Comentario comentario;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reclamo_id")
    private Reclamo reclamo;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pago_id")
    private Pago pago;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoPedido> productos = new ArrayList<>();

    protected Pedido() {
    }

    public Pedido(float total, EnumEstadoPedido estado, DTDireccion direccionEntrega, LocalDateTime horarioEntrega) {
        this.total = total;
        this.estado = estado;
        this.direccionEntrega = direccionEntrega;
        this.horarioEntrega = horarioEntrega;
        inicializarVigencia();
    }

    public void inicializarVigencia() {
        LocalDateTime ahora = LocalDateTime.now();
        this.fechaCreacion = ahora;
        this.fechaExpiracion = ahora.plusHours(24);
    }

    public void addProductoPedido(ProductoPedido pp) {
        pp.setPedido(this);
        this.productos.add(pp);
    }

    public void removeProductoPedido(ProductoPedido pp) {
        pp.setPedido(null);
        this.productos.remove(pp);
    }

    public int getIdPedido() {
        return idPedido;
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public Comentario getComentario() {
        return comentario;
    }

    public void setComentario(Comentario comentario) {
        this.comentario = comentario;
    }

    public Reclamo getReclamo() {
        return reclamo;
    }

    public void setReclamo(Reclamo reclamo) {
        this.reclamo = reclamo;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public List<ProductoPedido> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoPedido> productos) {
        this.productos = productos;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public EnumEstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EnumEstadoPedido estado) {
        this.estado = estado;
    }

    public DTDireccion getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(DTDireccion direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public LocalDateTime getHorarioEntrega() {
        return horarioEntrega;
    }

    public void setHorarioEntrega(LocalDateTime horarioEntrega) {
        this.horarioEntrega = horarioEntrega;
    }

    public String getRazonCancelacion() {
        return razonCancelacion;
    }

    public void setRazonCancelacion(String razonCancelacion) {
        this.razonCancelacion = razonCancelacion;
    }

    public float calcularTotal() {
        return (float) productos.stream()
                .mapToDouble(pp -> pp.getProducto().getPrecio() * pp.getCantidad())
                .sum();
    }
}