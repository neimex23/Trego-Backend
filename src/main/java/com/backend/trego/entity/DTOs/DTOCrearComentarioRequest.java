package com.backend.trego.entity.DTOs;

// Request para que un cliente comente y califique un restaurante.
public class DTOCrearComentarioRequest {

    private Integer idRestaurante;
    private Integer calificacion;
    private String texto;

    protected DTOCrearComentarioRequest() {
    }

    public Integer getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(Integer idRestaurante) {
        this.idRestaurante = idRestaurante;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
