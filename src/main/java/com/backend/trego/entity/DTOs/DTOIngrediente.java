package com.backend.trego.entity.DTOs;

// Ingrediente asociado a un producto del menú.
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

    public String getNombre() {
        return nombre;
    }

    public Integer getIdRestaurante() {
        return idRestaurante;
    }
}
