package com.backend.trego.entity.DTOs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DTOCombo {

    private List<ProductoIncluido> productosIncluidos = new ArrayList<>();

    // Clase interna estática (puede ser también una clase aparte)
    public static class ProductoIncluido {
        private Integer id;
        private String nombre;

        public ProductoIncluido() {
        }

        public ProductoIncluido(Integer id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof ProductoIncluido))
                return false;
            ProductoIncluido that = (ProductoIncluido) o;
            return Objects.equals(id, that.id) && Objects.equals(nombre, that.nombre);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, nombre);
        }
    }

    protected DTOCombo() {
    }

    public DTOCombo(List<ProductoIncluido> productosIncluidos) {
        if (productosIncluidos != null) {
            this.productosIncluidos = new ArrayList<>(productosIncluidos);
        }
    }

    public List<ProductoIncluido> getProductosIncluidos() {
        return productosIncluidos;
    }

    public List<Integer> getProductosIncluidosIds() {
        return productosIncluidos.stream()
                .map(ProductoIncluido::getId)
                .collect(Collectors.toList());
    }
}