package com.backend.trego.service;

import com.backend.trego.entity.Articulo;
import com.backend.trego.entity.Combo;
import com.backend.trego.entity.Plato;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.DTOs.DTOArticulo;
import com.backend.trego.entity.DTOs.DTOCombo;
import com.backend.trego.entity.DTOs.DTOFirma;
import com.backend.trego.entity.DTOs.DTOIngrediente;
import com.backend.trego.entity.DTOs.DTOOferta;
import com.backend.trego.entity.DTOs.DTOPlato;
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
    private final CurrentUserService currentUserService;

    public ProductosService(ProductoRepository productoRepository, CloudinaryService cloudinaryService, CurrentUserService currentUserService) {
        this.productoRepository = productoRepository;
        this.cloudinaryService = cloudinaryService;
        this.currentUserService = currentUserService;
    }

    // Devuelve los productos del menú de un restaurante, ya mapeados a DTO.
    public List<DTOProducto> listarProductos(String idRestaurante) {
        Integer id = parseId(idRestaurante);
        return productoRepository.findByRestauranteIdUsuario(id).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DTOFirma generarFirma(String nombreArchivo, String tipoArchivo) {
        return cloudinaryService.firmar(nombreArchivo, tipoArchivo);
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

        EnumTipoProducto tipo = tipoDe(producto);

        return new DTOProducto(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getUrlImagen(),
                categoria,
                producto.getDisponible(),
                idRestaurante,
                mapearIngredientes(producto),
                tipo,
                DTOOferta.desde(producto.getOferta()),
                mapearPlato(producto),
                mapearArticulo(producto),
                mapearCombo(producto)
        );
    }

    private DTOPlato mapearPlato(Producto producto) {
        if (producto instanceof Plato plato) {
            return new DTOPlato(plato.getTiempoPreparacionMinutos());
        }
        return null;
    }

    private DTOArticulo mapearArticulo(Producto producto) {
        if (producto instanceof Articulo) {
            return new DTOArticulo();
        }
        return null;
    }

    private DTOCombo mapearCombo(Producto producto) {
        if (producto instanceof Combo combo) {
            List<Integer> ids = combo.getProductosIncluidos().stream()
                    .map(Producto::getIdProducto)
                    .collect(Collectors.toList());
            return new DTOCombo(ids);
        }
        return null;
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
