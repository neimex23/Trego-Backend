package com.backend.trego.entity.DTOs;

import java.time.LocalDateTime;

public class DTOModificarOfertaRequest {
    private Integer idProducto;
    private Boolean habilitar;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    public Integer getIdProducto() {
        return idProducto;
    }
    public Boolean getHabilitar() {
        return habilitar;
    }
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }
    public LocalDateTime getFechaFin() {
        return fechaFin;
    }
    public DTOModificarOfertaRequest(Integer idProducto, Boolean habilitar, LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {
        this.idProducto = idProducto;
        this.habilitar = habilitar;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }




}
