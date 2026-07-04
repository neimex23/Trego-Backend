package com.backend.trego.service;

import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Comentario;
import com.backend.trego.entity.Ingrediente;
import com.backend.trego.entity.Oferta;
import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.DTOs.DTOComentario;
import com.backend.trego.entity.DTOs.DTOCrearComentarioRequest;
import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOEstadisticas;
import com.backend.trego.entity.DTOs.DTOModificarOfertaRequest;
import com.backend.trego.entity.DTOs.DTOIngrediente;
import com.backend.trego.entity.DTOs.DTOOferta;
import com.backend.trego.entity.DTOs.DTOProducto;
import com.backend.trego.entity.DTOs.DTOProductoSimplificado;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.exception.RestauranteCerradoException;
import com.backend.trego.exception.SinProductoException;
import com.backend.trego.repository.ComentarioRepository;
import com.backend.trego.repository.PedidoRepository;
import com.backend.trego.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// Gestión de restaurantes: alta, apertura/cierre, búsqueda y firma de imágenes.
@Service
public class RestauranteService {

    private final UsuarioRepository restauranteRepository;
    private final CurrentUserService currentUserService;
    private final ProductosService productosService;
    private final PedidoService pedidosService;
    private final NotificacionesService notificacionesService;
    private final GeoapifyService geoapifyService;
    private final PedidoRepository pedidoRepository;
    private final ComentarioRepository comentarioRepository;

    // Zona horaria del negocio. El cierre automático se calcula y reconcilia contra
    // esta zona para no depender de la zona por defecto de la JVM (en EC2 suele ser UTC).
    @Value("${app.timezone:America/Montevideo}")
    private String zonaHoraria;

    public RestauranteService(UsuarioRepository restauranteRepository, CurrentUserService currentUserService,
            @Lazy ProductosService productosService, NotificacionesService notificacionesService,
            GeoapifyService geoapifyService, @Lazy PedidoService pedidosService,
            PedidoRepository pedidoRepository, ComentarioRepository comentarioRepository) {
        this.restauranteRepository = restauranteRepository;
        this.currentUserService = currentUserService;
        this.productosService = productosService;
        this.notificacionesService = notificacionesService;
        this.geoapifyService = geoapifyService;
        this.pedidosService = pedidosService;
        this.pedidoRepository = pedidoRepository;
        this.comentarioRepository = comentarioRepository;
    }

