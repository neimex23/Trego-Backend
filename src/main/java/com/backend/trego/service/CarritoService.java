package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTOCarrito;
import com.backend.trego.entity.DTOs.DTOProducto;

import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la gestión del Carrito de compras del cliente.
 *
 * Las firmas siguen el Documento de Diseño (Tabla 7 - CarritoService).
 */
@Service
public class CarritoService {

    public CarritoService() {
        // TODO: inyectar repositorio o almacenamiento en sesión del carrito
    }

    /**
     * Agrega un producto al carrito actual.
     */
    public void agregarProducto(DTOProducto productoDTO) {
        // TODO: implementar
    }

    /**
     * Obtiene el carrito actual del usuario.
     */
    public DTOCarrito obtenerCarrito() {
        // TODO: implementar
        return null;
    }

    /**
     * Modifica un producto ya existente dentro del carrito (cantidad, observaciones, etc.).
     */
    public DTOProducto modificarProductoCarrito(DTOProducto productoDTO) {
        // TODO: implementar
        return null;
    }

    /**
     * Recalcula y actualiza el total del carrito.
     */
    public DTOCarrito actualizarTotal(DTOCarrito carritoDTO) {
        // TODO: implementar
        return null;
    }

    /**
     * Elimina un producto del carrito.
     */
    public boolean eliminarProducto(DTOProducto productoDTO) {
        // TODO: implementar
        return false;
    }

    /**
     * Limpia el carrito completo (vacía todos los items y resetea el total).
     */
    public void limpiarCarrito() {
        // TODO: implementar
    }

    /**
     * Limpia solo los ítems del carrito, manteniendo otros metadatos.
     */
    public DTOCarrito limpiarItemsCarrito(DTOCarrito carritoDTO) {
        // TODO: implementar
        return null;
    }
}
