package com.backend.trego.entity;

import java.util.ArrayList;
import java.util.List;

import com.backend.trego.entity.DTOs.DTOCarrito;
import com.backend.trego.entity.DTOs.DTOProducto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Carrito de compras de un cliente. Contiene un conjunto de líneas
 * (LineaCarrito) cada una con un producto y su cantidad. Todos los productos
 * de un carrito deben pertenecer al mismo restaurante.
 */
@Entity
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCarrito;

    /**
     * UID de Firebase del cliente propietario del carrito.
     */
    @Column(name = "uid_cliente", nullable = false)
    private String uidCliente;

    /**
     * Restaurante al que pertenecen los productos del carrito.
     */
    @Column(name = "id_restaurante")
    private Integer idRestaurante;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaCarrito> lineas = new ArrayList<>();

    private Double total = 0.0;

    protected Carrito() {
    }

    public Carrito(String uidCliente, Integer idRestaurante) {
        this.uidCliente = uidCliente;
        this.idRestaurante = idRestaurante;
        this.lineas = new ArrayList<>();
        this.total = 0.0;
    }

    public Integer getIdCarrito() {
        return idCarrito;
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

    public List<LineaCarrito> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaCarrito> lineas) {
        this.lineas = lineas;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    /**
     * Agrega una línea al carrito asegurando la relación bidireccional.
     */
    public void addLinea(LineaCarrito linea) {
        linea.setCarrito(this);
        this.lineas.add(linea);
        recalcularTotal();
    }

    /**
     * Quita la línea indicada del carrito.
     */
    public boolean removeLinea(LineaCarrito linea) {
        boolean removed = this.lineas.remove(linea);
        if (removed) {
            linea.setCarrito(null);
            recalcularTotal();
        }
        return removed;
    }

    /**
     * Vacía todas las líneas del carrito y deja el total en 0.
     */
    public void vaciar() {
        this.lineas.clear();
        this.total = 0.0;
    }

    /**
     * Recalcula y actualiza el total sumando los subtotales de todas las líneas.
     */
    public double recalcularTotal() {
        this.total = this.lineas.stream()
                .mapToDouble(LineaCarrito::getSubtotal)
                .sum();
        return this.total;
    }

    public DTOCarrito toDTO() {
        DTOCarrito dto = new DTOCarrito();
        dto.setIdCarrito(this.idCarrito);
        dto.setUidCliente(this.uidCliente);
        dto.setIdRestaurante(this.idRestaurante);

        List<DTOProducto> productosDTO = new ArrayList<>();
        for (LineaCarrito linea : this.lineas) {
            productosDTO.add(linea.toDTO());
        }
        dto.setProductos(productosDTO);
        dto.setTotal(this.total);
        return dto;
    }
}
