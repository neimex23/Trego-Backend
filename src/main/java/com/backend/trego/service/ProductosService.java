package com.backend.trego.service;

import com.backend.trego.entity.Articulo;
import com.backend.trego.entity.Combo;
import com.backend.trego.entity.Ingrediente;
import com.backend.trego.entity.Plato;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.SubCategoria;
import com.backend.trego.entity.DTOs.DTOArticulo;
import com.backend.trego.entity.DTOs.DTOCombo;
import com.backend.trego.entity.DTOs.DTOFirma;
import com.backend.trego.entity.DTOs.DTOIngrediente;
import com.backend.trego.entity.DTOs.DTOOferta;
import com.backend.trego.entity.DTOs.DTOPlato;
import com.backend.trego.entity.DTOs.DTOCrearProductoRequest;
import com.backend.trego.entity.DTOs.DTOModificarProductoRequest;
import com.backend.trego.entity.DTOs.DTOProducto;
import com.backend.trego.entity.DTOs.DTOSubCategoria;
import com.backend.trego.entity.Enums.EnumCategoriaProducto;
import com.backend.trego.entity.Enums.EnumTipoProducto;
import com.backend.trego.repository.IngredienteRepository;
import com.backend.trego.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Catálogo de productos e ingredientes de cada restaurante.
@Service
public class ProductosService {

    private final ProductoRepository productoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final RestauranteService restauranteService;
    private final CloudinaryService cloudinaryService;
    private final CurrentUserService currentUserService;
    private final SubCategoriaService subCategoriaService;

    public ProductosService(ProductoRepository productoRepository, IngredienteRepository ingredienteRepository,
            RestauranteService restauranteService, CloudinaryService cloudinaryService,
            CurrentUserService currentUserService, SubCategoriaService subCategoriaService) {
        this.productoRepository = productoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.restauranteService = restauranteService;
        this.cloudinaryService = cloudinaryService;
        this.currentUserService = currentUserService;
        this.subCategoriaService = subCategoriaService;
    }

