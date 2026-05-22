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

    public String getEmail() {
        return email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public EnumRoles getRol() {
        return rol;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public String getTelefono() {
        return telefono;
    }
}