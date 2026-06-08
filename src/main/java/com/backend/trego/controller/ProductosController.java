package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOIngrediente;
import com.backend.trego.entity.DTOs.DTOProducto;
import com.backend.trego.service.ProductosService;
import com.backend.trego.service.RestauranteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

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
    public ResponseEntity<DTOIngrediente> agregarIngrediente(@PathVariable String nombre){
        return ResponseEntity.ok(restauranteService.crearIngrediente(nombre));
    }

    @GetMapping("/listarProductos")
    @Operation(summary = "Listar Productos", description = "Obtiene todos los productos disponibles del restaurante actualmente logueado")
    @ApiResponse(responseCode = "200", description = "Productos Listados")
    @ApiResponse(responseCode = "404", description = "No hay productos para el restaurante o el restaurante no existe")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol Restaurante")
    public ResponseEntity<List<DTOProducto>> listarProductos(){
        return ResponseEntity.ok(productosService.listarProductos(null, true));
    }

    @GetMapping("/listarProductosHabilitados")
    @Operation(summary = "Listar Productos Habilitados", description = "Obtiene solo los productos disponibles (habilitados) del restaurante actualmente logueado")
    @ApiResponse(responseCode = "200", description = "Productos listados")
    @ApiResponse(responseCode = "404", description = "No hay productos habilitados para el restaurante")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol Restaurante")
    public ResponseEntity<List<DTOProducto>> listarProductosHabilitados() {
        return ResponseEntity.ok(productosService.listarSoloProductosHabilitados(null, true));
    }

    @PostMapping("agregarProducto")
    @Operation(summary = "Agregar Producto", description = "Agrega un nuevo producto al restaurante actualmente logueado")
    @ApiResponse(responseCode = "200", description = "Producto agregado correctamente")
    @ApiResponse(responseCode = "404", description = "Restaurante no existe")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol Restaurante")
    public ResponseEntity<DTOProducto> agregarProducto(@RequestBody DTOProducto producto){
        return ResponseEntity.ok(productosService.crearProducto(producto));
    }

    @PatchMapping("/{idProducto}/deshabilitar")
    @Operation(summary = "Deshabilitar Producto", description = "Deshabilita un producto del restaurante actualmente logueado (disponible = false)")
    @ApiResponse(responseCode = "200", description = "Producto deshabilitado correctamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol Restaurante o el producto no le pertenece")
    public ResponseEntity<Void> deshabilitarProducto(@PathVariable Integer idProducto) {
        productosService.deshabilitarProducto(idProducto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{idProducto}/habilitar")
    @Operation(summary = "Habilitar Producto", description = "Vuelve a habilitar un producto del restaurante actualmente logueado (disponible = true)")
    @ApiResponse(responseCode = "200", description = "El producto vuelve a estar disponible")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol Restaurante o el producto no le pertenece")
    public ResponseEntity<Void> habilitarProducto(@PathVariable Integer idProducto) {
        productosService.habilitarProducto(idProducto);
        return ResponseEntity.ok().build();
    }

}