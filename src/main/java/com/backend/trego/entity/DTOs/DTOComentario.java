package com.backend.trego.entity.DTOs;

public class DTOComentario {
    private Integer idComentario;
    private String texto;
    private Integer idRestaurante;
    private Integer calificacion;
    private String fechaCreacion;
    private String nombreCliente;

    protected DTOComentario() {
    }

    public DTOComentario(Integer idComentario, String texto, Integer idRestaurante, Integer calificacion,
            String fechaCreacion, String nombreCliente) {
        this.idComentario = idComentario;
        this.texto = texto;
        this.idRestaurante = idRestaurante;
        this.calificacion = calificacion;
        this.fechaCreacion = fechaCreacion;
        this.nombreCliente = nombreCliente;
    }

    public Integer getIdComentario() {
        return idComentario;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
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

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }
}
