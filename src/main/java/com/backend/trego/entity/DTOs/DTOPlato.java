package com.backend.trego.entity.DTOs;

import java.util.ArrayList;
import java.util.List;

import com.backend.trego.entity.Ingrediente;
import com.backend.trego.entity.Plato;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.Enums.EnumCategoriaProducto;
import com.backend.trego.entity.Enums.EnumTipoProducto;

public class DTOPlato extends DTOProducto {

    private Integer tiempoPreparacionMinutos;

    public DTOPlato() {
        super();
        setTipo(EnumTipoProducto.Plato);
    }

    public DTOPlato(Integer idProducto, String nombre, String descripcion, Double precio, String urlImagen,
            EnumCategoriaProducto categoria, Integer idRestaurante, List<DTOIngrediente> ingredientes,
            Boolean disponible, Integer tiempoPreparacionMinutos) {
        super(idProducto, nombre, descripcion, precio, urlImagen, categoria, idRestaurante, ingredientes, disponible);
        this.tiempoPreparacionMinutos = tiempoPreparacionMinutos;
        setTipo(EnumTipoProducto.Plato);
    }

    public Integer getTiempoPreparacionMinutos() {
        return tiempoPreparacionMinutos;
    }

    public void setTiempoPreparacionMinutos(Integer tiempoPreparacionMinutos) {
        this.tiempoPreparacionMinutos = tiempoPreparacionMinutos;
    }

    @Override
    public Producto toEntity() {
        float precio = getPrecio() != null ? getPrecio().floatValue() : 0f;
        int tiempo = tiempoPreparacionMinutos != null ? tiempoPreparacionMinutos : 0;

        Plato plato = new Plato(
                getNombre(),
                precio,
                getDescripcion(),
                getUrlImagen(),
                tiempo);

        List<DTOIngrediente> dtoIngredientes = getIngredientes();
        if (dtoIngredientes != null) {
            for (DTOIngrediente dtoIng : dtoIngredientes) {
                if (dtoIng != null && dtoIng.getNombre() != null) {
                    plato.addIngrediente(new Ingrediente(dtoIng.getNombre()));
                }
            }
        } else {
            new ArrayList<Ingrediente>();
        }

        return plato;
    }
}
