package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.EnumCategoriaRestaurante;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Representa un restaurante.
public class DTORestaurante {

    private Integer idRestaurante;
    private String nombre;
    private String email;
    private String password;
    private String telefono;
    private String urlImagen;
    private DTODireccion direccion;
    private EnumCategoriaRestaurante categoria;
    private Boolean habilitado;
    private Boolean abierto;
    private Date horaApertura;
    private Date horaCierre;
    private List<DTOProducto> productos = new ArrayList<>();

    public DTORestaurante(Integer idRestaurante, String nombre, String email, String password, String telefono,
            String urlImagen, DTODireccion direccion, EnumCategoriaRestaurante categoria, Boolean habilitado,
            Boolean abierto, Date horaApertura, Date horaCierre, List<DTOProducto> productos) {
        this.idRestaurante = idRestaurante;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.urlImagen = urlImagen;
        this.direccion = direccion;
        this.categoria = categoria;
        this.habilitado = habilitado;
        this.abierto = abierto;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.productos = productos;
    }

    public Integer getIdRestaurante() {
        return idRestaurante;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public DTODireccion getDireccion() {
        return direccion;
    }

    public EnumCategoriaRestaurante getCategoria() {
        return categoria;
    }

    public Boolean getHabilitado() {
        return habilitado;
    }

    public Boolean getAbierto() {
        return abierto;
    }

    public Date getHoraApertura() {
        return horaApertura;
    }

    public Date getHoraCierre() {
        return horaCierre;
    }

    public List<DTOProducto> getProductos() {
        return productos;
    }

    public DTORestaurante() {
    }

    // Constructor con los datos públicos del restaurante (sin password ni dirección)
    public DTORestaurante(Integer idRestaurante, String nombre, String email, String telefono, String urlImagen,
            EnumCategoriaRestaurante categoria, Boolean habilitado, Boolean abierto) {
        this.idRestaurante = idRestaurante;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.urlImagen = urlImagen;
        this.categoria = categoria;
        this.habilitado = habilitado;
        this.abierto = abierto;
    }

}
