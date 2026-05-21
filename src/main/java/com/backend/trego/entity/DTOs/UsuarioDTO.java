package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.EnumRoles;

public class UsuarioDTO {

    private Integer idUsuario;
    private String email;
    private String nombre;
    private String fotoPerfil;   // URL de la imagen
    private EnumRoles rol;
    private String firebaseUid;  // para el registro vía Firebase
    private String telefono;     // para el flujo de SMS

    public UsuarioDTO() {
    }

    public UsuarioDTO(Integer idUsuario, String email, String nombre, String fotoPerfil, EnumRoles rol, String firebaseUid, String telefono) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.nombre = nombre;
        this.fotoPerfil = fotoPerfil;
        this.rol = rol;
        this.firebaseUid = firebaseUid;
        this.telefono = telefono;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public EnumRoles getRol() {
        return rol;
    }

    public void setRol(EnumRoles rol) {
        this.rol = rol;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}