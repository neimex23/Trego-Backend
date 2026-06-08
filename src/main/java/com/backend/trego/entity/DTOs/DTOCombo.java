package com.backend.trego.entity.DTOs;

import java.util.ArrayList;
import java.util.List;

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

    public void setProductosIncluidosIds(List<Integer> productosIncluidosIds) {
        this.productosIncluidosIds = productosIncluidosIds != null
                ? productosIncluidosIds
                : new ArrayList<>();
    }
}
