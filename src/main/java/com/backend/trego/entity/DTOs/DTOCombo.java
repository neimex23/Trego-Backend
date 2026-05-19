package com.backend.trego.entity.DTOs;

import java.util.ArrayList;
import java.util.List;

import com.backend.trego.entity.Combo;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.Enums.EnumCategoriaProducto;
import com.backend.trego.entity.Enums.EnumTipoProducto;

public class DTOCombo extends DTOProducto {

    private List<Integer> productosIncluidosIds = new ArrayList<>();

    public DTOCombo() {
        super();
        setTipo(EnumTipoProducto.Combo);
    }

    public DTOCombo(Integer idProducto, String nombre, String descripcion, Double precio, String urlImagen,
            EnumCategoriaProducto categoria, Integer idRestaurante, List<DTOIngrediente> ingredientes,
            Boolean disponible, List<Integer> productosIncluidosIds) {
        super(idProducto, nombre, descripcion, precio, urlImagen, categoria, idRestaurante, ingredientes, disponible);
        this.productosIncluidosIds = productosIncluidosIds != null ? productosIncluidosIds : new ArrayList<>();
        setTipo(EnumTipoProducto.Combo);
    }

    public List<Integer> getProductosIncluidosIds() {
        return productosIncluidosIds;
    }

    public void setProductosIncluidosIds(List<Integer> productosIncluidosIds) {
        this.productosIncluidosIds = productosIncluidosIds;
    }

    @Override
    public Producto toEntity() {
        float precio = getPrecio() != null ? getPrecio().floatValue() : 0f;
        return new Combo(
                getNombre(),
                precio,
                getDescripcion(),
                getUrlImagen());
    }
}
