package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOUsuario;
import com.backend.trego.service.AdministradorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Endpoints del administrador autenticado.
@RestController
@RequestMapping("/api/administradores")
@CrossOrigin("*")
@Tag(name = "Administradores", description = "Consulta y gestión del administrador autenticado")
public class AdministradorController {

    private final AdministradorService administradorService;

    public AdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
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
}
