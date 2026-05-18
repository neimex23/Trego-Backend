package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.EnumCategoriaRestaurante;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO genérico para representar un Restaurante.
 */
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

    public DTORestaurante() {
    }

    public Integer getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(Integer idRestaurante) {
        this.idRestaurante = idRestaurante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public DTODireccion getDireccion() {
        return direccion;
    }

    public void setDireccion(DTODireccion direccion) {
        this.direccion = direccion;
    }

    public EnumCategoriaRestaurante getCategoria() {
        return categoria;
    }

    public void setCategoria(EnumCategoriaRestaurante categoria) {
        this.categoria = categoria;
    }

    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }

    public Boolean getAbierto() {
        return abierto;
    }

    public void setAbierto(Boolean abierto) {
        this.abierto = abierto;
    }

    public Date getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(Date horaApertura) {
        this.horaApertura = horaApertura;
    }

    public Date getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(Date horaCierre) {
        this.horaCierre = horaCierre;
    }

    public List<DTOProducto> getProductos() {
        return productos;
    }

    public void setProductos(List<DTOProducto> productos) {
        this.productos = productos;
    }
}
