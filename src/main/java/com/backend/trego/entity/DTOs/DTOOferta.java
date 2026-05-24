package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Oferta;

import java.time.LocalDateTime;

/** DTO de representación de una oferta. Solo lectura: no se usa para crear/editar ofertas. */
public class DTOOferta {

    private Integer idOferta;
    private String descripcion;
    private float descuento; // Porcentaje, ej. 20.0 para 20% off
    private String urlImagen;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    public DTOOferta() {
    }

    public DTOOferta(Integer idOferta, String descripcion, float descuento, String urlImagen,
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        this.idOferta = idOferta;
        this.descripcion = descripcion;
        this.descuento = descuento;
        this.urlImagen = urlImagen;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public static DTOOferta desde(Oferta oferta) {
        if (oferta == null) {
            return null;
        }
        return new DTOOferta(
                oferta.getIdOferta(),
                oferta.getDescripcion(),
                oferta.getDescuento(),
                oferta.getUrlImagen(),
                oferta.getFechaInicio(),
                oferta.getFechaFin());
    }

    public Integer getIdOferta() {
        return idOferta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public float getDescuento() {
        return descuento;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }
}
