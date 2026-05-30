package com.backend.trego.service;

import com.backend.trego.entity.Carrito;
import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Pago;
import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.ProductoPedido;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.DTOs.DTOCarrito;
import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;
import com.backend.trego.entity.DTOs.DTOProductoPedido;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.entity.Enums.EnumEstadoPedido;
import com.backend.trego.exception.RestauranteCerradoException;
import com.backend.trego.repository.ProductoRepository;
import com.backend.trego.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PedidoService {

    private final RestauranteService restauranteService;
    private final PagoService pagoService;
    private final OrdenesService ordenesService;
    private final CurrentUserService currentUserService;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CarritoService carritoService;

    public PedidoService(RestauranteService restauranteService,
                         PagoService pagoService,
                         OrdenesService ordenesService,
                         CurrentUserService currentUserService,
                         UsuarioRepository usuarioRepository,
                         ProductoRepository productoRepository,
                         CarritoService carritoService) {
        this.restauranteService = restauranteService;
        this.pagoService = pagoService;
        this.ordenesService = ordenesService;
        this.currentUserService = currentUserService;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.carritoService = carritoService;
    }

    // Flujo principal "realizar pedido": valida el restaurante, crea el pedido a
    // partir del carrito del cliente autenticado, genera la preferencia de pago
    // en MercadoPago y devuelve la preferencia (con la URL de checkout) para que
    // el front redirija a la pasarela.
    @Transactional
    public DTOPreferenciaMP confirmarPedido(DTODireccion direccionDTO) {
        DTOCarrito carritoDTO = carritoService.obtenerCarrito(); //Obtener Carrito actual

        // Valida existencia del restaurante (404 si no existe).
        DTORestaurante restauranteDTO = restauranteService.obtenerRestaurante(String.valueOf(carritoDTO.getIdRestaurante()));
                if (restauranteDTO == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante no encontrado con id: " + carritoDTO.getIdRestaurante());
                }
        
        Pedido pedido = crearPedido(carritoDTO, direccionDTO, restauranteDTO);

        // Genera la preferencia de pago delegando en PagoService -> MercadoPagoService.
        DTOPedido pedidoDTO = new DTOPedido(
                pedido.getIdPedido(),
                (double) pedido.getTotal(),
                carritoDTO.getProductos());
        DTOPreferenciaMP preferencia = pagoService.crearPreferencia(pedidoDTO);

        return preferencia;
    }

    // Construye el pedido a partir del carrito y lo guarda. Antes de crearlo
    // verifica que el restaurante esté abierto.
    @Transactional
    public Pedido crearPedido(DTOCarrito carritoDTO, DTODireccion direccionDTO,
                              DTORestaurante restauranteDTO) {
        if (restauranteDTO == null || restauranteDTO.getIdRestaurante() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurante inválido");
        }
        if (carritoDTO == null || carritoDTO.getProductos() == null || carritoDTO.getProductos().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío");
        }

        String restauranteId = String.valueOf(restauranteDTO.getIdRestaurante());
        if (!verificarRestauranteAbierto(restauranteId)) {
            throw new RestauranteCerradoException(
                   "El restaurante " + restauranteDTO.getNombre() + " está cerrado en este momento");
        }

        Cliente cliente = usuarioRepository.findClienteByUidCliente(currentUserService.getCurrentUid())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        Restaurante restaurante = usuarioRepository.findRestauranteById(restauranteDTO.getIdRestaurante())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Restaurante no encontrado"));

        Pedido pedido = new Pedido(0f, EnumEstadoPedido.Solicitado, mapDireccion(direccionDTO), null);
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);

        for (DTOProductoPedido linea : carritoDTO.getProductos()) {
            if (linea.getIdProducto() == null) {
                continue;
            }
            Producto producto = productoRepository.findById(linea.getIdProducto())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Producto no encontrado con id: " + linea.getIdProducto()));
            int cantidad = (linea.getCantidad() == null || linea.getCantidad() <= 0)
                    ? 1 : linea.getCantidad();
            float precioSuma = producto.getPrecio() * cantidad;
            ProductoPedido pp = new ProductoPedido(producto, cantidad, precioSuma, linea.getObservaciones());
            pedido.addProductoPedido(pp);
        }

        pedido.setTotal(pedido.calcularTotal());
        return ordenesService.guardar(pedido);
    }

    public DTORestaurante obtenerRestaurante(String idRestaurante) {
        return restauranteService.obtenerRestaurante(idRestaurante);
    }

    public boolean verificarRestauranteAbierto(String restauranteID) {
        return restauranteService.estaAbierto(restauranteID);
    }

    private DTODireccion mapDireccion(DTODireccion d) {
        if (d == null) {
            return null;
        }
        return new DTODireccion(d.getCalle(), d.getNumero(), d.getApartamento(),
                d.getEsquina(), d.getLatitud(), d.getLongitud());
    }

    public List<DTOPedido> listarPedidosConfirmados(EnumEstadoPedido estado, Integer idProducto) {
        var restauranteId = currentUserService.getCurrentId();

        return List.of();
    }

    public DTOPedido actualizarEstadoPedido(DTOPedido pedidoDTO, EnumEstadoPedido estado) {
        // TODO: implementar
        return null;
    }

    public Integer calcularTiempoPreparacion(List<DTOProductoPedido> productos) {
        // TODO: implementar
        return 0;
    }

    public DTOPedido actualizarPedido(DTOPedido pedidoDTO) {
        // TODO: implementar
        return null;
    }

    public void guardarPedido(DTOPedido pedidoDTO) {
        // TODO: implementar
    }

    public List<DTOPedido> listarPedidosPendientes(String restauranteId) {
        // TODO: implementar
        return List.of();
    }

    public DTOPedido actualizarHoraEntrega() {
        // TODO: implementar
        return null;
    }

    
    @Transactional
    public DTOPedido reembolsarPedido(DTOPedido pedidoDTO) {
        if (pedidoDTO == null || pedidoDTO.getIdPedido() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "DTOPedido inválido para reembolsar");
        }

        Pedido pedido = ordenesService.obtenerOFallar(pedidoDTO.getIdPedido());

        Pago pago = pedido.getPago();
        if (pago == null || pago.getIdTransaccion() == null || pago.getIdTransaccion().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El pedido " + pedido.getIdPedido() + " no tiene un pago asociado para reembolsar");
        }

        if (pedido.getEstado() == EnumEstadoPedido.Reembolsado) {
            // Aun así dejamos pasar a MP por la idempotencia, pero cortamos acá
            // para evitar trabajo innecesario y dar feedback claro al front.
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El pedido " + pedido.getIdPedido() + " ya estaba reembolsado");
        }

        String idempotencyKey = "reembolso-pedido-" + pedido.getIdPedido();
        pagoService.reembolsar(pago.getIdTransaccion(), idempotencyKey);

        pedido.setEstado(EnumEstadoPedido.Reembolsado);
        ordenesService.guardar(pedido);

        return DTOPedido.desde(pedido);
    }
}