    public void abrirLocal(LocalTime horaApertura, LocalTime horaCierre) {
        Integer restauranteId = currentUserService.getCurrentId();
        Restaurante restaurante = restauranteRepository.findRestauranteById(restauranteId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        // Precondición: el local debe estar cerrado
        if (restaurante.getAbierto()) {
            throw new RestauranteCerradoException("El local ya se encuentra abierto");
        }

        if (restaurante.getProductos().isEmpty() || restaurante.getProductos().stream().allMatch(p -> !p.getDisponible())){
            throw new RestauranteCerradoException("El local no tiene productos disponibles, por lo cual no puede abrir");
        }

        restaurante.setHorario(horaApertura, horaCierre);
        restauranteRepository.save(restaurante);

        List<DTOProducto> productos;
        try {
            productos = productosService.listarProductos(String.valueOf(restauranteId), false);
        } catch (ResponseStatusException e) {
            productos = Collections.emptyList();
        }

        if (productos == null || productos.isEmpty()) {
            throw new RestauranteCerradoException("Debe tener algun producto para ofrecer");
        }
        restaurante.setAbierto(true);
        restaurante.setCierreProgramado(calcularCierreProgramado(horaCierre));
        restauranteRepository.save(restaurante);
    }

    public void cerrarLocal() {
        Integer restauranteId = currentUserService.getCurrentId();
        Restaurante restaurante = restauranteRepository.findRestauranteById(restauranteId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        
        // Precondición: el local debe estar abierto
        if (!restaurante.getAbierto()) {
            throw new RestauranteCerradoException("El local ya se encontraba cerrado");
        }

        // Obtener la hora actual para dejar registro en la bd
        LocalTime horaActual = LocalTime.now(ZoneId.of(zonaHoraria));
        restaurante.setAbierto(false);
        restaurante.setHorario(restaurante.getApertura(), horaActual);
        restaurante.setCierreProgramado(null);
        restauranteRepository.save(restaurante);
    }

    // Método para actualizar la hora del cierre automatico
    public void actualizarHoraCierre(LocalTime horaCierre) {
        Integer restauranteId = currentUserService.getCurrentId();
        Restaurante restaurante = restauranteRepository.findRestauranteById(restauranteId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        restaurante.setAbierto(true);
        restaurante.setHorario(restaurante.getApertura(), horaCierre);
        restaurante.setCierreProgramado(calcularCierreProgramado(horaCierre));
        restauranteRepository.save(restaurante);
    }

    // Permite al restaurante fijar manualmente el instante exacto de cierre.
    // Arranca igual a la hora de cierre (lo setea abrirLocal), pero acá el usuario
    // puede moverlo a otra hora/fecha. Ese valor es el que dispara el cierre real.
    public void actualizarCierreProgramado(LocalDateTime cierre) {
        Integer restauranteId = currentUserService.getCurrentId();
        Restaurante restaurante = restauranteRepository.findRestauranteById(restauranteId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        if (!restaurante.getAbierto()) {
            throw new RestauranteCerradoException("El local debe estar abierto para programar el cierre");
        }
        if (cierre == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el cierre programado");
        }
        LocalDateTime ahora = LocalDateTime.now(ZoneId.of(zonaHoraria));
        if (!cierre.isAfter(ahora)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El cierre programado debe ser una fecha/hora futura");
        }
        restaurante.setCierreProgramado(cierre);
        restauranteRepository.save(restaurante);
    }

    private LocalDateTime calcularCierreProgramado(LocalTime horaCierre) {
        ZoneId zona = ZoneId.of(zonaHoraria);
        LocalDateTime ahora = LocalDateTime.now(zona);
        LocalDateTime cierre = LocalDateTime.of(ahora.toLocalDate(), horaCierre);
        if (!cierre.isAfter(ahora)) {
            cierre = cierre.plusDays(1);
        }
        return cierre;
    }

    @Transactional
    public int cerrarLocalesVencidos() {
        LocalDateTime ahora = LocalDateTime.now(ZoneId.of(zonaHoraria));
        List<Restaurante> vencidos = restauranteRepository.findRestaurantesParaCerrar(ahora);
        for (Restaurante restaurante : vencidos) {
            restaurante.setAbierto(false);
            restaurante.setCierreProgramado(null);
            System.out.println("[Cierre] Local " + restaurante.getIdUsuario()
                    + " cerrado automáticamente por reconciliación.");
        }
        if (!vencidos.isEmpty()) {
            restauranteRepository.saveAll(vencidos);
        }
        return vencidos.size();
    }


    public List<DTORestaurante> listarRestaurantesZona(DTODireccion direccion) {

        List<DTORestaurante> restaurantesHabilitados = listarRestaurantes();
        if (restaurantesHabilitados.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No hay restaurantes habilitados");
        }

        List<DTORestaurante> restaurantesFiltro = restaurantesHabilitados.stream()
                .filter(r -> cubreZona(direccion, r.getDireccion(), r.getRadioEntrega()))
                .filter(r -> r.getAbierto())
                .collect(Collectors.toList());

        if (restaurantesFiltro.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No hay restaurantes en la zona");
        }

        return restaurantesFiltro;
    }

    // Lista los restaurantes registrados y habilitados, en su forma pública (sin
    // password ni menú). Es el catálogo que ve el cliente.
    public List<DTORestaurante> listarRestaurantes() {
        return restauranteRepository.findRestaurantesHabilitados().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<DTORestaurante> listarRestaurantesHabilitadosNoCerrados() {
        return restauranteRepository.findRestaurantesHabilitados().stream()
                .filter(restaurante -> restaurante.estaAbierto())
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DTORestaurante obtenerRestaurante(String restauranteId) {
        Restaurante restaurante = buscarRestaurante(restauranteId);
        return toDTO(restaurante);
    }

    public List<DTOIngrediente> obtenerIngredientesDisponibles() {
        Integer id = currentUserService.getCurrentId();
        Restaurante restaurante = restauranteRepository.findRestauranteById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Restaurante autenticado no encontrado con id: " + id));
        return restaurante.getIngredientesDisponibles().stream()
                .map(ing -> new DTOIngrediente(ing.getIdIngrediente(), ing.getNombre(), id))
                .collect(Collectors.toList());
    }

    @Transactional
    public DTOIngrediente crearIngrediente(String nombre) {
        Integer actualID = currentUserService.getCurrentId();
        Restaurante restaurante = restauranteRepository.findRestauranteById(actualID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Restaurante autenticado no encontrado con id: " + actualID));
        if (restaurante.existeIngrediente(nombre))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ingrediente ya existe");

        Ingrediente ingrediente = new Ingrediente(nombre);
        restaurante.addIngredienteDisponible(ingrediente);
        restauranteRepository.flush();
        return new DTOIngrediente(ingrediente.getIdIngrediente(), ingrediente.getNombre(), actualID);
    }

    // Devuelve el restaurante autenticado actualmente (lee el id del token).
    public DTORestaurante obtenerRestauranteActual() {
        Integer id = currentUserService.getCurrentId();
        Restaurante restaurante = restauranteRepository.findRestauranteById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Restaurante autenticado no encontrado con id: " + id));
        return toDTO(restaurante);
    }

    // Carga la entidad o devuelve 404 si no existe.
    public Restaurante buscarRestaurante(String restauranteId) {
        Integer id = parseId(restauranteId);
        return restauranteRepository.findRestauranteById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Restaurante no encontrado con id: " + restauranteId));
    }

    private Integer parseId(String restauranteId) {
        try {
            return Integer.valueOf(restauranteId);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Id de restaurante inválido: " + restauranteId);
        }
    }

    private DTORestaurante toDTO(Restaurante restaurante) {
        DTORestaurante dto = new DTORestaurante(
                restaurante.getIdUsuario(),
                restaurante.getNombre(),
                restaurante.getEmail(),
                restaurante.getTelefono(),
                restaurante.getFotoPortada(),
                restaurante.getFotoPerfil(),
                restaurante.getDireccion(),
                restaurante.getDescripcion(),
                restaurante.getCategoria(),
                restaurante.getCalificacionProm(),
                restaurante.getRadioEntrega(),
                restaurante.isHabilitado(),
                restaurante.getAbierto(),
                restaurante.getApertura(),
                restaurante.getCierre(),
                restaurante.getCierreProgramado(),
                restaurante.isCuentaHabilitada());
        return dto;
    }

    private DTORestaurante toDTOHabilitar(Restaurante restaurante) {
        return new DTORestaurante(
                restaurante.getIdUsuario(),
                restaurante.getNombre(),
                restaurante.getEmail(),
                null, // password: nunca se expone al frontend
                restaurante.getRut(),
                restaurante.getTelefono(),
                restaurante.getFotoPortada(),
                restaurante.getFotoPerfil(),
                restaurante.getDireccion(),
                restaurante.getDescripcion(),
                null,
                null,
                null,
                restaurante.isHabilitado(),
                null, // abierto
                null, // horaApertura
                null, // horaCierre
                null, // cierreProgramado
                null, // productos
                null, // ingredientesDisponibles
                restaurante.isCuentaHabilitada());
    }

    public List<DTORestaurante> buscarRestaurantePorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return listarRestaurantes();
        }
        return restauranteRepository.findRestaurantesHabilitadosPorNombre(nombre.trim()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Devuelve el restaurante junto con su menú de productos, aplicando filtro por
    // categoría y orden por precio si se indican. Lanza SinProductoException si,
    // tras filtrar, el restaurante no tiene productos para mostrar.
    public DTORestaurante verRestaurante(Integer restauranteId, String categoria, String orden) {
        String id = String.valueOf(restauranteId);
        Restaurante restaurante = buscarRestaurante(id);

        List<DTOProducto> productos;
        try {
            productos = productosService.listarSoloProductosHabilitados(id, false);
        } catch (ResponseStatusException e) {
            // listarProductos devuelve 404 si no hay productos; en verMenu eso es menú vacío, no local inexistente.
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                productos = Collections.emptyList();
            } else {
                throw e;
            }
        }

        if (categoria != null && !categoria.isBlank()) {
            productos = aplicarFiltro(productos, categoria);
        }

        if (orden != null && !orden.isBlank()) {
            productos = aplicarOrden(productos, orden);
        }

        boolean filtroCategoriaActivo = categoria != null && !categoria.isBlank();
        if (productos.isEmpty() && !filtroCategoriaActivo) {
            throw new SinProductoException("Restaurante sin Productos");
        }

        return toDTOConProductos(restaurante, productos);
    }

    private List<DTOProducto> aplicarFiltro(List<DTOProducto> productos, String categoria) {
        return productos.stream()
                .filter(p -> p.getCategoria() != null
                        && p.getCategoria().name().equalsIgnoreCase(categoria))
                .collect(Collectors.toList());
    }

    private List<DTOProducto> aplicarOrden(List<DTOProducto> productos, String orden) {
        if (orden.equalsIgnoreCase("precio_asc")) {
            productos.sort(Comparator.comparing((DTOProducto producto) -> producto.getPrecio()));
        } else if (orden.equalsIgnoreCase("precio_desc")) {
            productos.sort(Comparator.comparing((DTOProducto producto) -> producto.getPrecio()).reversed());
        }
        return productos;
    }

    // Arma el DTO con los datos públicos del restaurante más la lista de productos.
    // No se envía el password al frontend.
    private DTORestaurante toDTOConProductos(Restaurante restaurante, List<DTOProducto> productos) {
        DTORestaurante dto = new DTORestaurante(
                restaurante.getIdUsuario(),
                restaurante.getNombre(),
                restaurante.getEmail(),
                null, // password: nunca se expone al frontend
                restaurante.getRut(),
                restaurante.getTelefono(),
                restaurante.getFotoPortada(),
                restaurante.getFotoPerfil(),
                restaurante.getDireccion(),
                restaurante.getDescripcion(),
                restaurante.getCategoria(),
                restaurante.getCalificacionProm(),
                restaurante.getRadioEntrega(),
                restaurante.isHabilitado(),
                restaurante.getAbierto(),
                restaurante.getApertura(),
                restaurante.getCierre(),
                restaurante.getCierreProgramado(),
                productos,
                restaurante.getIngredientesDisponibles().stream()
                        .map(i -> new DTOIngrediente(i.getIdIngrediente(), i.getNombre(), restaurante.getIdUsuario()))
                        .collect(Collectors.toList()),
                restaurante.isCuentaHabilitada());
        return dto;
    }

    public List<DTORestaurante> listarRestaurantesNoHabilitados() {
        if (!currentUserService.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado: se requiere iniciar sesión");
        }

        if (!currentUserService.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Acceso denegado: se requieren privilegios de administrador");
        }

        List<Restaurante> restaurantesNoHabilitados = restauranteRepository.findRestaurantesNoHabilitados();
        return restaurantesNoHabilitados.stream()
                .map(this::toDTOHabilitar)
                .collect(Collectors.toList());
    }

    @Transactional
    public DTORestaurante altaRestaurante(DTORestaurante dto) {
        Restaurante restaurante = buscarRestaurante(String.valueOf(currentUserService.getCurrentId()));
        restaurante.setDireccion(dto.getDireccion());
        restauranteRepository.save(restaurante);
        return actualizarRestaurante(dto);
    }
        
    // Actualiza los datos del restaurante aplicando sólo los campos no nulos del
    // DTO. No se permite modificar id ni habilitado.
    // Direcciones, Horarios y Contraseña se actualizan por endpoints específicos.
    @Transactional
    public DTORestaurante actualizarRestaurante(DTORestaurante dto) {
        Restaurante restaurante = buscarRestaurante(String.valueOf(currentUserService.getCurrentId()));

        if (dto.getNombre() != null && !dto.getNombre().isBlank() && !dto.getNombre().equals(restaurante.getNombre())) {
            restaurante.setNombre(dto.getNombre());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(restaurante.getEmail())) {
            restaurante.setEmail(dto.getEmail());
        }   
        if (dto.getRut() != null && !dto.getRut().isBlank() && !dto.getRut().equals(restaurante.getRut())) {
            restaurante.setRut(dto.getRut());
        }
        if (dto.getTelefono() != null && !dto.getTelefono().isBlank() && !dto.getTelefono().equals(restaurante.getTelefono())) {
            restaurante.setTelefono(dto.getTelefono());
        }
        if (dto.getFotoPortada() != null && !dto.getFotoPortada().isBlank() && !dto.getFotoPortada().equals(restaurante.getFotoPortada())) {
            restaurante.setFotoPortada(dto.getFotoPortada());
        }
        if (dto.getFotoPerfil() != null && !dto.getFotoPerfil().isBlank() && !dto.getFotoPerfil().equals(restaurante.getFotoPerfil())) {
            restaurante.setFotoPerfil(dto.getFotoPerfil());
        }
        if (dto.getDescripcion() != null && !dto.getDescripcion().isBlank() && !dto.getDescripcion().equals(restaurante.getDescripcion())) {
            restaurante.setDescripcion(dto.getDescripcion());
        }
        if (dto.getCategoria() != null && !dto.getCategoria().equals(restaurante.getCategoria())) {
            restaurante.setCategoria(dto.getCategoria());
        }
        if (dto.getCalificacionProm() != null && !dto.getCalificacionProm().equals(restaurante.getCalificacionProm())) {
            restaurante.setCalificacionProm(dto.getCalificacionProm());
        }
        if (dto.getRadioEntrega() != null && !dto.getRadioEntrega().equals(restaurante.getRadioEntrega())) {
            restaurante.setRadioEntrega(dto.getRadioEntrega());
        }

        Restaurante actualizado = restauranteRepository.save(restaurante);
        return toDTO(actualizado);
    }

    @Transactional
    public void habilitarRestaurante(Integer restauranteId) {

        if (!currentUserService.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "No autenticado: se requiere iniciar sesión");
        }

        if (!currentUserService.isAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acceso denegado: se requieren privilegios de administrador");
        }

        Restaurante restaurante = buscarRestaurante(String.valueOf(restauranteId));

        if (restaurante.isHabilitado()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El restaurante ya se encuentra habilitado");
        }

        restaurante.setHabilitado(true);
        restauranteRepository.save(restaurante);

        notificacionesService.notificarRestauranteHabilitado(restaurante);
    }

    public void noHabilitarRestaurante(Integer restauranteId, String motivo) {
        if (!currentUserService.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "No autenticado: se requiere iniciar sesión");
        }

        if (!currentUserService.isAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acceso denegado: se requieren privilegios de administrador");
        }

        Restaurante restaurante = buscarRestaurante(String.valueOf(restauranteId));
        notificacionesService.notificarRestauranteNoHabilitado(restaurante, motivo);
    }

    @Transactional
    public DTOComentario agregarComentario(DTOCrearComentarioRequest request) {
        if (request.getIdRestaurante() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id del restaurante es obligatorio");
        }

        Integer calificacion = request.getCalificacion();
        if (calificacion == null || calificacion < 1 || calificacion > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La calificación debe ser un valor entre 1 y 5 estrellas");
        }

        String texto = request.getTexto() != null ? request.getTexto().trim() : "";
        if (texto.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El comentario no puede estar vacío");
        }

        Restaurante restaurante = buscarRestaurante(String.valueOf(request.getIdRestaurante()));
        Cliente cliente = resolverClienteAutenticado();

        long cantidadPedidos = pedidoRepository.countPedidosPorClienteYRestaurante(
                cliente.getIdUsuario(), restaurante.getIdUsuario());
        if (cantidadPedidos == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo podés comentar un restaurante en el que hayas realizado un pedido");
        }

        if (comentarioRepository.existsByClienteIdUsuarioAndRestauranteIdUsuario(
                cliente.getIdUsuario(), restaurante.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya dejaste un comentario en este restaurante");
        }

        Comentario comentario = new Comentario(texto, calificacion, cliente, restaurante);
        comentarioRepository.save(comentario);
        actualizarCalificacionPromedioRestaurante(restaurante.getIdUsuario());

        return Comentario.toDTOComentario(comentario, restaurante.getIdUsuario());
    }

    @Transactional(readOnly = true)
    public boolean clienteYaComentoEnRestaurante(Integer idRestaurante) {
        if (idRestaurante == null || currentUserService.getCurrentUserOrNull() == null) {
            return false;
        }
        if (!"Cliente".equalsIgnoreCase(currentUserService.getCurrentRol())) {
            return false;
        }
        Cliente cliente = resolverClienteAutenticado();
        return comentarioRepository.existsByClienteIdUsuarioAndRestauranteIdUsuario(
                cliente.getIdUsuario(), idRestaurante);
    }

    @Transactional(readOnly = true)
    public List<DTOComentario> listarComentarios(Integer idRestaurante) {
        final Integer restauranteId;

        if (idRestaurante == null) {
            if (!"Restaurante".equals(currentUserService.getCurrentRol())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Debe indicar el id del restaurante para listar comentarios");
            }
            restauranteId = currentUserService.getCurrentId();
        } else {
            buscarRestaurante(String.valueOf(idRestaurante));
            restauranteId = idRestaurante;
        }

        Set<Integer> clientesVistos = new HashSet<>();
        return comentarioRepository.findByRestauranteWithClienteOrderByFechaCreacionDesc(restauranteId)
            .stream()
            .filter(c -> clientesVistos.add(c.getCliente().getIdUsuario()))
            .map(c -> Comentario.toDTOComentario(c, restauranteId))
            .collect(Collectors.toList());
    }

    private void actualizarCalificacionPromedioRestaurante(Integer idRestaurante) {
        Double promedio = comentarioRepository.promedioCalificacionPorRestaurante(idRestaurante);
        restauranteRepository.updateCalificacionProm(idRestaurante, promedio != null ? promedio.floatValue() : 0f);
    }

    private Cliente resolverClienteAutenticado() {
        String uid = currentUserService.getCurrentUid();
        if (uid != null && !uid.isBlank()) {
            Optional<Cliente> porUid = restauranteRepository.findClienteByUidCliente(uid);
            if (porUid.isPresent()) {
                return porUid.get();
            }
        }
        Integer idUsuario = currentUserService.getCurrentIdUsuario();
        if (idUsuario != null) {
            return restauranteRepository.findClienteById(idUsuario)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Cliente autenticado no encontrado"));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente autenticado no encontrado");
    }

    public Integer obtenerCalificacion(Integer restauranteId, Boolean esRestaurante) {
        Integer id = restauranteId;
        if (esRestaurante) {
            id = currentUserService.getCurrentId();
        }
        Restaurante restaurante = buscarRestaurante(id.toString());
        return Math.round(restaurante.getCalificacionProm());
    }

    public DTOEstadisticas obtenerEstadisticas(DTOEstadisticas request) {
        if (!"Restaurante".equals(currentUserService.getCurrentRol())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado: solo los restaurantes pueden obtener estadísticas");
        }
        if (request.getFechaInicio() == null || request.getFechaFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requieren ambas fechas para filtrar estadísticas por rango de fecha");
        }
        LocalDateTime fechaInicio = request.getFechaInicio();
        LocalDateTime fechaFin = request.getFechaFin();

        List<Pedido> pedidosFiltrados = pedidosService.listarPedidosRestauranteActual().stream()
                .filter(p -> !p.getFechaCreacion().isBefore(fechaInicio) && !p.getFechaCreacion().isAfter(fechaFin))
                .collect(Collectors.toList());

        // Platos más solicitados: agrupa por producto, suma cantidades y ordena de mayor a menor
        List<DTOProductoSimplificado> productosMasVendidos = pedidosFiltrados.stream()
                .flatMap(p -> p.getProductos().stream())
                .collect(Collectors.groupingBy(pp -> pp.getProducto().getIdProducto()))
                .entrySet().stream()
                .sorted((a, b) -> b.getValue().stream().mapToInt(pp -> pp.getCantidad()).sum()
                                - a.getValue().stream().mapToInt(pp -> pp.getCantidad()).sum())
                .map(e -> DTOProductoSimplificado.desdeConCantidadVendida(
                        e.getValue().get(0).getProducto(),
                        e.getValue().stream().mapToInt(pp -> pp.getCantidad()).sum()))
                .collect(Collectors.toList());

        // Cantidad de pedidos por fecha (agrupados por día)
        Map<LocalDateTime, Integer> ventasPorFecha = pedidosFiltrados.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getFechaCreacion().truncatedTo(ChronoUnit.DAYS),
                        Collectors.summingInt(p -> 1)));

        // Monto promedio de pedido por día
        Map<LocalDateTime, Float> ingresosPorFecha = pedidosFiltrados.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getFechaCreacion().truncatedTo(ChronoUnit.DAYS),
                        Collectors.averagingDouble(p -> (double) p.getTotal())))
                .entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), e -> e.getValue().floatValue()));

        return new DTOEstadisticas(fechaInicio, fechaFin, productosMasVendidos, ventasPorFecha, ingresosPorFecha);
    }

    @Transactional
    public DTOOferta crearOferta(DTOOferta request, Integer idProducto) {
        Restaurante restaurante = restauranteRepository.findRestauranteById(currentUserService.getCurrentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante no encontrado"));

        Producto producto = restaurante.getProductos().stream()
                .filter(p -> p.getIdProducto().equals(idProducto))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + idProducto));

        if (request.getDescuento() <= 0 || request.getDescuento() >= 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El descuento debe ser un valor entre 0 y 100");
        }
        if (request.getFechaInicio() == null || request.getFechaFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requieren ambas fechas para la oferta");
        }
        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de fin debe ser posterior a la fecha de inicio");
        }

        Oferta oferta = new Oferta(
                request.getDescripcion(),
                request.getDescuento(),
                request.getUrlImagen(),
                request.getFechaInicio(),
                request.getFechaFin());

        producto.setOferta(oferta);
        if (request.getFechaInicio().isBefore(LocalDateTime.now()) && request.getFechaFin().isAfter(LocalDateTime.now())) {
            producto.setOfertaActiva(true);
        }
        productosService.modificarProducto(producto);

        return DTOOferta.desde(oferta);
    }

    @Transactional
    @Modifying
    public void eliminarOferta(Integer idProducto) {
        Restaurante restaurante = restauranteRepository.findRestauranteById(currentUserService.getCurrentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante no encontrado"));

        Producto producto = restaurante.getProductos().stream()
                .filter(p -> p.getIdProducto().equals(idProducto))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + idProducto));

        if (producto.getOferta() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto no tiene una oferta activa para eliminar");
        }

        producto.setOferta(null);
        producto.setOfertaActiva(false);
        productosService.modificarProducto(producto);
    }

    @Transactional
    public void activarDesactivarOferta(DTOModificarOfertaRequest request) {
        Restaurante restaurante = restauranteRepository.findRestauranteById(currentUserService.getCurrentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante no encontrado"));

        Producto producto = restaurante.getProductos().stream()
                .filter(p -> p.getIdProducto().equals(request.getIdProducto()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + request.getIdProducto()));

        if (producto.getOferta() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto no tiene una oferta para activar/desactivar");
        }

        if (request.getHabilitar()) {
            Oferta oferta = producto.getOferta();
            if (!oferta.isVigente()) {
                if (request.getFechaInicio() == null || request.getFechaFin() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La oferta no tiene fechas configuradas. Definí fechaInicio y fechaFin antes de activarla");
                } else{
                    if (request.getFechaFin().isBefore(request.getFechaInicio())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "La fecha de fin debe ser posterior a la fecha de inicio");
                    }
                    oferta.setFechaInicio(request.getFechaInicio());
                    oferta.setFechaFin(request.getFechaFin());
                }      
            }
        }

        producto.setOfertaActiva(request.getHabilitar());
        productosService.modificarProducto(producto);
    }

    
    // Indica si el restaurante del producto cubre la dirección dada, comparando
    // la distancia por ruta contra su radio de entrega (en KM).
    public boolean estaEnZona(Producto producto, DTODireccion direccion){
        Restaurante restaurante = producto.getRestaurante();
        if (restaurante == null) {
            return false;
        }
        return cubreZona(direccion, restaurante.getDireccion(), restaurante.getRadioEntrega());
    }

    private boolean cubreZona(DTODireccion direccionBusqueda, DTODireccion direccionResto, Integer radioEntrega){
        if (direccionResto == null || radioEntrega == null) {
            return false;
        }

        double distancia = geoapifyService.calcularDistanciaKm(
                direccionBusqueda.getLatitud(),
                direccionBusqueda.getLongitud(),
                direccionResto.getLatitud(),
                direccionResto.getLongitud());

        return distancia >= 0 && distancia <= radioEntrega;
    }
}