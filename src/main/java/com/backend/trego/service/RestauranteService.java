package com.backend.trego.service;

import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Comentario;
import com.backend.trego.entity.Ingrediente;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.DTOs.DTOComentario;
import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOFirma;
import com.backend.trego.entity.DTOs.DTOIngrediente;
import com.backend.trego.entity.DTOs.DTOProducto;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.entity.Enums.EnumCategoriaRestaurante;
import com.backend.trego.exception.RestauranteCerradoException;
import com.backend.trego.exception.SinProductoException;
import com.backend.trego.repository.UsuarioRepository;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// Gestión de restaurantes: alta, apertura/cierre, búsqueda y firma de imágenes.
@Service
public class RestauranteService {

    private final UsuarioRepository restauranteRepository;
    private final CurrentUserService currentUserService;
    private final ProductosService productosService;
    private final NotificacionesService notificacionesService;
    private final GeoapifyService geoapifyService;

    // Para calcular y cerrar automaticamente el restaurante
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<Integer, ScheduledFuture<?>> cierresProgramados = new ConcurrentHashMap<>();

    public RestauranteService(UsuarioRepository restauranteRepository, CurrentUserService currentUserService,
            @Lazy ProductosService productosService, NotificacionesService notificacionesService,
            GeoapifyService geoapifyService) {
        this.restauranteRepository = restauranteRepository;
        this.currentUserService = currentUserService;
        this.productosService = productosService;
        this.notificacionesService = notificacionesService;
        this.geoapifyService = geoapifyService;
    }

