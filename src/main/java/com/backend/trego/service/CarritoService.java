package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTOCarrito;
import com.backend.trego.entity.DTOs.DTOProducto;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.entity.Carrito;
import com.backend.trego.repository.CarritoRepository;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la gestión del Carrito de compras del cliente.
 *
 * Las firmas siguen el Documento de Diseño (Tabla 7 - CarritoService).
 */
@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CurrentUserService currentUserService;

    public CarritoService(CarritoRepository carritoRepository, CurrentUserService currentUserService) {
        this.carritoRepository = carritoRepository;
        this.currentUserService = currentUserService;
    }

    /**
     * Agrega un producto al carrito actual.
     */
    public void agregarProducto(DTOProducto productoDTO, DTORestaurante restauranteDTO) {
        boolean existeCarrito = carritoRepository.findAll().stream()
        .anyMatch(carrito -> carrito.getUidCliente().equals(currentUserService.getCurrentUid()));

        if (!existeCarrito) {
           //Crear Carrito nuevo 
        } 
        
        boolean existeProducto = carritoRepository.findAll().stream()
        .filter(carrito -> carrito.getUidCliente()
                .equals(currentUserService.getCurrentUid()))
        .flatMap(carrito -> carrito.getProductos().stream())
        .anyMatch(producto -> producto.getIdProducto() == (productoDTO.getIdProducto()));
       
        if (existeProducto) {
            modificarProductoCarrito(productoDTO);
        } else {
            //Agregar producto al carrito existente
        }
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
