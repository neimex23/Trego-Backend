package com.backend.trego.service;

import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOFirma;
import com.backend.trego.entity.DTOs.DTOProducto;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.exception.SinProductoException;
import com.backend.trego.repository.RestauranteRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// Gestión de restaurantes: alta, apertura/cierre, búsqueda y firma de imágenes.
@Service
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final ProductosService productosService;

    public RestauranteService(RestauranteRepository restauranteRepository, ProductosService productosService) {
        this.restauranteRepository = restauranteRepository;
        this.productosService = productosService;
    }

    public boolean abrirLocal(String idRestaurante, Date horaServicio) {
        // TODO: implementar
        return false;
    }

    public boolean cerrarLocal(String restauranteId) {
        // TODO: implementar
        return false;
    }

    public List<DTORestaurante> listarRestaurantesZona(DTODireccion direccion) {
        // TODO: implementar (filtrado por zona/dirección)
        return List.of();
    }

    // Lista los restaurantes registrados y habilitados, en su forma pública (sin
    // password ni menú). Es el catálogo que ve el cliente.
    public List<DTORestaurante> listarRestaurantes() {
        return restauranteRepository.findByHabilitadoTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void verificarHoraCierre() {
        // TODO: implementar
    }

    public DTORestaurante obtenerRestaurante(String restauranteId) {
        Restaurante restaurante = buscarRestaurante(restauranteId);
        return toDTO(restaurante);
    }

    // Carga la entidad o devuelve 404 si no existe.
    public Restaurante buscarRestaurante(String restauranteId) {
        Integer id = parseId(restauranteId);
        return restauranteRepository.findById(id)
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
                restaurante.getUrlImagen(),
                restaurante.getCategoria(),
                restaurante.isHabilitado(),
                estaAbiertoDe(restaurante));
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

    public Date actualizarHoraCierre(Date horaCierre) {
        // TODO: implementar
        return null;
    }

    public void crearRestaurante(DTORestaurante restauranteDTO) {
        // TODO: implementar
    }

    public DTORestaurante verRestauranteConProducto(String restauranteId) {
        // TODO: implementar
        return null;
    }

    public List<DTORestaurante> buscarRestaurantePorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return listarRestaurantes();
        }
        return restauranteRepository.findByHabilitadoTrueAndNombreContainingIgnoreCase(nombre.trim()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Devuelve el restaurante junto con su menú de productos, aplicando filtro por
    // categoría y orden por precio si se indican. Lanza SinProductoException si,
    // tras filtrar, el restaurante no tiene productos para mostrar.
    public DTORestaurante verRestaurante(Integer restauranteId, String categoria, String orden) {
        String id = String.valueOf(restauranteId);
        Restaurante restaurante = buscarRestaurante(id);

        List<DTOProducto> productos = productosService.listarProductos(id);

        if (categoria != null && !categoria.isBlank()) {
            productos = aplicarFiltro(productos, categoria);
        }

        if (orden != null && !orden.isBlank()) {
            productos = aplicarOrden(productos, orden);
        }

        if (productos.isEmpty()) {
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
                restaurante.getTelefono(),
                restaurante.getUrlImagen(),
                restaurante.getDireccion(),
                restaurante.getCategoria(),
                restaurante.isHabilitado(),
                estaAbiertoDe(restaurante),
                null, // horaApertura: la entidad usa LocalTime, el DTO Date
                null, // horaCierre
                productos);
    }

    public List<DTORestaurante> listarRestaurantesEnEspera() {
        // TODO: implementar
        return List.of();
    }

    public DTOFirma firmarImagen(String nombreArchivo, String tipoArchivo) {
        // TODO: implementar
        return null;
    }
}
