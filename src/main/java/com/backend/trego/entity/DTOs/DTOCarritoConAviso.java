package com.backend.trego.entity.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;

// Respuesta del endpoint agregar-al-carrito.
// Siempre incluye el carrito actualizado; el campo aviso sólo aparece
// en el JSON cuando no es null (oferta desactivada por fechas vencidas).
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DTOCarritoConAviso {

    private DTOCarrito carrito;
    private String aviso;

    public DTOCarritoConAviso(DTOCarrito carrito) {
        this.carrito = carrito;
    }

    public DTOCarritoConAviso(DTOCarrito carrito, String aviso) {
        this.carrito = carrito;
        this.aviso = aviso;
    }

    public DTOCarrito getCarrito() { return carrito; }
    public String getAviso() { return aviso; }
}
