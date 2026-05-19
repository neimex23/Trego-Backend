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
    public void agregarProducto(DTOProducto productoDTO) {

        String uidCliente = currentUserService.getCurrentUid();

        // Buscar carrito del usuario
        Carrito carrito = carritoRepository.findAll().stream()
                .filter(c -> c.getUidCliente().equals(uidCliente))
                .findFirst()
                .orElseGet(() -> {
                    Carrito nuevoCarrito = new Carrito(
                            uidCliente,
                            productoDTO.getIdRestaurante(),
                            new ArrayList<>(),
                            0.0
                    );

                    return carritoRepository.save(nuevoCarrito);
                });

        // Validar que todos los productos sean del mismo restaurante
        if (!carrito.getProductos().isEmpty()
                && carrito.getIdRestaurante() != productoDTO.getIdRestaurante()) {

            throw new IllegalArgumentException(
                    "No se pueden agregar productos de distintos restaurantes al carrito"
            );
        }

        // Buscar producto existente
        Optional<Producto> productoExistente = carrito.getProductos().stream()
                .filter(producto ->
                        producto.getIdProducto() == productoDTO.getIdProducto())
                .findFirst();

        if (!productoExistente.isPresent()) {
            carrito.getProductos().add(productoDTO.toProducto());  producto.getCantidad() + productoDTO.getCantidad());
        } 

        // Recalcular total
        double total = carrito.getProductos().stream()
                .mapToDouble(p -> p.getPrecio() * p.getCantidad())
                .sum();

        carrito.setTotal(total);
        carritoRepository.save(carrito);
    }


    /**
     * Obtiene el carrito actual del usuario.
     */
    public DTOCarrito obtenerCarrito() {
        String uidCliente = currentUserService.getCurrentUid();
        Carrito carrito = carritoRepository.findAll().stream()
                .filter(c -> c.getUidCliente().equals(uidCliente))
                .findFirst()
                .orElse(null);
        return carrito;
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
