package com.backend.trego.service;

import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Pago;
import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.Plato;
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
import com.backend.trego.exception.PedidoCanceladoException;
import com.backend.trego.exception.RestauranteCerradoException;
import com.backend.trego.repository.PedidoRepository;
import com.backend.trego.repository.ProductoRepository;
import com.backend.trego.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

// Servicio único de pedidos: combina el CRUD que antes vivía en OrdenesService
// con la lógica de negocio (confirmación, actualización de estado, reembolso).
// Trabaja directamente contra PedidoRepository.
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final RestauranteService restauranteService;
    private final PagoService pagoService;
    private final NotificacionesService notificacionesService;
    private final CurrentUserService currentUserService;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CarritoService carritoService;
    private final GeoapifyService geoapifyService;
    private final IngredientePedidoService ingredientePedidoService;

    public PedidoService(PedidoRepository pedidoRepository,
            RestauranteService restauranteService,
            PagoService pagoService,
            NotificacionesService notificacionesService,
            CurrentUserService currentUserService,
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            CarritoService carritoService,
            GeoapifyService geoapifyService,
            IngredientePedidoService ingredientePedidoService) {
        this.pedidoRepository = pedidoRepository;
        this.restauranteService = restauranteService;
        this.pagoService = pagoService;
        this.notificacionesService = notificacionesService;
        this.currentUserService = currentUserService;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.carritoService = carritoService;
        this.geoapifyService = geoapifyService;
        this.ingredientePedidoService = ingredientePedidoService;
    }

    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @Transactional
    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Optional<Pedido> obtener(Integer id) {
        return pedidoRepository.findById(id);
    }

    public Pedido obtenerOFallar(Integer id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    public List<Pedido> listarPorCliente(int idCliente) {
        return pedidoRepository.findByClienteIdUsuario(idCliente);
    }

    public List<Pedido> listarPorRestaurante(int idRestaurante) {
        return pedidoRepository.findByRestauranteIdUsuario(idRestaurante);
    }

    public List<Pedido> listarPorEstado(EnumEstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    @Transactional
    public Pedido cambiarEstado(Integer id, EnumEstadoPedido nuevoEstado) {
        Pedido p = obtenerOFallar(id);
        p.setEstado(nuevoEstado);
        return pedidoRepository.save(p);
    }

    public Pedido cancelar(Integer id, String razon) {
        Pedido p = obtenerOFallar(id);
        p.setEstado(EnumEstadoPedido.Cancelado);
        p.setRazonCancelacion(razon != null ? razon : "Cancelado por el cliente");
        return pedidoRepository.save(p);
    }

    @Transactional
    public Pedido recalcularTotal(Integer id) {
        Pedido p = obtenerOFallar(id);
        p.setTotal(p.calcularTotal());
        return pedidoRepository.save(p);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!pedidoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado");
        }
        pedidoRepository.deleteById(id);
    }

    public List<Pedido> listarPedidos(Integer idRestaurante, EnumEstadoPedido estado) {
        if (estado != null) {
            return pedidoRepository.findByRestauranteIdUsuarioAndEstado(idRestaurante, estado);
        }
        return pedidoRepository.findByRestauranteIdUsuario(idRestaurante);
    }

    @Transactional
    public int cancelarPedidosExpirados() {
        List<Pedido> expirados = pedidoRepository
                .findByFechaExpiracionNotNullAndFechaExpiracionBefore(LocalDateTime.now());
        for (Pedido pedido : expirados) {
            pedido.setEstado(EnumEstadoPedido.Cancelado);
            pedido.setRazonCancelacion("Expirado: plazo de pago de 24 horas vencido");
            pedido.setFechaExpiracion(null);
        }
        pedidoRepository.saveAll(expirados);
        return expirados.size();
    }

    // Valida el restaurante, crea el pedido a partir del carrito del cliente
    // autenticado, genera la preferencia de pago en MercadoPago y devuelve la
    // preferencia (con la URL de checkout) para que el front redirija a la
    // pasarela.
    @Transactional
    public DTOPreferenciaMP confirmarPedido(DTODireccion direccionDTO) {
        DTOCarrito carritoDTO = carritoService.obtenerCarrito();

        DTORestaurante restauranteDTO = restauranteService
                .obtenerRestaurante(String.valueOf(carritoDTO.getIdRestaurante()));
        if (restauranteDTO == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Restaurante no encontrado con id: " + carritoDTO.getIdRestaurante());
        }

        Pedido pedido = crearPedido(carritoDTO, direccionDTO, restauranteDTO);

        DTOPedido pedidoDTO = new DTOPedido(
                pedido.getIdPedido(),
                (double) pedido.getTotal(),
                carritoDTO.getProductos());
        return pagoService.crearPreferencia(pedidoDTO);
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
                    ? 1
                    : linea.getCantidad();
            float precioSuma = producto.getPrecio() * cantidad;
            ProductoPedido pp = new ProductoPedido(producto, cantidad, precioSuma, linea.getObservaciones());
            pp.setIngredientesAQuitar(
                    ingredientePedidoService.resolverIngredientesAQuitar(
                            linea.getIngredientesAQuitar(), producto));
            pedido.addProductoPedido(pp);
        }

        pedido.setTotal(pedido.calcularTotal());
        return pedidoRepository.save(pedido);
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
        return new DTODireccion(d.getTag(), d.getCalle(), d.getNumero(), d.getApartamento(),
                d.getEsquina(), d.getLatitud(), d.getLongitud());
    }

    // Lista los pedidos del restaurante autenticado, filtrando por estado (default
    // Pagado) y opcionalmente por producto incluido en el pedido.
    public List<DTOPedido> listarPedidosConfirmados(Integer idProducto, EnumEstadoPedido estado) {
        Integer idRestaurante;
        try {
            idRestaurante = Integer.valueOf(currentUserService.getCurrentId());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Usuario autenticado inválido");
        }

        EnumEstadoPedido estadoConsulta = (estado != null) ? estado : EnumEstadoPedido.Pagado;
        List<Pedido> pedidos = listarPedidos(idRestaurante, estadoConsulta);

        var stream = pedidos.stream().map(DTOPedido::desde);
        if (idProducto != null) {
            stream = stream.filter(p -> p.getProductos().stream()
                    .anyMatch(prod -> idProducto.equals(prod.getIdProducto())));
        }
        return stream.collect(Collectors.toList());
    }

    /**
     * Historial de compras del cliente autenticado (CU cliente).
     * No reutiliza listarPedidosConfirmados (ese CU es del restaurante).
     */
    @Transactional(readOnly = true)
    public List<DTOPedido> listarMisPedidosCliente() {
        if (!"Cliente".equals(currentUserService.getCurrentRol())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo clientes pueden consultar su historial de pedidos");
        }

        Cliente cliente = resolverClienteAutenticado();

        return pedidoRepository.findHistorialByClienteIdUsuario(cliente.getIdUsuario()).stream()
                .filter(p -> p.getEstado() != EnumEstadoPedido.Solicitado
                        && p.getEstado() != EnumEstadoPedido.PagoRechazado)
                .map(DTOPedido::desdeHistorial)
                .collect(Collectors.toList());
    }

    /**
     * Compras activas del cliente (Pagado, En preparacion, En Camino)
     * Los filtros se definen dentro y retorna un pedido con sus correspondientes productos pedidos, para que el usuario pueda visualizar lo que pidio
     */
    @Transactional(readOnly = true)
    public List<DTOPedido> listarMisPedidosActualesCliente() {
        if (!"Cliente".equals(currentUserService.getCurrentRol())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo clientes pueden consultar su historial de pedidos");
        }

        Cliente cliente = resolverClienteAutenticado();

        // Lista de estados que definen un pedido en espera para el cliente
        List<EnumEstadoPedido> estadosActuales = List.of(
                EnumEstadoPedido.Pagado,
                EnumEstadoPedido.EnPreparacion,
                EnumEstadoPedido.EnCamino);

                // Envio esa lista al repositorio para que cree el filtro en db y no tenga que traer datos inecesarios
        return pedidoRepository.findPedidosActualesByCliente(cliente.getIdUsuario(), estadosActuales)
                .stream()
                .map(DTOPedido::desde) // Se crea el DTO con los ProductosPedidos
                .collect(Collectors.toList());
    }

    private Cliente resolverClienteAutenticado() {
        String uid = currentUserService.getCurrentUid();
        if (uid != null && !uid.isBlank()) {
            Optional<Cliente> porUid = usuarioRepository.findClienteByUidCliente(uid);
            if (porUid.isPresent()) {
                return porUid.get();
            }
        }
        Integer idUsuario = currentUserService.getCurrentIdUsuario();
        if (idUsuario != null) {
            return usuarioRepository.findClienteById(idUsuario)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
    }

    @Transactional
    public DTOPedido actualizarEstadoPedido(DTOPedido pedidoDTO, EnumEstadoPedido estado) {
        if (pedidoDTO == null || pedidoDTO.getIdPedido() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DTOPedido inválido");
        }

        Pedido pedido = obtenerOFallar(pedidoDTO.getIdPedido());

        EnumEstadoPedido nuevoEstado = estado;
        EnumEstadoPedido estadoActual = pedido.getEstado();

        if (!verificarSaltoEstado(estadoActual, nuevoEstado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Salto de estado incorrecto: No se puede pasar de " + estadoActual + " a " + nuevoEstado);
        }

        pedido.setEstado(estado);
        if (nuevoEstado == EnumEstadoPedido.Entregado) {
            pedido.setHorarioEntrega(LocalDateTime.now());
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        DTOPedido dtoActualizado = DTOPedido.desde(pedidoGuardado);
        try {
            if (nuevoEstado == EnumEstadoPedido.EnCamino) {
                notificacionesService.notificarPedidoEnCamino(dtoActualizado, pedidoGuardado.getTiempoPreparacion());
                notificacionesService.notificarPushEnCamino(dtoActualizado, pedidoGuardado.getTiempoPreparacion());
            } else if (nuevoEstado == EnumEstadoPedido.Entregado) {
                notificacionesService.notificarPedidoEntregado(dtoActualizado);
                notificacionesService.notificarPushEntregado(dtoActualizado);
            }
        } catch (Exception e) {
            System.err.println("Error enviando notificación: " + e.getMessage());
        }

        return dtoActualizado;
    }

    private boolean verificarSaltoEstado(EnumEstadoPedido actual, EnumEstadoPedido nuevo) {
        if (actual == EnumEstadoPedido.EnPreparacion && nuevo == EnumEstadoPedido.EnCamino)
            return true;
        if (actual == EnumEstadoPedido.EnCamino && nuevo == EnumEstadoPedido.Entregado)
            return true;
        return false;
    }

    @Transactional
    public DTOPedido confirmarPedidoPendiente(Integer pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
        if (pedido.getEstado() == EnumEstadoPedido.Cancelado) {
            throw new PedidoCanceladoException("El pedido ha sido cancelado");
        }
        if (pedido.getEstado() != EnumEstadoPedido.Pagado) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se pueden confirmar pedidos en estado 'Pagado'");
        }

        pedido.setEstado(EnumEstadoPedido.EnPreparacion);

        Integer tiempoEstimado = calcularTiempoEstimadoEntrega(pedido);
        pedido.setTiempoPreparacion(tiempoEstimado);
        pedidoRepository.save(pedido);

        DTOPedido pedidoDTO = DTOPedido.desde(pedido);
        notificacionesService.notificarConfirmacionPedido(pedidoDTO, tiempoEstimado);
        try {
            notificacionesService.notificarPushEnPreparacion(pedidoDTO, tiempoEstimado);
        } catch (Exception e) {
            System.err.println("Error enviando push EnPreparacion: " + e.getMessage());
        }
        return pedidoDTO;
    }

    // Recorre los productos del pedido buscando el mayor tiempo de preparación
    // entre los que sean Plato (otros productos no tienen tiempo asociado).
    private Integer calcularTiempoPreparacion(Pedido pedido) {
        if (pedido.getProductos() == null) {
            return 0;
        }
        return pedido.getProductos().stream()
                .map(pp -> {
                    if (pp.getProducto() instanceof Plato plato) {
                        return plato.getTiempoPreparacionMinutos();
                    }
                    return 0;
                })
                .max(Integer::compareTo)
                .orElse(0);
    }

    private Integer obtenerTiempoViaje(Pedido pedido) {
        DTODireccion origen = pedido.getRestaurante().getDireccion();
        DTODireccion destino = pedido.getDireccionEntrega();
        if (origen == null || destino == null) {
            return 0;
        }
        return geoapifyService.calcularTiempoLlegadaMinutos(origen, destino);
    }

    private Integer calcularTiempoEstimadoEntrega(Pedido pedido) {
        Integer tiempoPreparacion = calcularTiempoPreparacion(pedido);
        Integer tiempoViaje = obtenerTiempoViaje(pedido);
        return tiempoPreparacion + tiempoViaje;
    }

    @Transactional
    public DTOPedido reembolsarPedido(DTOPedido pedidoDTO) {
        if (pedidoDTO == null || pedidoDTO.getIdPedido() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "DTOPedido inválido para reembolsar");
        }

        Pedido pedido = obtenerOFallar(pedidoDTO.getIdPedido());

        Pago pago = pedido.getPago();
        if (pago == null || pago.getIdTransaccion() == null || pago.getIdTransaccion().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El pedido " + pedido.getIdPedido() + " no tiene un pago asociado para reembolsar");
        }

        if (pedido.getEstado() == EnumEstadoPedido.Reembolsado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El pedido " + pedido.getIdPedido() + " ya estaba reembolsado");
        }

        String idempotencyKey = "reembolso-pedido-" + pedido.getIdPedido();
        pagoService.reembolsar(pago.getIdTransaccion(), idempotencyKey);

        pedido.setEstado(EnumEstadoPedido.Reembolsado);
        pedidoRepository.save(pedido);

        return DTOPedido.desde(pedido);
    }
}
