package com.backend.trego.entity.DTOs;

// Respuesta del login: token JWT y datos básicos del usuario.
public class LoginResponseDTO {

    private String jwtToken;
    private String rol;
    private String nombre;
    private String email;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String jwtToken, String rol, String nombre, String email) {
        this.jwtToken = jwtToken;
        this.rol = rol;
        this.nombre = nombre;
        this.email = email;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public String getRol() {
        return rol;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }
}