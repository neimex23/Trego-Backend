package com.backend.trego.entity.DTOs;

/**
 * DTO para Ingredientes asociados a productos del menú.
 */
public class DTOIngrediente {

    private Integer idIngrediente;
    private String nombre;
    private Integer idRestaurante;

    public DTOIngrediente() {
    }

    public DTOIngrediente(Integer idIngrediente, String nombre, Integer idRestaurante) {
        this.idIngrediente = idIngrediente;
        this.nombre = nombre;
        this.idRestaurante = idRestaurante;
    }

    public Integer getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(Integer idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(Integer idRestaurante) {
        this.idRestaurante = idRestaurante;
    }
}
