package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOFirma;
import com.backend.trego.entity.DTOs.DTORestaurante;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Servicio encargado de la gestión de Restaurantes:
 * alta, apertura/cierre del local, búsqueda por zona o nombre,
 * consulta de productos asociados y firma de imágenes.
 *
 * Las firmas siguen el Documento de Diseño (Tabla 4 - RestauranteService).
 */
@Service
public class RestauranteService {

    public RestauranteService() {
        // TODO: inyectar RestauranteRepository y servicios auxiliares
    }

    /**
     * Marca el local como abierto a partir de una hora de servicio.
     */
    public boolean abrirLocal(String idRestaurante, Date horaServicio) {
        // TODO: implementar
        return false;
    }

    /**
     * Marca el local como cerrado.
     */
    public boolean cerrarLocal(String restauranteId) {
        // TODO: implementar
        return false;
    }

    /**
     * Lista los restaurantes que cubren la zona indicada por una dirección.
     */
    public List<DTORestaurante> listarRestaurantesZona(DTODireccion direccion) {
        // TODO: implementar
        return List.of();
    }

    /**
     * Verifica la hora de cierre programada de los restaurantes y dispara cierres automáticos.
     */
    public void verificarHoraCierre() {
        // TODO: implementar
    }

    /**
     * Obtiene un restaurante por su id.
     */
    public DTORestaurante obtenerRestaurante(String restauranteId) {
        // TODO: implementar
        return null;
    }

    /**
     * Actualiza la hora de cierre del restaurante.
     */
    public Date actualizarHoraCierre(Date horaCierre) {
        // TODO: implementar
        return null;
    }

    /**
     * Crea un nuevo restaurante en el sistema.
     */
    public void crearRestaurante(DTORestaurante restauranteDTO) {
        // TODO: implementar
    }

    /**
     * Obtiene la información de un restaurante junto con su catálogo de productos.
     */
    public DTORestaurante verRestauranteConProducto(String restauranteId) {
        // TODO: implementar
        return null;
    }

    /**
     * Busca restaurantes cuyo nombre coincida con el texto indicado.
     */
    public List<DTORestaurante> buscarRestaurantePorNombre(String nombre) {
        // TODO: implementar
        return List.of();
    }

    /**
     * Devuelve la vista pública de un restaurante para mostrar al cliente.
     */
    public DTORestaurante verRestaurante(String restauranteId) {
        // TODO: implementar
        return null;
    }

    /**
     * Lista los restaurantes que solicitaron alta y están a la espera de aprobación.
     */
    public List<DTORestaurante> listarRestaurantesEnEspera() {
        // TODO: implementar
        return List.of();
    }

    /**
     * Genera una firma para subir una imagen (logo, portada, etc.) del restaurante.
     */
    public DTOFirma firmarImagen(String nombreArchivo, String tipoArchivo) {
        // TODO: implementar
        return null;
    }
}
