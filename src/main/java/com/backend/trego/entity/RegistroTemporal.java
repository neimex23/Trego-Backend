package com.backend.trego.entity;

import java.time.LocalDateTime;

public class RegistroTemporal {
    private String email;
    private String password;
    private String codigoVerificacion;
    private LocalDateTime fechaExpiracionCodigo;
    private LocalDateTime fechaExpiracionCacheReenvio;

    public RegistroTemporal(String email, String password, String codigoVerificacion) {
        this.email = email;
        this.password = password;
        this.codigoVerificacion = codigoVerificacion;
        this.fechaExpiracionCodigo = LocalDateTime.now().plusMinutes(10); // Código válido por 10 minutos
        this.fechaExpiracionCacheReenvio = LocalDateTime.now().plusHours(12); // Cache de reenvío por 12 horas
    }

    public boolean estaExpiradoCodigo() {
        return LocalDateTime.now().isAfter(this.fechaExpiracionCodigo);
    }

    public boolean estaExpiradoCacheReenvio() {
        return LocalDateTime.now().isAfter(this.fechaExpiracionCacheReenvio);
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

    public LocalDateTime getFechaExpiracionCodigo() {
        return fechaExpiracionCodigo;
    }

    public LocalDateTime getFechaExpiracionCacheReenvio() {
        return fechaExpiracionCacheReenvio;
    }

    public void setFechaExpiracionCodigo(LocalDateTime fechaExpiracionCodigo) {
        this.fechaExpiracionCodigo = fechaExpiracionCodigo;
    }

    public void setFechaExpiracionCacheReenvio(LocalDateTime fechaExpiracionCacheReenvio) {
        this.fechaExpiracionCacheReenvio = fechaExpiracionCacheReenvio;
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