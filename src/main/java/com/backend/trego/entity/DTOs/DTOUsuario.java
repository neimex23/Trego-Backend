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

    public DTOUsuario(Integer idUsuario, String uid, String nombre, String email, String password, String urlImagen,
            String telefono, EnumRoles rol) {
        this.idUsuario = idUsuario;
        this.uid = uid;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.urlImagen = urlImagen;
        this.telefono = telefono;
        this.rol = rol;
    }

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

    public String getUid() {
        return uid;
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

    public String getUrlImagen() {
        return urlImagen;
    }

    public String getTelefono() {
        return telefono;
    }
    public EnumRoles getRol() {
        return rol;
    }
}
