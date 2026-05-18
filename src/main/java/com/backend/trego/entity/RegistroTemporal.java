package com.backend.trego.entity;

import java.time.LocalDateTime;

public class RegistroTemporal {
    private String email;
    private String password;
    private String codigoVerificacion;
    private LocalDateTime fechaExpiracion;

    public RegistroTemporal(String email, String password, String codigoVerificacion) {
        this.email = email;
        this.password = password;
        this.codigoVerificacion = codigoVerificacion;
        this.fechaExpiracion = LocalDateTime.now().plusMinutes(10); // Código válido por 10 minutos
    }

    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(this.fechaExpiracion);
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }
}