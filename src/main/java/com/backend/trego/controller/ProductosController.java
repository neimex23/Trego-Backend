package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOIngrediente;
import com.backend.trego.service.ProductosService;
import com.backend.trego.service.RestauranteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Endpoints del catálogo de productos e ingredientes.
@RestController
@RequestMapping("/api/productos")
@CrossOrigin("*")
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
      
}
