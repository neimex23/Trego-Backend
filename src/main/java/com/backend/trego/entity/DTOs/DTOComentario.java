package com.backend.trego.entity.DTOs;

public class DTOComentario {
    private String idComentario;
    private String texto;
    private Integer idRestaurante;
    private Integer calificacion;
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
    public DTOComentario(String idComentario, String texto, Integer idRestaurante, Integer calificacion) {
        this.idComentario = idComentario;
        this.texto = texto;
        this.idRestaurante = idRestaurante;
        this.calificacion = calificacion;
    }

    
}
