package com.backend.trego.entity.DTOs;

import java.util.List;

import com.backend.trego.entity.Articulo;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.Enums.EnumCategoriaProducto;
import com.backend.trego.entity.Enums.EnumTipoProducto;

public class DTOArticulo extends DTOProducto {

    public DTOArticulo() {
        super();
        setTipo(EnumTipoProducto.Articulo);
    }

    public DTOArticulo(Integer idProducto, String nombre, String descripcion, Double precio, String urlImagen,
            EnumCategoriaProducto categoria, Integer idRestaurante, List<DTOIngrediente> ingredientes,
            Boolean disponible) {
        super(idProducto, nombre, descripcion, precio, urlImagen, categoria, idRestaurante, ingredientes, disponible);
        setTipo(EnumTipoProducto.Articulo);
    }

    @Override
    public Producto toEntity() {
        float precio = getPrecio() != null ? getPrecio().floatValue() : 0f;

        return new Articulo(
                getNombre(),
                precio,
                getDescripcion(),
                getUrlImagen());
    }
}
