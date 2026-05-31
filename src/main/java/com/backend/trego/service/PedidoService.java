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
import com.backend.trego.entity.DTOs.DTOProductoPedido;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.entity.Enums.EnumEstadoPedido;
import com.backend.trego.exception.RestauranteCerradoException;
import com.backend.trego.repository.ClienteRepository;
import com.backend.trego.repository.ProductoRepository;
import com.backend.trego.repository.RestauranteRepository;

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

        // Crea y persiste el pedido. Lanza RestauranteCerradoException (409) si el
        // restaurante no está operativo.
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

        Cliente cliente = clienteRepository.findByUidCliente(currentUserService.getCurrentUid())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        Restaurante restaurante = restauranteRepository.findById(restauranteDTO.getIdRestaurante())
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

	public List<DTOPedido> listarPedidosConfirmados(Integer idProducto, String estado) {
        var restauranteId = currentUserService.getCurrentId();
        Integer idRestaurante = Integer.valueOf(restauranteId);

        List<Pedido> pedidos = pedidoRepository.findByRestauranteIdUsuarioAndEstadoNot(idRestaurante, EnumEstadoPedido.Pendiente);
        List<DTOPedido> pedidosDTO = pedidos.stream().map(DTOPedido::desde).collect(Collectors.toList());
        if (estado != null && !estado.isBlank()) {
            pedidosDTO = pedidosDTO.stream()
                    .filter(p -> p.getEstado().name().equalsIgnoreCase(estado.trim().replace(" ", "_")))
                    .collect(Collectors.toList());
        }
        if (idProducto != null) {
            pedidosDTO = filtrarPorProductos(pedidosDTO, idProducto);
        }
        return pedidosDTO;
    }

    // Método auxiliar privado para procesar el filtro por producto de forma limpia
    private List<DTOPedido> filtrarPorProductos(List<DTOPedido> lista, Integer idProducto) {
        return lista.stream()
                .filter(pedido -> pedido.getProductos() != null && pedido.getProductos().stream()
                        .anyMatch(linea -> idProducto.equals(linea.getIdProducto())))
                .collect(Collectors.toList());
    }

    public DTOPedido actualizarEstadoPedido(DTOPedido pedidoDTO, EnumEstadoPedido estado) {
        // TODO: implementar
        return null;
    }

    public Integer calcularTiempoPreparacion() {
        // TODO: implementar
        return 0;
    }

    public DTOPedido actualizarEstadoPedido(DTOPedido pedidoDTO, String estadoStr) {
        Pedido pedido = pedidoRepository.findById(pedidoDTO.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        EnumEstadoPedido estadoActual = pedido.getEstado();
        EnumEstadoPedido nuevoEstado = parsearEstado(estadoStr);

        if (!verificarSaltoEstado(estadoActual, nuevoEstado)) {
            throw new RuntimeException("Salto de estado incorrecto: No se puede pasar de " + estadoActual + " a " + nuevoEstado);
        }

        pedido.setEstado(nuevoEstado);
        if (nuevoEstado == EnumEstadoPedido.Entregado) {
            pedido.setHorarioEntrega(LocalDateTime.now());
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        DTOPedido dtoActualizado = DTOPedido.desde(pedidoGuardado);
        try {
            if (nuevoEstado == EnumEstadoPedido.EnCamino) {
            notificacionesService.notificarPedidoEnCamino(dtoActualizado, pedidoGuardado.getTiempoPreparacion());
            
            } else if (nuevoEstado == EnumEstadoPedido.Entregado) {
                
                String emailCliente = pedidoGuardado.getCliente().getEmail();
                String tokenPush = "TOKEN_SIMULADO"; 
                notificacionesService.enviarPush(tokenPush, 0, "Pedido Entregado", emailCliente);
            }
        } catch (Exception e) {
            System.err.println("Error enviando notificación: " + e.getMessage());
        }

        return dtoActualizado;
    }

    private boolean verificarSaltoEstado(EnumEstadoPedido actual, EnumEstadoPedido nuevo) {
        if (actual == EnumEstadoPedido.EnPreparacion && nuevo == EnumEstadoPedido.EnCamino) return true;
        if (actual == EnumEstadoPedido.EnCamino && nuevo == EnumEstadoPedido.Entregado) return true;
        return false;
    }

    private EnumEstadoPedido parsearEstado(String estadoStr) {
        try {
            return EnumEstadoPedido.valueOf(estadoStr.trim());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("El estado enviado no es válido.");
        }
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
