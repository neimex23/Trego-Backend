package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTOFirma;
import com.backend.trego.entity.DTOs.DTOIngrediente;
import com.backend.trego.entity.DTOs.DTOProducto;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio encargado de la gestión del catálogo de Productos
 * e Ingredientes de un restaurante.
 *
 * Las firmas siguen el Documento de Diseño (Tabla 6 - ProductosService).
 */
@Service
public class ProductosService {

    public ProductosService() {
        // TODO: inyectar ProductoRepository, IngredienteRepository
    }

    /**
     * Lista los productos del restaurante indicado.
     */
    public List<DTOProducto> listarProductos(String idRestaurante) {
        // TODO: implementar
        return List.of();
    }

    /**
     * Lista los ingredientes disponibles para el restaurante indicado.
     */
    public List<DTOIngrediente> listarIngredientes(String idRestaurante) {
        // TODO: implementar
        return List.of();
    }

    /**
     * Crea un nuevo ingrediente con el nombre indicado.
     */
    public DTOIngrediente crearIngrediente(String nombre) {
        // TODO: implementar
        return null;
    }

    /**
     * Genera una firma para subir una imagen al storage externo.
     */
    public DTOFirma generarFirma(String nombreArchivo, String tipoArchivo) {
        // TODO: implementar
        return null;
    }

    /**
     * Crea un nuevo producto del menú.
     */
    public DTOProducto crearProducto(DTOProducto productoDTO) {
        // TODO: implementar
        return null;
    }

    /**
     * Firma un archivo de imagen para su upload (alias de generarFirma).
     */
    public DTOFirma firmarImagen(String nombreArchivo, String tipoArchivo) {
        // TODO: implementar
        return null;
    }
}
