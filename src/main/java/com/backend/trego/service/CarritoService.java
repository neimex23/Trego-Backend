package com.backend.trego.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.trego.entity.Carrito;
import com.backend.trego.entity.LineaCarrito;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.DTOs.DTOCarrito;
import com.backend.trego.entity.DTOs.DTOProductoPedido;
import com.backend.trego.repository.CarritoRepository;
import com.backend.trego.repository.ProductoRepository;

// Carrito del cliente. Un cliente tiene un solo carrito activo y todos sus
// productos deben ser del mismo restaurante. Hacia el front cada línea va como DTOProductoPedido.
@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final CurrentUserService currentUserService;
    private final IngredientePedidoService ingredientePedidoService;

    public CarritoService(CarritoRepository carritoRepository,
                          ProductoRepository productoRepository,
                          CurrentUserService currentUserService,
                          IngredientePedidoService ingredientePedidoService) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
        this.currentUserService = currentUserService;
        this.ingredientePedidoService = ingredientePedidoService;
    }

    @Transactional
    public DTOCarrito obtenerCarrito() {
        String uidCliente = currentUserService.getCurrentUid();
        return carritoRepository.findByUidCliente(uidCliente)
                .map(Carrito::toDTO)
                .orElse(null);
    }

    @Transactional
    public DTOCarrito agregarProducto(DTOProductoPedido request) {
        if (request == null || request.getProducto() == null || request.getProducto().getIdProducto() == null) {
            throw new IllegalArgumentException("La petición debe incluir producto.idProducto");
        }

        Producto producto = productoRepository.findById(request.getProducto().getIdProducto())
                .orElseThrow(() -> new NoSuchElementException(
                        "Producto no encontrado con id: " + request.getProducto().getIdProducto()));

        Integer idRestauranteRequest = request.getProducto().getIdRestaurante();
        final Integer idRestauranteSolicitado = idRestauranteRequest != null
                ? idRestauranteRequest
                : (producto.getRestaurante() != null ? producto.getRestaurante().getIdUsuario() : null);
        if (idRestauranteSolicitado == null) {
            throw new IllegalArgumentException("producto.idRestaurante es obligatorio");
        }

        Integer cantidad = (request.getCantidad() == null || request.getCantidad() <= 0) ? 1 : request.getCantidad();
        String observaciones = request.getObservaciones();

        String uidCliente = currentUserService.getCurrentUid();

        Carrito carrito = carritoRepository.findByUidCliente(uidCliente)
                .orElseGet(() -> new Carrito(uidCliente, idRestauranteSolicitado));

        if (!carrito.getLineas().isEmpty()
                && carrito.getIdRestaurante() != null
                && !carrito.getIdRestaurante().equals(idRestauranteSolicitado)) {
            throw new IllegalArgumentException(
                    "No se pueden agregar productos de distintos restaurantes al carrito");
        }

        if (carrito.getIdRestaurante() == null) {
            carrito.setIdRestaurante(idRestauranteSolicitado);
        }

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
            if (request.getIngredientesAQuitar() != null) {
                linea.setIngredientesAQuitar(
                        ingredientePedidoService.resolverIngredientesAQuitar(
                                request.getIngredientesAQuitar(), producto));
            }
        } else {
            LineaCarrito linea = new LineaCarrito(carrito, producto, cantidad, observaciones);
            linea.setIngredientesAQuitar(
                    ingredientePedidoService.resolverIngredientesAQuitar(
                            request.getIngredientesAQuitar(), producto));
            carrito.addLinea(linea);
        }

        carrito.recalcularTotal();
        return carritoRepository.save(carrito).toDTO();
    }

    @Transactional
    public DTOProductoPedido modificarProductoCarrito(DTOProductoPedido request) {
        if (request == null || request.getIdProducto() == null) {
            throw new IllegalArgumentException("Debe especificarse producto.idProducto a modificar");
        }
        String uidCliente = currentUserService.getCurrentUid();
        Carrito carrito = carritoRepository.findByUidCliente(uidCliente)
                .orElseThrow(() -> new NoSuchElementException("El usuario no tiene un carrito activo"));

        LineaCarrito linea = carrito.getLineas().stream()
                .filter(l -> l.getProducto() != null
                        && l.getProducto().getIdProducto() == request.getIdProducto())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "El producto " + request.getIdProducto() + " no está en el carrito"));

        Integer nuevaCantidad = request.getCantidad();
        if (nuevaCantidad != null && nuevaCantidad <= 0) {
            carrito.removeLinea(linea);
            carritoRepository.save(carrito);
            return null;
        }

        if (nuevaCantidad != null) {
            linea.setCantidad(nuevaCantidad);
        }
        if (request.getObservaciones() != null) {
            linea.setObservaciones(request.getObservaciones());
        }
        if (request.getIngredientesAQuitar() != null) {
            linea.setIngredientesAQuitar(
                    ingredientePedidoService.resolverIngredientesAQuitar(
                            request.getIngredientesAQuitar(), linea.getProducto()));
        }

        carrito.recalcularTotal();
        carritoRepository.save(carrito);
        return linea.toDTO();
    }

    @Transactional
    public DTOCarrito actualizarTotal() {
        String uidCliente = currentUserService.getCurrentUid();
        Carrito carrito = carritoRepository.findByUidCliente(uidCliente)
                .orElseThrow(() -> new NoSuchElementException("El usuario no tiene un carrito activo"));
        carrito.recalcularTotal();
        carritoRepository.save(carrito);
        return carrito.toDTO();
    }

    @Transactional
    public DTOCarrito eliminarProducto(DTOProductoPedido request) {
        if (request == null || request.getIdProducto() == null) {
            throw new IllegalArgumentException("producto.idProducto es obligatorio");
        }
        String uidCliente = currentUserService.getCurrentUid();
        Optional<Carrito> carritoOpt = carritoRepository.findByUidCliente(uidCliente);
        if (carritoOpt.isEmpty()) {
            return null;
        }
        Carrito carrito = carritoOpt.get();

        Optional<LineaCarrito> lineaOpt = carrito.getLineas().stream()
                .filter(l -> l.getProducto() != null
                        && l.getProducto().getIdProducto() == request.getIdProducto())
                .findFirst();

        if (lineaOpt.isEmpty()) {
            return null;
        }

        carrito.removeLinea(lineaOpt.get());
        return carritoRepository.save(carrito).toDTO();
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
        Carrito carrito = carritoRepository.findByUidCliente(uidCliente).orElse(null);
        if (carrito == null) {
            return null;
        }
        carrito.vaciar();
        carritoRepository.save(carrito);
        return carrito.toDTO();
    }

    public void limpiarItemsCarrito(String uidCliente) {
        carritoRepository.findByUidCliente(uidCliente)
                .ifPresent(carrito -> {
                    carrito.vaciar();
                    carritoRepository.save(carrito);
                });
    }
}
