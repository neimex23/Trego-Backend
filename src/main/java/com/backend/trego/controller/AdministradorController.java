package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.entity.DTOs.DTOUsuario;
import com.backend.trego.service.AdministradorService;
import com.backend.trego.service.RestauranteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Endpoints del administrador autenticado.
@RestController
@RequestMapping("/api/administradores")
@CrossOrigin("*")
@Tag(name = "Administradores", description = "Consulta y gestión del administrador autenticado")
public class AdministradorController {

    private final AdministradorService administradorService;
    private final RestauranteService restauranteService;

    public AdministradorController(AdministradorService administradorService, RestauranteService restauranteService) {
        this.administradorService = administradorService;
        this.restauranteService = restauranteService;
    }

    // Devuelve los datos del administrador autenticado (extraídos del JWT).
    @GetMapping("/actual")
    @Operation(summary = "Obtener administrador actual", description = "Devuelve los datos del administrador actualmente autenticado, según el token JWT.")
    @ApiResponse(responseCode = "200", description = "Administrador autenticado encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no es administrador")
    @ApiResponse(responseCode = "404", description = "Administrador autenticado no encontrado")
    public ResponseEntity<DTOUsuario> obtenerActual() {
        return ResponseEntity.ok(administradorService.obtenerAdministradorActual());
    }

    //CU AltaRestaurante
    @GetMapping("/restaurantes/lista")
    @Operation(summary = "Listar restaurantes en espera", description = "Devuelve los restaurantes que están registrados pero no habilitados, es decir, en espera de aprobación por el administrador.")
    @ApiResponse(responseCode = "200", description = "Listado de restaurantes en espera")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no es administrador")
    public ResponseEntity<List<DTORestaurante>> listarRestaurantesEnEspera() {
        List<DTORestaurante> restaurantesEnEspera = restauranteService.listarRestaurantesNoHabilitados();
        return ResponseEntity.ok(restaurantesEnEspera); 
    }

    @PutMapping("/restaurantes/{id}/habilitar")
    @Operation(summary = "Habilitar restaurante", description = "Habilita un restaurante que está en espera de aprobación por el administrador.")
    @ApiResponse(responseCode = "200", description = "Restaurante habilitado")
    @ApiResponse(responseCode = "400", description = "Restaurante ya habilitado")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no es administrador")
    @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    public ResponseEntity<Void> habilitarRestaurante(@PathVariable Integer id) {
        restauranteService.habilitarRestaurante(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/restaurantes/{id}/noHabilitar/{motivo}")
    @Operation(summary = "No Habilitar restaurante", description = "Estableciendo Motivo, el Sistema envia una notificacion al Restaurante indicando el motivo de no habilitacion")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no es administrador")
    @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    public ResponseEntity<Void> noHabilitarRestaurante(@PathVariable Integer id, @PathVariable String motivo) {
        restauranteService.noHabilitarRestaurante(id, motivo);
        return ResponseEntity.ok().build();
    }
}