package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.service.RestauranteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoints de restaurantes.
@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin("*")
@Tag(name = "Restaurantes", description = "Listado y consulta de restaurantes para el cliente")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    // CU-CLI: Listar restaurantes registrados. Si se pasa 'nombre' se filtra por
    // coincidencia parcial; si no, devuelve todos los habilitados.
    @GetMapping("/listar")
    @Operation(summary = "Listar restaurantes", description = "Devuelve los restaurantes habilitados. Acepta un filtro opcional por nombre.")
    @ApiResponse(responseCode = "200", description = "Listado de restaurantes obtenido")
    public ResponseEntity<List<DTORestaurante>> listar(@RequestParam(required = false) String nombre) {
        List<DTORestaurante> restaurantes = (nombre == null || nombre.isBlank())
                ? restauranteService.listarRestaurantes()
                : restauranteService.buscarRestaurantePorNombre(nombre);
        return ResponseEntity.ok(restaurantes);
    }

    @PostMapping("/listarXdirreccion")
    @Operation(summary = "Listar restaurantes dado una direccion", description = "Lista todos los restaurantes con cobertura segun la dirrecion provista")
    @ApiResponse(responseCode = "200", description =  "Listado de restaurantes obtenido")
    @ApiResponse(responseCode = "404", description = "Ningun restaurante obtenido")
    public ResponseEntity<List<DTORestaurante>> listarRestaurantesDirreccion (@RequestBody DTODireccion dirreccionBusqueda){
        return ResponseEntity.ok(restauranteService.listarRestaurantesZona(dirreccionBusqueda));
    }

    // Devuelve los datos del restaurante autenticado (extraídos del JWT).
    @GetMapping("/actual")
    @Operation(summary = "Obtener restaurante actual", description = "Devuelve los datos del restaurante actualmente autenticado, según el token JWT.")
    @ApiResponse(responseCode = "200", description = "Restaurante autenticado encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado o token sin rol Restaurante")
    @ApiResponse(responseCode = "404", description = "Restaurante autenticado no encontrado")
    public ResponseEntity<DTORestaurante> obtenerActual() {
        return ResponseEntity.ok(restauranteService.obtenerRestauranteActual());
    }

    // CU-CLI: Ver datos de un restaurante puntual (sin el menú).
    @GetMapping("obtenerRestaurante/{id}")
    @Operation(summary = "Obtener restaurante", description = "Devuelve los datos públicos de un restaurante por id.")
    @ApiResponse(responseCode = "200", description = "Restaurante encontrado")
    @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    public ResponseEntity<DTORestaurante> obtener(@PathVariable String id) {
        return ResponseEntity.ok(restauranteService.obtenerRestaurante(id));
    }

    // Actualizar datos de un restaurante. Sólo se aplican los campos no nulos del
    // DTO; id y habilitado no se modifican.
    @PatchMapping("/actualizar")
    @Operation(summary = "Actualizar restaurante", description = "Actualiza los datos del restaurante actualmente logeado. Sólo se modifican los campos no nulos del DTO. No se puede cambiar id ni habilitado.")
    @ApiResponse(responseCode = "200", description = "Restaurante actualizado")
    @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    public ResponseEntity<DTORestaurante> actualizar(@RequestBody DTORestaurante dto) {
        return ResponseEntity.ok(restauranteService.actualizarRestaurante(dto));
    }

}
