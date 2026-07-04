package com.backend.trego.entity;

import java.util.ArrayList;
import java.util.List;

import com.backend.trego.entity.DTOs.DTOCarrito;
import com.backend.trego.entity.DTOs.DTOProductoPedido;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

// Carrito de un cliente. Tiene varias líneas (LineaCarrito), y todos los
// productos deben ser del mismo restaurante.
@Entity
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCarrito;

    @Column(name = "uid_cliente", nullable = false)
    private String uidCliente;

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

    public void addLinea(LineaCarrito linea) {
        linea.setCarrito(this);
        this.lineas.add(linea);
        recalcularTotal();
    }

    public boolean removeLinea(LineaCarrito linea) {
        boolean removed = this.lineas.remove(linea);
        if (removed) {
            linea.setCarrito(null);
            recalcularTotal();
        }
        return removed;
    }

    public void vaciar() {
        this.lineas.clear();
        this.total = 0.0;
        this.idRestaurante = null;
    }

    public double recalcularTotal() {
        this.total = this.lineas.stream()
                .mapToDouble(linea -> linea.getSubtotal())
                .sum();
        return this.total;
    }

    public DTOCarrito toDTO() {
        List<DTOProductoPedido> productosDTO = new ArrayList<>();
        for (LineaCarrito linea : this.lineas) {
            productosDTO.add(linea.toDTO());
        }
        return new DTOCarrito(this.idCarrito, this.uidCliente, this.idRestaurante, productosDTO, this.total);
    }
}
