package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.EnumRoles;

// Usuario del sistema: cliente, restaurante o administrador.
public class DTOUsuario {

    private Integer idUsuario;
    private String uid;
    private String nombre;
    private String email;
    private String password;
    private String urlImagen;
    private String telefono;
    private EnumRoles rol;

    public DTOUsuario() {
    }

    public DTOUsuario(String email, String password, EnumRoles rol) {
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public EnumRoles getRol() {
        return rol;
    }

    public void setRol(EnumRoles rol) {
        this.rol = rol;
    }
}
