package com.backend.trego.service;

import com.backend.trego.entity.Articulo;
import com.backend.trego.entity.Combo;
import com.backend.trego.entity.Plato;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.DTOs.DTOFirma;
import com.backend.trego.entity.DTOs.DTOIngrediente;
import com.backend.trego.entity.DTOs.DTOProducto;
import com.backend.trego.entity.Enums.EnumCategoriaProducto;
import com.backend.trego.entity.Enums.EnumTipoProducto;
import com.backend.trego.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// Catálogo de productos e ingredientes de cada restaurante.
@Service
public class ProductosService {

    private final ProductoRepository productoRepository;
    private final CloudinaryService cloudinaryService;

    public ProductosService(ProductoRepository productoRepository, CloudinaryService cloudinaryService) {
        this.productoRepository = productoRepository;
        this.cloudinaryService = cloudinaryService;
    }

    // Devuelve los productos del menú de un restaurante, ya mapeados a DTO.
    public List<DTOProducto> listarProductos(String idRestaurante) {
        Integer id = parseId(idRestaurante);
        return productoRepository.findByRestauranteIdUsuario(id).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
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
        return cloudinaryService.firmar(nombreArchivo, tipoArchivo);
    }

    public DTOProducto crearProducto(DTOProducto productoDTO) {
        // TODO: implementar
        return null;
    }

    public DTOFirma firmarImagen(String nombreArchivo, String tipoArchivo) {
        return cloudinaryService.firmar(nombreArchivo, tipoArchivo);
    }

    // Convierte una entidad Producto (Plato, Articulo o Combo) a su DTO de catálogo.
    private DTOProducto toDTO(Producto producto) {
        EnumCategoriaProducto categoria = producto.getSubCategoria() != null
                ? producto.getSubCategoria().getCategoria()
                : null;

        Integer idRestaurante = producto.getRestaurante() != null
                ? producto.getRestaurante().getIdUsuario()
                : null;

        return new DTOProducto(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getUrlImagen(),
                categoria,
                true, // disponible: el modelo actual no maneja stock por unidad
                idRestaurante,
                null, // cantidadDisponible: no modelado
                mapearIngredientes(producto),
                tipoDe(producto));
    }

    // Solo los platos tienen ingredientes asociados en el modelo actual.
    private List<DTOIngrediente> mapearIngredientes(Producto producto) {
        if (!(producto instanceof Plato plato)) {
            return Collections.emptyList();
        }
        Integer idRestaurante = producto.getRestaurante() != null
                ? producto.getRestaurante().getIdUsuario()
                : null;
        return plato.getIngredientes().stream()
                .map(ing -> new DTOIngrediente(ing.getIdIngrediente(), ing.getNombre(), idRestaurante))
                .collect(Collectors.toList());
    }

    private EnumTipoProducto tipoDe(Producto producto) {
        if (producto instanceof Plato) {
            return EnumTipoProducto.Plato;
        }
        if (producto instanceof Articulo) {
            return EnumTipoProducto.Articulo;
        }
        if (producto instanceof Combo) {
            return EnumTipoProducto.Combo;
        }
        return null;
    }

    private Integer parseId(String idRestaurante) {
        try {
            return Integer.valueOf(idRestaurante);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Id de restaurante inválido: " + idRestaurante);
        }
    }
}
