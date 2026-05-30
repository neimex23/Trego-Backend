package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOIngrediente;
import com.backend.trego.entity.DTOs.DTOProducto;
import com.backend.trego.entity.DTOs.DTOSubCategoria;
import com.backend.trego.entity.Enums.EnumCategoriaProducto;
import com.backend.trego.entity.Enums.EnumTipoProducto;
import com.backend.trego.service.ProductosService;
import com.backend.trego.service.RestauranteService;
import com.backend.trego.service.SubCategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Table;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Endpoints del catálogo de productos e ingredientes.
@RestController
@RequestMapping("/api/productos")
@CrossOrigin("*")
@Tag(name = "Productos", description = "Gestión del catálogo de productos e ingredientes de cada restaurante. Solo accesible para restaurantes autenticados.")
public class ProductosController {

    private final ProductosService productosService;
    private final RestauranteService restauranteService;

    public ProductosController(ProductosService productosService, RestauranteService restauranteService) {
        this.productosService = productosService;
        this.restauranteService = restauranteService;
    }

        //CU Alta Plato
    @GetMapping("/listarIngredientes")
    @Operation(summary = "Listar Ingredientes", description = "Obtiene todos los ingredientes disponibles del restaurante actualmente loegueado")
    @ApiResponse(responseCode = "200", description = "Ingredientes Listados")
    @ApiResponse(responseCode = "404", description = "Restaurante no existe")

    public ResponseEntity<List<DTOIngrediente>> listarIngredientes(){
        return ResponseEntity.ok(restauranteService.obtenerIngredientesDisponibles());
    }

    @PostMapping("/agregarIngrediente/{nombre}")
    @Operation(summary = "Agrega un Nuevo Ingrediente", description = "Agrega un nuevo ingrediente disponible con nombre al restaurante actualmente logueado")
    @ApiResponse(responseCode = "200", description = "Ingrediente Agregado correctamente")
    @ApiResponse(responseCode = "404", description = "Restaurante no existe")
    @ApiResponse(responseCode = "409", description = "Ingrediente ya existe")
    public ResponseEntity<Void> agregarIngrediente(@PathVariable String nombre){
        restauranteService.crearIngrediente(nombre);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/listarProductos")
    @Operation(summary = "Listar Productos", description = "Obtiene todos los productos disponibles del restaurante actualmente logueado")
    @ApiResponse(responseCode = "200", description = "Productos Listados")
    @ApiResponse(responseCode = "404", description = "No hay productos para el restaurante o el restaurante no existe")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol Restaurante")
    public ResponseEntity<List<DTOProducto>> listarProductos(){
        return ResponseEntity.ok(productosService.listarProductos(null, true));
    }

    @PostMapping("agregarProducto")
    @Operation(summary = "Agregar Producto", description = "Agrega un nuevo producto al restaurante actualmente logueado")
    @ApiResponse(responseCode = "200", description = "Producto agregado correctamente")
    @ApiResponse(responseCode = "404", description = "Restaurante no existe")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol Restaurante")
    public ResponseEntity<Void> agregarProducto(@RequestBody DTOProducto producto){
        productosService.crearProducto(producto);
        return ResponseEntity.ok().build();
    }

}