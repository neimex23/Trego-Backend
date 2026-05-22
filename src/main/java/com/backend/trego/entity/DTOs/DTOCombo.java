package com.backend.trego.entity.DTOs;

import java.util.ArrayList;
import java.util.List;

import com.backend.trego.entity.Combo;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.Enums.EnumCategoriaProducto;
import com.backend.trego.entity.Enums.EnumTipoProducto;

public class DTOCombo {

    private List<Integer> productosIncluidosIds = new ArrayList<>();

    public DTOCombo() {
    }

    public DTOCombo(List<Integer> productosIncluidosIds) {
        this.productosIncluidosIds = productosIncluidosIds;
    }

    public List<Integer> getProductosIncluidosIds() {
        return productosIncluidosIds;
    }
}
