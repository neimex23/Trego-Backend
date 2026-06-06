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
    private Integer idUsuario;

    private String nombre;
    private String email;
    private String fotoPerfil;

    @Enumerated(EnumType.STRING)
    private EnumRoles rol = EnumRoles.Cliente;

    protected Usuario() {
    }

    public Usuario(String email, EnumRoles rol) {
        this.email = email;
        this.rol = rol;
    }

    public Usuario(String nombre, String email, String fotoPerfil, EnumRoles rol) {
        this.nombre = nombre;
        this.email = email;
        this.fotoPerfil = fotoPerfil;
        this.rol = rol;
    }

    public Integer getIdUsuario() {
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
}