    public void abrirLocal(LocalTime horaCierre) {
        Integer restauranteId = currentUserService.getCurrentId();
        Restaurante restaurante = restauranteRepository.findRestauranteById(restauranteId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        
        // Precondición: el local debe estar cerrado
        if (restaurante.getAbierto()) {
            throw new RestauranteCerradoException("El local ya se encuentra abierto");
        }

        List<DTOProducto> productos;
        try {
            productos = productosService.listarProductos(String.valueOf(restauranteId), false);
        } catch (ResponseStatusException e) {
            productos = Collections.emptyList();
        }

        if (productos == null || productos.isEmpty()) {
            throw new RestauranteCerradoException("Debe tener algun producto para ofrecer");
        }
        abrirCerrarRestaurante(restaurante, true, horaCierre);
        programarCierre(restauranteId, horaCierre);
    }

    public void cerrarLocal() {
        Integer restauranteId = currentUserService.getCurrentId();
        Restaurante restaurante = restauranteRepository.findRestauranteById(restauranteId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        
        // Precondición: el local debe estar abierto
        if (!restaurante.getAbierto()) {
            throw new RestauranteCerradoException("El local ya se encontraba cerrado");
        }        

        // Cancelar el cronómetro programado si existe
        ScheduledFuture<?> cierresProgramado = cierresProgramados.get(restauranteId);
        if (cierresProgramado != null && !cierresProgramado.isDone()) {
            cierresProgramado.cancel(false);
            cierresProgramados.remove(restauranteId);
            System.out.println("DEBUG: Cronómetro cancelado por cierre manual del restaurante " + restauranteId);
        }

        // Obtener la hora actual para dejar registro en la bd
        LocalTime horaActual = LocalTime.now();
        abrirCerrarRestaurante(restaurante, false, horaActual);
    }

    // Método para actualizar la hora del cierre automatico
    public void actualizarHoraCierre(LocalTime horaCierre) {
        Integer restauranteId = currentUserService.getCurrentId();
        Restaurante restaurante = restauranteRepository.findRestauranteById(restauranteId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        abrirCerrarRestaurante(restaurante, true, horaCierre);
        programarCierre(restauranteId, horaCierre);
    }

    // Método privado para abrir y cerrar el restaurante
    private void abrirCerrarRestaurante(Restaurante restaurante, boolean estadoAbierto, LocalTime horaCierre) {
        restaurante.setAbierto(estadoAbierto);
        if (horaCierre != null) {
            restaurante.setHorario(restaurante.getApertura(), horaCierre);
        }
        restauranteRepository.save(restaurante);
    }

    // Método privado para programar el cierre
    private void programarCierre(Integer restauranteId, LocalTime horaCierre) {
        // Cancelar el cronómetro anterior si existe
        ScheduledFuture<?> cierreAnterior = cierresProgramados.get(restauranteId);
        if (cierreAnterior != null && !cierreAnterior.isDone()) {
            cierreAnterior.cancel(false);
            System.out.println("DEBUG: Cronómetro anterior cancelado para restaurante " + restauranteId);
        }

        // Calcular segundos hasta el cierre
        LocalTime ahora = LocalTime.now();
        long segundosHastaCierre = ahora.until(horaCierre, ChronoUnit.SECONDS);
        if (segundosHastaCierre <= 0) {
            segundosHastaCierre += 24 * 60 * 60;
        }

        System.out.println("DEBUG: Nuevo cierre programado en " + segundosHastaCierre + " segundos");

        // Programar el nuevo cierre y guardarlo en el Map
        ScheduledFuture<?> nuevoCierre = scheduler.schedule(() -> {
            try {
                Restaurante res = restauranteRepository.findRestauranteById(restauranteId)
                        .orElseThrow(() -> new RuntimeException("Restaurante no encontrado al cerrar"));
                abrirCerrarRestaurante(res, false, null);
                cierresProgramados.remove(restauranteId);
                System.out.println("DEBUG: Restaurante " + restauranteId + " cerrado automáticamente");
            } catch (Exception e) {
                System.out.println("ERROR al cerrar restaurante automáticamente: " + e.getMessage());
            }
        }, segundosHastaCierre, TimeUnit.SECONDS);

        cierresProgramados.put(restauranteId, nuevoCierre);
    }


    public List<DTORestaurante> listarRestaurantesZona(DTODireccion direccion) {

        List<DTORestaurante> restaurantesHabilitados = listarRestaurantesHabilitadosNoCerrados();
        if (restaurantesHabilitados.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No hay restaurantes habilitados");
        }

        double latitud = direccion.getLatitud();
        double longitud = direccion.getLongitud();

        List<DTORestaurante> restaurantesFiltro = new ArrayList<>();

        for (DTORestaurante dtoRestaurante : restaurantesHabilitados) {

            DTODireccion direccionResto = dtoRestaurante.getDireccion();
            if (direccionResto == null) {
                continue;
            }

            double latitudResto = direccionResto.getLatitud();
            double longitudResto = direccionResto.getLongitud();

            double radioEntrega = dtoRestaurante.getRadioEntrega(); // en KM

            double distancia = geoapifyService.calcularDistanciaKm(
                    latitud,
                    longitud,
                    latitudResto,
                    longitudResto);

            // Si Geoapify no pudo calcular la ruta, descartamos el restaurante.
            if (distancia >= 0 && distancia <= radioEntrega) {
                restaurantesFiltro.add(dtoRestaurante);
            }
        }

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
                .filter(this::estaAbiertoDe)
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
        restauranteRepository.save(restaurante);
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

    public boolean estaAbierto(String restauranteId) {
        Restaurante restaurante = buscarRestaurante(restauranteId);
        if (!restaurante.isHabilitado()) {
            return false;
        }
        LocalTime apertura = restaurante.getApertura();
        LocalTime cierre = restaurante.getCierre();
        if (apertura == null || cierre == null) {
            return true;
        }
        LocalTime ahora = LocalTime.now();
        if (cierre.isAfter(apertura)) {
            return !ahora.isBefore(apertura) && !ahora.isAfter(cierre);
        }
        // Horario que cruza la medianoche (ej. 20:00 - 02:00).
        return !ahora.isBefore(apertura) || !ahora.isAfter(cierre);
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
        return new DTORestaurante(
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
                restaurante.getCierre());
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
                null,
                null,
                null,
                null);
    }

    

    // Misma lógica que estaAbierto pero sobre una entidad ya cargada, para no
    // volver a consultar la base.
    private boolean estaAbiertoDe(Restaurante restaurante) {
        if (!restaurante.isHabilitado()) {
            return false;
        }
        LocalTime apertura = restaurante.getApertura();
        LocalTime cierre = restaurante.getCierre();
        if (apertura == null || cierre == null) {
            return true;
        }
        LocalTime ahora = LocalTime.now();
        if (cierre.isAfter(apertura)) {
            return !ahora.isBefore(apertura) && !ahora.isAfter(cierre);
        }
        return !ahora.isBefore(apertura) || !ahora.isAfter(cierre);
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
            productos.sort(Comparator.comparing(DTOProducto::getPrecio));
        } else if (orden.equalsIgnoreCase("precio_desc")) {
            productos.sort(Comparator.comparing(DTOProducto::getPrecio).reversed());
        }
        return productos;
    }

    // Arma el DTO con los datos públicos del restaurante más la lista de productos.
    // No se envía el password al frontend.
    private DTORestaurante toDTOConProductos(Restaurante restaurante, List<DTOProducto> productos) {
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
                restaurante.getCategoria(),
                restaurante.getCalificacionProm(),
                restaurante.getRadioEntrega(),
                restaurante.isHabilitado(),
                restaurante.getAbierto(),
                restaurante.getApertura(),
                restaurante.getCierre(),
                productos);
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

    public DTOComentario agregarComentario(DTOComentario request) {
        if (!currentUserService.getCurrentRol().equals("CLIENTE")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado: solo los clientes pueden agregar comentarios");
        }

        Restaurante restaurante = buscarRestaurante(String.valueOf(request.getIdRestaurante()));
        Cliente cliente = restauranteRepository.findClienteById(currentUserService.getCurrentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente autenticado no encontrado"));

        Comentario comentario = new Comentario(request.getTexto(), request.getCalificacion(), cliente, restaurante);
        restaurante.addComentario(comentario);
        restauranteRepository.save(restaurante);
        return new DTOComentario(
            comentario.getIdComentario(), 
            comentario.getTexto(), 
            restaurante.getIdUsuario(),
            comentario.getCalificacion(), 
            comentario.getFechaCreacion().toString(), 
            comentario.getCliente().getNombre());
    }

    public List<DTOComentario> listarComentarios() {
        Restaurante restaurante = buscarRestaurante(String.valueOf(currentUserService.getCurrentId()));
        return restaurante.getComentarios().stream()
                .map(c -> new DTOComentario(
                    c.getIdComentario(), 
                    c.getTexto(), 
                    restaurante.getIdUsuario(),
                    c.getCalificacion(), 
                    c.getFechaCreacion().toString(), 
                    c.getCliente().getNombre()))
                .collect(Collectors.toList());
    }
}