package com.backend.trego.entity.DTOs;

/**
 * DTO de respuesta para un inicio de sesión exitoso.
 * Contiene el token JWT y datos básicos del usuario autenticado.
 */
public class DTOLoginResponse {

    private String jwtToken;
    private String rol;
    private String nombre;
    private String email;

    public DTOLoginResponse() {
    }

    public DTOLoginResponse(String jwtToken, String rol, String nombre, String email) {
        this.jwtToken = jwtToken;
        this.rol = rol;
        this.nombre = nombre;
        this.email = email;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
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
}
