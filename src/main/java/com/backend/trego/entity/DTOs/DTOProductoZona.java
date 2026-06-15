package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Producto;
import com.backend.trego.entity.Restaurante;

/**
 * DTO de salida para los listados por zona (subcategoría y oferta). Envuelve el
 * {@link DTOProducto} junto con el nombre y la calificación promedio del
 * restaurante que lo ofrece, evitando que el cliente tenga que resolver el
 * restaurante por separado.
 */
public class DTOProductoZona {

    private DTOProducto producto;
    private String nombreRestaurante;
    private float calificacionProm;
    private DTODireccion direccion;

    protected DTOProductoZona() {
    }

    public DTOProductoZona(DTOProducto producto, String nombreRestaurante, float calificacionProm,
            DTODireccion direccion) {
        this.producto = producto;
        this.nombreRestaurante = nombreRestaurante;
        this.calificacionProm = calificacionProm;
        this.direccion = direccion;
    }

    public DTOProducto getProducto() {
        return producto;
    }

    public String getNombreRestaurante() {
        return nombreRestaurante;
    }

    public float getCalificacionProm() {
        return calificacionProm;
    }

    public DTODireccion getDireccion() {
        return direccion;
    }

    public static DTOProductoZona desde(Producto producto) {
        if (producto == null) {
            return null;
        }
        Restaurante restaurante = producto.getRestaurante();
        String nombre = restaurante != null ? restaurante.getNombre() : null;
        float calificacion = restaurante != null ? restaurante.getCalificacionProm() : 0;
        DTODireccion direccion = restaurante != null ? restaurante.getDireccion() : null;
        return new DTOProductoZona(DTOProducto.desde(producto), nombre, calificacion, direccion);
    }
}
