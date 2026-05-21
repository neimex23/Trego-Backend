package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOFirma;
import com.backend.trego.entity.DTOs.DTORestaurante;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

// Gestión de restaurantes: alta, apertura/cierre, búsqueda y firma de imágenes.
@Service
public class RestauranteService {

    public RestauranteService() {
        // TODO: inyectar RestauranteRepository y servicios auxiliares
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
        // TODO: implementar
        return null;
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
