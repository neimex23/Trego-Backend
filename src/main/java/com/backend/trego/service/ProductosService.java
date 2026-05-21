package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTOFirma;
import com.backend.trego.entity.DTOs.DTOIngrediente;
import com.backend.trego.entity.DTOs.DTOProducto;

import org.springframework.stereotype.Service;

import java.util.List;

// Catálogo de productos e ingredientes de cada restaurante.
@Service
public class ProductosService {

    public ProductosService() {
        // TODO: inyectar ProductoRepository, IngredienteRepository
    }

    public List<DTOProducto> listarProductos(String idRestaurante) {
        // TODO: implementar
        return List.of();
    }

    public List<DTOIngrediente> listarIngredientes(String idRestaurante) {
        // TODO: implementar
        return List.of();
    }

    public DTOIngrediente crearIngrediente(String nombre) {
        // TODO: implementar
        return null;
    }

    public DTOFirma generarFirma(String nombreArchivo, String tipoArchivo) {
        // TODO: implementar
        return null;
    }

    public DTOProducto crearProducto(DTOProducto productoDTO) {
        // TODO: implementar
        return null;
    }

    public DTOFirma firmarImagen(String nombreArchivo, String tipoArchivo) {
        // TODO: implementar
        return null;
    }
}
