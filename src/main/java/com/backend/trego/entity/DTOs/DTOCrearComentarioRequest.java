package com.backend.trego.entity.DTOs;

// Request para que un cliente comente y califique un restaurante.
public class DTOCrearComentarioRequest {

    private Integer idRestaurante;
    private Integer calificacion;
    private String texto;

    protected DTOCrearComentarioRequest() {
    }

    public DTOCrearComentarioRequest(Integer idRestaurante, Integer calificacion, String texto) {
        this.idRestaurante = idRestaurante;
        this.calificacion = calificacion;
        this.texto = texto;
    }

    public Integer getIdRestaurante() {
        return idRestaurante;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public String getTexto() {
        return texto;
    }
}
