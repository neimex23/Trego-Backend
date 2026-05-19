package com.backend.trego.entity;

import java.util.ArrayList;
import java.util.List;

import com.backend.trego.entity.DTOs.DTOProducto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;


@Entity
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCarrito;

    public Integer getIdCarrito() {
        return idCarrito;
    }

    @ManyToOne
    @JoinColumn(name = "uidCliente")
    private String uidCliente;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private String idRestaurante;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos = new ArrayList<>();

    private Double total;

    public String getUidCliente() {
        return uidCliente;
    }

    public void setUidCliente(String uidCliente) {
        this.uidCliente = uidCliente;
    }

    public String getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(String idRestaurante) {
        this.idRestaurante = idRestaurante;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void addProducto(Producto producto) {
        this.productos.add(producto);
        total += producto.getPrecio();
    }

    public void removeProducto(Producto producto) {
        if (this.productos.remove(producto)) {
            total -= producto.getPrecio();
        }
    }

    public Carrito(String uidCliente, String idRestaurante, List<Producto> productos, Double total) {
        this.uidCliente = uidCliente;
        this.idRestaurante = idRestaurante;
        this.productos = productos;
        this.total = total;
    }
}


