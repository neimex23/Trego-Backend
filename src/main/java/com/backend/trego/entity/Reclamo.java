package com.backend.trego.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.backend.trego.entity.Enums.EnumEstadoReclamo;

@Entity
public class Reclamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idReclamo;

    private String texto;

    @Enumerated(EnumType.STRING)
    private EnumEstadoReclamo estado;

    private LocalDateTime fechaReclamo;
    private String motivoRechazo;

    protected Reclamo() {
    }

    public Reclamo(String texto, EnumEstadoReclamo estado) {
        this.texto = texto;
        this.estado = estado;
        this.fechaReclamo = LocalDateTime.now();
    }

    public int getIdReclamo() {
        return idReclamo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public EnumEstadoReclamo getEstado() {
        return estado;
    }

    public void setEstado(EnumEstadoReclamo estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaReclamo() {
        return fechaReclamo;
    }

    public void setFechaReclamo(LocalDateTime fechaReclamo) {
        this.fechaReclamo = fechaReclamo;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }
}
