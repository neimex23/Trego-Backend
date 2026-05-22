package com.backend.trego.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.trego.entity.Carrito;
import com.backend.trego.entity.LineaCarrito;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.DTOs.DTOAgregarAlCarritoRequest;
import com.backend.trego.entity.DTOs.DTOCarrito;
import com.backend.trego.entity.DTOs.DTOProductoCarrito;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.repository.CarritoRepository;
import com.backend.trego.repository.ProductoRepository;

// Carrito del cliente. Un cliente tiene un solo carrito activo y todos sus
// productos deben ser del mismo restaurante. Por dentro usa LineaCarrito
// (producto + cantidad), pero hacia el front cada línea va como DTOProductoCarrito.
@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final CurrentUserService currentUserService;

    public CarritoService(CarritoRepository carritoRepository,
                          ProductoRepository productoRepository,
                          CurrentUserService currentUserService) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public DTOCarrito obtenerCarrito() {
        String uidCliente = currentUserService.getCurrentUid();
        return carritoRepository.findByUidCliente(uidCliente)
                .map(Carrito::toDTO)
                .orElse(null);
    }

    @Transactional
    public DTOCarrito agregarProducto(DTOAgregarAlCarritoRequest request) {
        if (request == null || request.getProducto() == null) {
            throw new IllegalArgumentException("La petición y la línea del carrito no pueden ser nulas");
        }
        DTOProductoCarrito productoDTO = request.getProducto();
        DTORestaurante restauranteDTO = request.getRestaurante();

        if (productoDTO.getIdProducto() == null) {
            throw new IllegalArgumentException("DTOProductoCarrito.idProducto es obligatorio");
        }
        if (restauranteDTO == null || restauranteDTO.getIdRestaurante() == null) {
            throw new IllegalArgumentException("DTORestaurante.idRestaurante es obligatorio");
        }

        int cantidad = (productoDTO.getCantidad() == null || productoDTO.getCantidad() <= 0)
                ? 1 : productoDTO.getCantidad();
        String observaciones = productoDTO.getObservaciones();

        String uidCliente = currentUserService.getCurrentUid();

        Producto producto = productoRepository.findById(productoDTO.getIdProducto())
                .orElseThrow(() -> new NoSuchElementException(
                        "Producto no encontrado con id: " + productoDTO.getIdProducto()));

        Integer idRestauranteSolicitado = restauranteDTO.getIdRestaurante();

        Carrito carrito = carritoRepository.findByUidCliente(uidCliente)
                .orElseGet(() -> new Carrito(uidCliente, idRestauranteSolicitado));

        // No se mezclan productos de distintos restaurantes
        if (!carrito.getLineas().isEmpty()
                && carrito.getIdRestaurante() != null
                && !carrito.getIdRestaurante().equals(idRestauranteSolicitado)) {
            throw new IllegalArgumentException(
                    "No se pueden agregar productos de distintos restaurantes al carrito");
        }

        if (carrito.getIdRestaurante() == null) {
            carrito.setIdRestaurante(idRestauranteSolicitado);
        }

        // Si el producto ya está en el carrito, sumamos cantidad en vez de duplicar la línea
        Optional<LineaCarrito> existente = carrito.getLineas().stream()
                .filter(l -> l.getProducto() != null
                        && l.getProducto().getIdProducto() == producto.getIdProducto())
                .findFirst();

        if (existente.isPresent()) {
            LineaCarrito linea = existente.get();
            linea.setCantidad(linea.getCantidad() + cantidad);
            if (observaciones != null) {
                linea.setObservaciones(observaciones);
            }
        } else {
            LineaCarrito nuevaLinea = new LineaCarrito(carrito, producto, cantidad, observaciones);
            carrito.addLinea(nuevaLinea);
        }

        carrito.recalcularTotal();
        Carrito guardado = carritoRepository.save(carrito);
        return guardado.toDTO();
    }


    @Transactional
    public DTOProductoCarrito modificarProductoCarrito(DTOProductoCarrito productoDTO) {
        if (productoDTO == null || productoDTO.getIdProducto() == null) {
            throw new IllegalArgumentException("Debe especificarse el idProducto a modificar");
        }
        String uidCliente = currentUserService.getCurrentUid();
        Carrito carrito = carritoRepository.findByUidCliente(uidCliente)
                .orElseThrow(() -> new NoSuchElementException(
                        "El usuario no tiene un carrito activo"));

        LineaCarrito linea = carrito.getLineas().stream()
                .filter(l -> l.getProducto() != null
                        && l.getProducto().getIdProducto() == productoDTO.getIdProducto())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "El producto " + productoDTO.getIdProducto() + " no está en el carrito"));

        Integer nuevaCantidad = productoDTO.getCantidad();
        if (nuevaCantidad != null && nuevaCantidad <= 0) {
            carrito.removeLinea(linea);
            carritoRepository.save(carrito);
            return null;
        }

        if (nuevaCantidad != null) {
            linea.setCantidad(nuevaCantidad);
        }
        if (productoDTO.getObservaciones() != null) {
            linea.setObservaciones(productoDTO.getObservaciones());
        }

        carrito.recalcularTotal();
        carritoRepository.save(carrito);
        return linea.toDTO();
    }

    @Transactional
    public DTOCarrito actualizarTotal() {
        String uidCliente = currentUserService.getCurrentUid();
        Carrito carrito = carritoRepository.findByUidCliente(uidCliente)
                .orElseThrow(() -> new NoSuchElementException(
                        "El usuario no tiene un carrito activo"));
        carrito.recalcularTotal();
        carritoRepository.save(carrito);
        return carrito.toDTO();
    }

    @Transactional
    public DTOCarrito eliminarProducto(DTOProductoCarrito productoDTO) {
        if (productoDTO == null || productoDTO.getIdProducto() == null) {
            throw new IllegalArgumentException("DTOProductoCarrito.idProducto es obligatorio");
        }
        String uidCliente = currentUserService.getCurrentUid();
        Optional<Carrito> carritoOpt = carritoRepository.findByUidCliente(uidCliente);
        if (carritoOpt.isEmpty()) {
            return null;
        }
        Carrito carrito = carritoOpt.get();

        Optional<LineaCarrito> lineaOpt = carrito.getLineas().stream()
                .filter(l -> l.getProducto() != null
                        && l.getProducto().getIdProducto() == productoDTO.getIdProducto())
                .findFirst();

        if (lineaOpt.isEmpty()) {
            return null;
        }

        carrito.removeLinea(lineaOpt.get());
        Carrito guardado = carritoRepository.save(carrito);
        return guardado.toDTO();
    }

    @Transactional
    public void limpiarCarrito() {
        String uidCliente = currentUserService.getCurrentUid();
        carritoRepository.findByUidCliente(uidCliente)
                .ifPresent(carritoRepository::delete);
    }

    @Transactional
    public DTOCarrito limpiarItemsCarrito() {
        String uidCliente = currentUserService.getCurrentUid();
        Carrito carrito = carritoRepository.findByUidCliente(uidCliente)
                .orElse(null);
        if (carrito == null) {
            return null;
        }
        carrito.vaciar();
        carritoRepository.save(carrito);
        return carrito.toDTO();
    }
}
