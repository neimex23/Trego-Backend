package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.EnumRoles;

public class UsuarioDTO {

    private Integer idUsuario;
    private String email;      // Cambiado a String (los emails llevan letras y @)
    private String nombre;
    private String fotoPerfil; // En API REST y Firebase se maneja como String (la URL de la imagen)
    private EnumRoles rol;     // Usamos tu Enum nativo EnumRoles
    private String firebaseUid; // Lo necesitamos para mapear el UID de Firebase al registrar
    private String telefono;    // Lo necesitamos para el flujo de SMS

    // Constructor vacío (Obligatorio para que Spring/Jackson deserialice el JSON)
    public UsuarioDTO() {
    }

    // Constructor completo
    public UsuarioDTO(Integer idUsuario, String email, String nombre, String fotoPerfil, EnumRoles rol, String firebaseUid, String telefono) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.nombre = nombre;
        this.fotoPerfil = fotoPerfil;
        this.rol = rol;
        this.firebaseUid = firebaseUid;
        this.telefono = telefono;
    }

    // Getters y Setters
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