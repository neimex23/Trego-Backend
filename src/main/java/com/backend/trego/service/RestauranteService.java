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
    private final CurrentUserService currentUserService;
    private final ProductosService productosService;
    private final CloudinaryService cloudinaryService;

    public RestauranteService(RestauranteRepository restauranteRepository, CurrentUserService currentUserService, ProductosService productosService,
            CloudinaryService cloudinaryService) {
        this.restauranteRepository = restauranteRepository;
        this.currentUserService = currentUserService;
        this.productosService = productosService;
        this.cloudinaryService = cloudinaryService;
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
                restaurante.getFotoPortada(),
                restaurante.getFotoPerfil(),
                restaurante.getDescripcion(),
                restaurante.getCategoria(),
                restaurante.getCalificacionProm(),
                restaurante.getRadioEntrega(),
                restaurante.isHabilitado(),
                estaAbiertoDe(restaurante),
                restaurante.getApertura(),
                restaurante.getCierre());
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
                restaurante.getRut(),
                restaurante.getTelefono(),
                restaurante.getFotoPerfil(),
                restaurante.getFotoPortada(),
                restaurante.getDireccion(),
                restaurante.getDescripcion(),
                restaurante.getCategoria(),
                restaurante.getCalificacionProm(),
                restaurante.getRadioEntrega(),
                restaurante.isHabilitado(),
                estaAbiertoDe(restaurante),
                restaurante.getApertura(),
                restaurante.getCierre(),
                productos);
    }

    public List<DTORestaurante> listarRestaurantesEnEspera() {
        // TODO: implementar
        return List.of();
    }

    public DTOFirma firmarArchivo(String nombreArchivo, String tipoArchivo) {
        return cloudinaryService.firmar(nombreArchivo, tipoArchivo);
    }

    // Actualiza los datos del restaurante aplicando sólo los campos no nulos del
    // DTO. No se permite modificar id ni habilitado.
    public DTORestaurante actualizarRestaurante(DTORestaurante dto) {
        Restaurante restaurante = buscarRestaurante(String.valueOf(currentUserService.getCurrentId()));

        if (dto.getNombre() != null) {
            restaurante.setNombre(dto.getNombre());
        }
        if (dto.getEmail() != null) {
            restaurante.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null) {
            restaurante.setPassword(dto.getPassword());
        }
        if (dto.getRut() != null) {
            restaurante.setRut(dto.getRut());
        }
        if (dto.getTelefono() != null) {
            restaurante.setTelefono(dto.getTelefono());
        }
        if (dto.getFotoPortada() != null) {
            restaurante.setFotoPortada(dto.getFotoPortada());
        }
        if (dto.getDireccion() != null) {
            restaurante.setDireccion(dto.getDireccion());
        }
        if (dto.getDescripcion() != null) {
            restaurante.setDescripcion(dto.getDescripcion());
        }
        if (dto.getCategoria() != null) {
            restaurante.setCategoria(dto.getCategoria());
        }
        if (dto.getCalificacionProm() != null) {
            restaurante.setCalificacionProm(dto.getCalificacionProm());
        }
        if (dto.getRadioEntrega() != null) {
            restaurante.setRadioEntrega(dto.getRadioEntrega());
        }
        if (dto.getHoraApertura() != null || dto.getHoraCierre() != null) {
            LocalTime apertura = dto.getHoraApertura() != null ? dto.getHoraApertura() : restaurante.getApertura();
            LocalTime cierre = dto.getHoraCierre() != null ? dto.getHoraCierre() : restaurante.getCierre();
            restaurante.setHorario(apertura, cierre);
        }

        Restaurante actualizado = restauranteRepository.save(restaurante);
        return toDTO(actualizado);
    }
}