    // Devuelve los productos del menú de un restaurante, ya mapeados a DTO.
    @Transactional(readOnly = true)
    public List<DTOProducto> listarProductos(String idRestaurante, boolean restauranActual) {
        if (restauranActual) {
            String rol = currentUserService.getCurrentRol();
            if (!"Restaurante".equals(rol)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "El endpoint solo está disponible para restaurantes autenticados (rol actual: " + rol + ")");
            }else {
                idRestaurante = String.valueOf(currentUserService.getCurrentId());
            }
        }
        Integer id = parseId(idRestaurante);
        var productos = productoRepository.findByRestauranteIdUsuario(id).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
            if (productos.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontraron productos para el restaurante con id: " + id);
            }
        return productos;
    }

    public List<DTOProducto> listarSoloProductosHabilitados(String idRestaurante, boolean restauranteActual) {
        if (restauranteActual) {
            String rol = currentUserService.getCurrentRol();
            if (!"Restaurante".equals(rol)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "El endpoint solo está disponible para restaurantes autenticados (rol actual: " + rol + ")");
            } else {
                idRestaurante = String.valueOf(currentUserService.getCurrentId());
            }
        }

        Integer id = parseId(idRestaurante);

        var productos = productoRepository.findByRestauranteIdUsuarioAndDisponibleTrue(id).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        if (productos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron productos habilitados para el restaurante con id: " + id);
        }

        return productos;
    }

    @Transactional
    public DTOProducto crearProducto(DTOCrearProductoRequest request) {
        // Validar que el restaurante existe y pertenece al usuario autenticado.
        String rol = currentUserService.getCurrentRol();
        if (!"Restaurante".equals(rol)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El endpoint solo está disponible para restaurantes autenticados (rol actual: " + rol + ")");
        }
        Integer idRestaurante = currentUserService.getCurrentId();
        Producto producto = null;

        switch (request.getTipo()) {
            case Plato -> {
                if (request.getPlato() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "El producto de tipo Plato debe incluir el campo 'plato' con los detalles específicos del plato.");
                }
                Plato plato = new Plato(
                    request.getNombre(),
                    request.getPrecio(),
                    request.getDescripcion(),
                    request.getUrlImagen(),
                    request.getPlato().getTiempoPreparacionMinutos()
                );
                producto = plato;
            }
            case Combo -> {
                if (request.getCombo() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "El producto de tipo Combo debe incluir el campo 'combo' con los detalles específicos del combo.");
                }
                List<Producto> productosIncluidos = request.getCombo().getProductosIncluidosIds().stream()
                        .map(id -> productoRepository.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Producto incluido en el combo no encontrado con id: " + id)))
                        .collect(Collectors.toList());
                producto = new Combo(
                    request.getNombre(),
                    request.getPrecio(),
                    request.getDescripcion(),
                    request.getUrlImagen()
                );
                ((Combo) producto).setProductosIncluidos(productosIncluidos);
            }
            case Articulo -> {
                producto = new Articulo(
                    request.getNombre(),
                    request.getPrecio(),
                    request.getDescripcion(),
                    request.getUrlImagen()
                );
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tipo de producto inválido: " + request.getTipo());
        }

        Restaurante restaurante = restauranteService.buscarRestaurante(String.valueOf(idRestaurante));
        producto.setRestaurante(restaurante);

        if (request.getIdSubCategoria() != null) {
            SubCategoria subCategoria = subCategoriaService.buscarPorId(request.getIdSubCategoria());
            producto.setSubCategoria(subCategoria);
        }

        if (request.getDisponible() != null) {
            producto.setDisponible(request.getDisponible());
        }

        if (producto instanceof Plato plato && request.getIngredientes() != null) {
            vincularIngredientesPlato(plato, request.getIngredientes(), idRestaurante);
        }

        productoRepository.save(producto);
        return toDTO(producto);
    }

    @Transactional
    public DTOProducto modificarProducto(DTOModificarProductoRequest request) {
        String rol = currentUserService.getCurrentRol();
        if (!"Restaurante".equals(rol)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El endpoint solo está disponible para restaurantes autenticados (rol actual: " + rol + ")");
        }
        if (request.getIdProducto() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El idProducto es obligatorio para modificar un producto.");
        }

        Integer idRestaurante = currentUserService.getCurrentId();
        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Producto no encontrado con id: " + request.getIdProducto()));

        if (producto.getRestaurante() == null
                || !Objects.equals(producto.getRestaurante().getIdUsuario(), idRestaurante)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El producto no pertenece al restaurante autenticado.");
        }

        if (request.getNombre() != null) {
            producto.setNombre(request.getNombre());
        }
        if (request.getDescripcion() != null) {
            producto.setDescripcion(request.getDescripcion());
        }
        producto.setPrecio(request.getPrecio());
        if (request.getUrlImagen() != null) {
            producto.setUrlImagen(request.getUrlImagen());
        }
        if (request.getDisponible() != null) {
            producto.setDisponible(request.getDisponible());
        }
        if (request.getIdSubCategoria() != null) {
            SubCategoria subCategoria = subCategoriaService.buscarPorId(request.getIdSubCategoria());
            producto.setSubCategoria(subCategoria);
        }

        EnumTipoProducto tipoActual = tipoDe(producto);
        if (request.getTipo() != null && request.getTipo() != tipoActual) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede cambiar el tipo de un producto existente.");
        }

        switch (tipoActual) {
            case Plato -> {
                Plato plato = (Plato) producto;
                if (request.getPlato() != null
                        && request.getPlato().getTiempoPreparacionMinutos() != null) {
                    plato.setTiempoPreparacionMinutos(request.getPlato().getTiempoPreparacionMinutos());
                }
                if (request.getIngredientes() != null) {
                    vincularIngredientesPlato(plato, request.getIngredientes(), idRestaurante);
                }
            }
            case Combo -> {
                if (request.getCombo() != null
                        && request.getCombo().getProductosIncluidosIds() != null) {
                    Combo combo = (Combo) producto;
                    List<Producto> productosIncluidos = request.getCombo().getProductosIncluidosIds().stream()
                            .map(id -> productoRepository.findById(id)
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                            "Producto incluido en el combo no encontrado con id: " + id)))
                            .collect(Collectors.toList());
                    combo.setProductosIncluidos(productosIncluidos);
                }
            }
            case Articulo -> {
                // Sin campos adicionales en el modelo actual.
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tipo de producto inválido: " + tipoActual);
        }

        productoRepository.save(producto);
        return toDTO(producto);
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
                mapearCombo(producto),
                DTOSubCategoria.desde(producto.getSubCategoria())
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

    private void vincularIngredientesPlato(Plato plato, List<DTOIngrediente> ingredientesDTO,
            Integer idRestaurante) {
        plato.getIngredientes().clear();
        for (DTOIngrediente dtoIng : ingredientesDTO) {
            if (dtoIng.getIdIngrediente() == null) {
                continue;
            }
            Integer idIngrediente = dtoIng.getIdIngrediente();
            if (ingredienteRepository.countByIdAndRestauranteId(idIngrediente, idRestaurante) == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El ingrediente con id " + idIngrediente
                                + " no pertenece a los ingredientes disponibles del restaurante.");
            }
            Ingrediente ingrediente = ingredienteRepository.findById(idIngrediente)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Ingrediente no encontrado con id: " + idIngrediente));
            plato.addIngrediente(ingrediente);
        }
    }

    // Solo los platos tienen ingredientes asociados en el modelo actual.
    private List<DTOIngrediente> mapearIngredientes(Producto producto) {
        if (!(producto instanceof Plato plato)) {
            return Collections.emptyList();
        }
        Integer idRestaurante = producto.getRestaurante() != null
                ? producto.getRestaurante().getIdUsuario()
                : null;
        List<Ingrediente> ingredientes = plato.getIngredientes();
        if (ingredientes == null || ingredientes.isEmpty()) {
            return Collections.emptyList();
        }
        return ingredientes.stream()
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

    @Transactional
    public void deshabilitarProducto(Integer idProducto) {
        String rol = currentUserService.getCurrentRol();
        if (!"Restaurante".equals(rol)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El endpoint solo está disponible para restaurantes autenticados (rol actual: " + rol + ")");
        }

        Integer idRestaurante = currentUserService.getCurrentId();

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Producto no encontrado con id: " + idProducto));

        if (!producto.getRestaurante().getIdUsuario().equals(idRestaurante)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tenés permisos para modificar este producto.");
        }

        producto.setDisponible(false);
        productoRepository.save(producto);
    }

    @Transactional
    public void habilitarProducto(Integer idProducto) {
        String rol = currentUserService.getCurrentRol();
        if (!"Restaurante".equals(rol)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El endpoint solo está disponible para restaurantes autenticados (rol actual: " + rol + ")");
        }

        Integer idRestaurante = currentUserService.getCurrentId();

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Producto no encontrado con id: " + idProducto));

        if (!producto.getRestaurante().getIdUsuario().equals(idRestaurante)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tenés permisos para modificar este producto.");
        }

        producto.setDisponible(true);
        productoRepository.save(producto);
    }

    @Modifying
    @Transactional
    public void modificarProducto(Producto producto) {
        productoRepository.save(producto);
    }

}
