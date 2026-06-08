package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.SubCategoria;
import com.backend.trego.entity.Enums.EnumCategoriaProducto;

/**
 * DTO de SubCategoria. La SubCategoria es la asociación concreta a la que se
 * cuelga un Producto (ej: "Pizzas", "Hamburguesas"), y a su vez pertenece a una
 * categoría madre del enum {@link EnumCategoriaProducto}.
 */
public class DTOSubCategoria {

    private Integer idSubCategoria;
    private String nombre;
    private EnumCategoriaProducto categoria;
    private String urlImagen;

    protected DTOSubCategoria() {
    }

    public DTOSubCategoria(Integer idSubCategoria, String nombre, EnumCategoriaProducto categoria, String urlImagen) {
        this.idSubCategoria = idSubCategoria;
        this.nombre = nombre;
        this.categoria = categoria;
        this.urlImagen = urlImagen;
    }

    public static DTOSubCategoria desde(SubCategoria sub) {
        if (sub == null) {
            return null;
        }
        return new DTOSubCategoria(
                sub.getIdSubCategoria(),
                sub.getNombre(),
                sub.getCategoria(),
                sub.getUrlImagen());
    }

    public Integer getIdSubCategoria() {
        return idSubCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public EnumCategoriaProducto getCategoria() {
        return categoria;
    }

    public String getUrlImagen() {
        return urlImagen;
    }
}
