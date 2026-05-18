package com.backend.trego.entity;

import com.backend.trego.entity.Enums.EnumRoles;

import jakarta.persistence.Entity;

@Entity
public class Administrador extends Usuario {
    private String password;

    protected Administrador() {
    }

    public Administrador(String nombre, String email, String urlImagen, EnumRoles rol, String password) {
        super(nombre, email, urlImagen, rol, null);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
