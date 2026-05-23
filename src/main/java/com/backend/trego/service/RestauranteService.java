package com.backend.trego.service;

import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOFirma;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.repository.RestauranteRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

// Gestión de restaurantes: alta, apertura/cierre, búsqueda y firma de imágenes.
@Service
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;

    public RestauranteService(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
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
        // TODO: implementar
        return List.of();
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
        // TODO: implementar
        return List.of();
    }

    public DTORestaurante verRestaurante(String restauranteId) {
        // TODO: implementar
        return null;
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
