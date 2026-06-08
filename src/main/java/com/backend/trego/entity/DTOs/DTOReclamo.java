package com.backend.trego.entity.DTOs;

import java.time.LocalDateTime;

import com.backend.trego.entity.Enums.EnumEstadoReclamo;

// DTO de lectura para un reclamo: se usa en listados y respuesta tras resolución.
public class DTOReclamo {

    private Integer idReclamo;
    private Integer idPedido;
    private String nombreUsuario;
    private String emailUsuario;
    private String motivoReclamo;
    private EnumEstadoReclamo estado;
    private LocalDateTime fechaReclamo;
    private String motivoRechazo;
    private Float totalPedido;

    protected DTOReclamo() {
    }

    public DTOReclamo(Integer idReclamo, Integer idPedido, String nombreUsuario, String emailUsuario,
            String motivoReclamo, EnumEstadoReclamo estado, LocalDateTime fechaReclamo,
            String motivoRechazo, Float totalPedido) {
        this.idReclamo = idReclamo;
        this.idPedido = idPedido;
        this.nombreUsuario = nombreUsuario;
        this.emailUsuario = emailUsuario;
        this.motivoReclamo = motivoReclamo;
        this.estado = estado;
        this.fechaReclamo = fechaReclamo;
        this.motivoRechazo = motivoRechazo;
        this.totalPedido = totalPedido;
    }

    public Integer getIdReclamo() {
        return idReclamo;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public String getMotivoReclamo() {
        return motivoReclamo;
    }

    public EnumEstadoReclamo getEstado() {
        return estado;
    }

    public LocalDateTime getFechaReclamo() {
        return fechaReclamo;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public Float getTotalPedido() {
        return totalPedido;
    }
}
