package com.backend.trego.service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.trego.entity.Carrito;
import com.backend.trego.entity.Ingrediente;
import com.backend.trego.entity.LineaCarrito;
import com.backend.trego.entity.Plato;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.DTOs.DTOCarrito;
import com.backend.trego.entity.DTOs.DTOProductoPedido;
import com.backend.trego.repository.CarritoRepository;
import com.backend.trego.repository.PlatoRepository;
import com.backend.trego.repository.ProductoRepository;

// Carrito del cliente. Un cliente tiene un solo carrito activo y todos sus
// productos deben ser del mismo restaurante. Hacia el front cada línea va como DTOProductoPedido.
@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final PlatoRepository platoRepository;
    private final CurrentUserService currentUserService;
    private final IngredientePedidoService ingredientePedidoService;

    public CarritoService(CarritoRepository carritoRepository,
                          ProductoRepository productoRepository,
                          PlatoRepository platoRepository,
                          CurrentUserService currentUserService,
                          IngredientePedidoService ingredientePedidoService) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
        this.platoRepository = platoRepository;
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

        Integer idProducto = request.getProducto().getIdProducto();
        Producto productoBase = productoRepository.findById(idProducto)
                .orElseThrow(() -> new NoSuchElementException(
                        "Producto no encontrado con id: " + idProducto));
        final Producto producto = cargarProductoConIngredientes(productoBase, idProducto);
        if (!producto.getDisponible()) {
            throw new IllegalArgumentException("El producto no está disponible");
        }
        
        if (producto.isOfertaActiva() && (producto.getOferta() == null || !producto.getOferta().isVigente())) {
            producto.setOfertaActiva(false);
            productoRepository.save(producto);
        }

        float precioUnitario = producto.getPrecioConDescuento();

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

        // Se resuelven los ingredientes a quitar antes de buscar la línea: dos
        // pedidos del mismo producto con distinta personalización (p. ej. una burger
        // con pepinillos y otra sin) deben ser líneas separadas, no fusionarse.
        List<Ingrediente> ingredientesAQuitar = ingredientePedidoService.resolverIngredientesAQuitar(
                request.getIngredientesAQuitar(), producto);

        Optional<LineaCarrito> existente = carrito.getLineas().stream()
                .filter(l -> l.getProducto() != null
                        && l.getProducto().getIdProducto() == producto.getIdProducto()
                        && mismosIngredientes(l.getIngredientesAQuitar(), ingredientesAQuitar))
                .findFirst();

        if (existente.isPresent()) {
            LineaCarrito linea = existente.get();
            linea.setCantidad(linea.getCantidad() + cantidad);
            if (observaciones != null) {
                linea.setObservaciones(observaciones);
            }
        } else {
            LineaCarrito linea = new LineaCarrito(carrito, producto, cantidad, observaciones, precioUnitario);
            linea.setIngredientesAQuitar(ingredientesAQuitar);
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

        Producto producto = cargarProductoConIngredientes(
                linea.getProducto(), linea.getProducto().getIdProducto());

        if (producto.isOfertaActiva() && (producto.getOferta() == null || !producto.getOferta().isVigente())) {
            producto.setOfertaActiva(false);
            productoRepository.save(producto);
        }
        linea.setPrecioUnitario(producto.getPrecioConDescuento());

        if (nuevaCantidad != null) {
            linea.setCantidad(nuevaCantidad);
        }
        if (request.getObservaciones() != null) {
            linea.setObservaciones(request.getObservaciones());
        }
        if (request.getIngredientesAQuitar() != null) {
            linea.setIngredientesAQuitar(
                    ingredientePedidoService.resolverIngredientesAQuitar(
                            request.getIngredientesAQuitar(), producto));
        }

        carrito.recalcularTotal();
        carritoRepository.save(carrito);
        return linea.toDTO();
    }

    // Dos líneas son "el mismo ítem" solo si quitan exactamente el mismo conjunto
    // de ingredientes. Se compara por conjunto de ids (sin importar el orden).
    private boolean mismosIngredientes(List<Ingrediente> a, List<Ingrediente> b) {
        return idsIngredientes(a).equals(idsIngredientes(b));
    }

    private Set<Integer> idsIngredientes(List<Ingrediente> ingredientes) {
        Set<Integer> ids = new HashSet<>();
        if (ingredientes != null) {
            for (Ingrediente ing : ingredientes) {
                if (ing != null && ing.getIdIngrediente() != null) {
                    ids.add(ing.getIdIngrediente());
                }
            }
        }
        return ids;
    }

    private Producto cargarProductoConIngredientes(Producto producto, Integer idProducto) {
        if (producto instanceof Plato) {
            return platoRepository.findById(idProducto)
                    .map(plat -> (Producto) plat)
                    .orElse(producto);
        }
        return producto;
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
