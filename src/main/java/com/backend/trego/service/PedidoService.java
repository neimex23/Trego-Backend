package com.backend.trego.service;

import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.ProductoPedido;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.DTOs.DTOCarrito;
import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;
import com.backend.trego.entity.DTOs.DTOProductoCarrito;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.entity.DTOs.DTDireccion;
import com.backend.trego.entity.Enums.EnumEstadoPedido;
import com.backend.trego.exception.RestauranteCerradoException;
import com.backend.trego.repository.ClienteRepository;
import com.backend.trego.repository.ProductoRepository;
import com.backend.trego.repository.RestauranteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PedidoService {

    private final RestauranteService restauranteService;
    private final PagoService pagoService;
    private final OrdenesService ordenesService;
    private final CurrentUserService currentUserService;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProductoRepository productoRepository;

    public PedidoService(RestauranteService restauranteService,
                         PagoService pagoService,
                         OrdenesService ordenesService,
                         CurrentUserService currentUserService,
                         ClienteRepository clienteRepository,
                         RestauranteRepository restauranteRepository,
                         ProductoRepository productoRepository) {
        this.restauranteService = restauranteService;
        this.pagoService = pagoService;
        this.ordenesService = ordenesService;
        this.currentUserService = currentUserService;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.productoRepository = productoRepository;
    }

    // Flujo principal "realizar pedido": valida el restaurante, crea el pedido a
    // partir del carrito del cliente autenticado, genera la preferencia de pago
    // en MercadoPago y devuelve la preferencia (con la URL de checkout) para que
    // el front redirija a la pasarela.
    @Transactional
    public DTOPreferenciaMP confirmarPedido(DTOCarrito carritoDTO, DTODireccion direccionDTO,
                                            String restauranteId) {
        // Valida existencia del restaurante (404 si no existe).
        DTORestaurante restauranteDTO = restauranteService.obtenerRestaurante(restauranteId);

        Date fecha = obtenerHoraActual();

        // Crea y persiste el pedido. Lanza RestauranteCerradoException (409) si el
        // restaurante no está operativo.
        Pedido pedido = crearPedido(carritoDTO, direccionDTO, fecha, restauranteDTO);

        // Genera la preferencia de pago delegando en PagoService -> MercadoPagoService.
        DTOPedido pedidoDTO = new DTOPedido();
        pedidoDTO.setIdPedido(pedido.getIdPedido());
        pedidoDTO.setTotal((double) pedido.getTotal());
        DTOPreferenciaMP preferencia = pagoService.crearPreferencia(pedidoDTO);

        return preferencia;
    }

    // Construye el pedido a partir del carrito y lo guarda. Antes de crearlo
    // verifica que el restaurante esté abierto.
    @Transactional
    public Pedido crearPedido(DTOCarrito carritoDTO, DTODireccion direccionDTO,
                              Date fecha, DTORestaurante restauranteDTO) {
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

        Cliente cliente = clienteRepository.findByUidCliente(currentUserService.getCurrentUid())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        Restaurante restaurante = restauranteRepository.findById(restauranteDTO.getIdRestaurante())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Restaurante no encontrado"));

        Pedido pedido = new Pedido(0f, EnumEstadoPedido.Solicitado, mapDireccion(direccionDTO), null);
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);

        for (DTOProductoCarrito linea : carritoDTO.getProductos()) {
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

    public Date obtenerHoraActual() {
        return new Date();
    }

    public boolean verificarRestauranteAbierto(String restauranteID) {
        return restauranteService.estaAbierto(restauranteID);
    }

    private DTDireccion mapDireccion(DTODireccion d) {
        if (d == null) {
            return null;
        }
        return new DTDireccion(d.getCalle(), d.getNumero(), d.getApartamento(),
                d.getEsquina(), d.getLatitud(), d.getLongitud());
    }

    public List<DTOPedido> listarPedidosConfirmados(String restauranteId) {
        // TODO: implementar
        return List.of();
    }

    public DTOPedido actualizarEstadoPedido(DTOPedido pedidoDTO, EnumEstadoPedido estado) {
        // TODO: implementar
        return null;
    }

    public Integer calcularTiempoPreparacion() {
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

    public void pagoConfirmado(DTOPreferenciaMP preferenciaDTO) {
        // TODO: implementar
    }
}
