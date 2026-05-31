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
import java.util.stream.Collectors;

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

    // Lista los pedidos confirmados (pagados) de un cliente, con opción de filtrar por producto incluido en el pedido.
    public List<DTOPedido> listarPedidosConfirmados(Integer idProducto, EnumEstadoPedido estado) {
        Integer idRestaurante;
        try {
            idRestaurante = Integer.valueOf(currentUserService.getCurrentId());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Usuario autenticado inválido");
        }

        EnumEstadoPedido estadoConsulta = (estado != null) ? estado : EnumEstadoPedido.Pagado;
        List<Pedido> pedidos = ordenesService.listarPedidos(idRestaurante, estadoConsulta);

        var stream = pedidos.stream().map(DTOPedido::desde);
        if (idProducto != null) {
            stream = stream.filter(p -> p.getProductos().stream()
                    .anyMatch(prod -> idProducto.equals(prod.getIdProducto())));
        }
        return stream.collect(Collectors.toList());
    }

    public DTOPedido actualizarEstadoPedido(DTOPedido pedidoDTO, EnumEstadoPedido estado) {
        // TODO: implementar
        return null;
    }

    public Integer calcularTiempoPreparacion(List<DTOProductoPedido> productos) {
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
        Integer idRestaurante = Integer.valueOf(restauranteId);
        //buscarPedidos(restauranteId, "Pendiente")
        List<Pedido> pedidos = pedidoRepository.findByRestauranteIdUsuarioAndEstado(idRestaurante, EnumEstadoPedido.Pendiente);        
        return pedidos.stream().map(DTOPedido::desde).collect(Collectors.toList());
    }
    
    @Transactional
    public DTOPedido confirmarPedidoPendiente(Integer pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
        if (pedido.getEstado() == EnumEstadoPedido.Cancelado) {
            throw new PedidoCanceladoException("El pedido ha sido cancelado");
        }
        pedido.setEstado(EnumEstadoPedido.Confirmado);  //creo que no existe ese enum hay que crearlo si se puede o no se como se maneja
        pedido = pedidoRepository.save(pedido);
        Integer tiempoPreparacion = calcularTiempoPreparacion(pedido);
        Integer tiempoViaje = obtenerTiempoViaje(pedido);
        Integer tiempoEstimado = tiempoViaje + tiempoPreparacion;
        DTOPedido pedidoDTO = DTOPedido.desde(pedido);
        notificacionesService.notificarConfirmacionPedido(pedidoDTO, tiempoEstimado);
        return pedidoDTO;   //tiene que ser 200 OK
    }
    
    // Lógica interna: Recorre productos buscando el tiempo mayor
    private Integer calcularTiempoPreparacion(Pedido pedido) {
        if (pedido.getProductos() == null) return 0;
        
        return pedido.getProductos().stream()
                .map(pp -> {
                    // Extrae el tiempo si la entidad es un Plato
                    if (pp.getProducto() != null && pp.getProducto() instanceof Plato plato) {
                        return plato.getTiempoPreparacionMinutos() != null ? plato.getTiempoPreparacionMinutos() : 0;
                    }
                    return 0;
                })
                .max(Integer::compareTo)
                .orElse(0);
    }
    
    private Integer obtenerTiempoViaje(Pedido pedido) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            DTODireccion direccionCliente = DTODireccion.desde(pedido.getDireccionEntrega());
            
            // Construcción del Endpoint Geoapify según el diagrama
            // mode=drive | waypoints=lat,lon|lat,lon
            String urlGeoapify = String.format(
                "https://api.geoapify.com/v1/routing?waypoints=%s,%s|%s,%s&mode=drive&apiKey=TU_API_KEY",
                direccionCliente.getLatitud(), direccionCliente.getLongitud(),
                pedido.getRestaurante().getLatitud(), pedido.getRestaurante().getLongitud()
            );

            // ALT 1.1: Simulamos la petición (Descomentar para entorno de producción real)
            // ResponseEntity<Map> response = restTemplate.getForEntity(urlGeoapify, Map.class);
            // Integer duracion = extraerDuracionDesdeRespuesta(response);
            // return duracion;

            // Para evitar que la app explote si no tienes Key o red, forzamos un fallo simulado
            throw new RuntimeException("Forzando fallback de Geoapify");

        } catch (Exception e) {
            // ALT 1.2 - API no responde -> Retorna valor por defecto
            System.err.println("Geoapify Routing API no responde o falló. Aplicando fallback de 30 min.");
            return 30; 
        }
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
