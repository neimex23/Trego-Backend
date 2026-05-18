package com.backend.trego.entity;

import com.backend.trego.entity.Enums.EnumRoles;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsuario;

    private String nombre;
    private String email;
    private String urlImagen;
    private String firebaseUid;

    @Enumerated(EnumType.STRING)
    private EnumRoles rol = EnumRoles.Cliente;

    protected Usuario() {
    }

    public Usuario(String nombre, String email, String urlImagen, EnumRoles rol, String firebaseUid) {
        this.nombre = nombre;
        this.email = email;
        this.urlImagen = urlImagen;
        this.rol = rol;
        this.firebaseUid = firebaseUid;
    }

    public int getIdUsuario() {
        return idUsuario;
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
        throw new UnsupportedOperationException("Unimplemented method 'getTelefono'");
    }
}