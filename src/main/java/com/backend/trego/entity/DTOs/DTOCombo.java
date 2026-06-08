package com.backend.trego.entity.DTOs;

import java.util.ArrayList;
import java.util.List;

public class DTOCombo {

    private List<Integer> productosIncluidosIds = new ArrayList<>();

    protected DTOCombo() {
    }

    public DTOCombo(List<Integer> productosIncluidosIds) {
        if (productosIncluidosIds != null) {
            this.productosIncluidosIds = productosIncluidosIds;
        }
    }

    public List<Integer> getProductosIncluidosIds() {
        return productosIncluidosIds;
    }
}
