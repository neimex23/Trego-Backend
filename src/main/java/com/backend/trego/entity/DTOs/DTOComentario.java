package com.backend.trego.entity.DTOs;

public class DTOComentario {
    private String idComentario;
    private String texto;
    private Integer idRestaurante;
    private Integer calificacion;
    private String fechaCreacion;
    private String nombreCliente;

    public String getIdComentario() {
        return idComentario;
    }
    public String getTexto() {
        return texto;
    }
    public Integer getIdRestaurante() {
        return idRestaurante;
    }
    public Integer getCalificacion() {
        return calificacion;
    }
    public String getFechaCreacion() {
        return fechaCreacion;
    }
    public String getNombreCliente() {
        return nombreCliente;
    }
    public DTOComentario(String idComentario, String texto, Integer idRestaurante, Integer calificacion, String fechaCreacion, String nombreCliente) {
        this.idComentario = idComentario;
        this.texto = texto;
        this.idRestaurante = idRestaurante;
        this.calificacion = calificacion;
        this.fechaCreacion = fechaCreacion;
        this.nombreCliente = nombreCliente;
    }

    
}
