package com.backend.trego.entity.DTOs;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO de entrada para crear o actualizar un Cliente.
 * No expone campos internos como rol o habilitado.
 */
public class DTOCliente {
    private String nombre;
    private String email;
    private String urlImagen;
    private String telefono;
    private String uidCliente;
    private List<DTDireccion> direcciones = new ArrayList<>();

    public DTOCliente() {
    }

    public DTOCliente(String nombre, String email, String urlImagen, String telefono,
                      String uidCliente, List<DTDireccion> direcciones) {
        this.nombre = nombre;
        this.email = email;
        this.urlImagen = urlImagen;
        this.telefono = telefono;
        this.uidCliente = uidCliente;
        if (direcciones != null) {
            this.direcciones = direcciones;
        }
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

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUidCliente() {
        return uidCliente;
    }

    public void setUidCliente(String uidCliente) {
        this.uidCliente = uidCliente;
    }

    public List<DTDireccion> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(List<DTDireccion> direcciones) {
        this.direcciones = direcciones;
    }
}
