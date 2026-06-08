package com.backend.trego.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.trego.entity.DTOs.DTOCrearReclamoRequest;
import com.backend.trego.entity.DTOs.DTOReclamo;
import com.backend.trego.entity.DTOs.DTOResolverReclamoRequest;
import com.backend.trego.entity.Enums.EnumEstadoReclamo;
import com.backend.trego.service.ReclamoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// Endpoints de gestión de reclamos para el restaurante autenticado.
@RestController
@RequestMapping("/api/reclamos")
@CrossOrigin("*")
@Tag(name = "Reclamos", description = "Gestión de reclamos del restaurante")
public class ReclamoController {

    private final ReclamoService reclamoService;

    public ReclamoController(ReclamoService reclamoService) {
        this.reclamoService = reclamoService;
    }

    @PostMapping
    @Operation(
        summary = "Crear reclamo",
        description = "Registra un reclamo sobre un pedido del cliente. "
                + "No aplica a pedidos Pagado, Cancelado o Reembolsado. "
                + "El pedido no debe tener un reclamo previo."
    )
    @ApiResponse(responseCode = "201", description = "Reclamo creado")
    @ApiResponse(responseCode = "400", description = "Estado del pedido no permite reclamo")
    @ApiResponse(responseCode = "403", description = "El pedido no pertenece al cliente autenticado")
    @ApiResponse(responseCode = "404", description = "Pedido o cliente no encontrado")
    @ApiResponse(responseCode = "409", description = "El pedido ya tiene un reclamo")
    public ResponseEntity<DTOReclamo> crear(@Valid @RequestBody DTOCrearReclamoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reclamoService.crearReclamo(request));
    }


    @GetMapping
    @Operation(
        summary = "Listar reclamos",
        description = "Devuelve los reclamos del restaurante autenticado. "
                + "Admite filtros opcionales por nombre de usuario, estado y rango de fechas."
    )
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<DTOReclamo>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) EnumEstadoReclamo estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {

        return ResponseEntity.ok(reclamoService.listarReclamos(nombre, estado, fechaDesde, fechaHasta));
    }

    
    @PutMapping("/{idReclamo}/resolver")
    @Operation(
        summary = "Resolver reclamo",
        description = "Acepta (estado → Resuelto) o rechaza (estado → Rechazado) un reclamo. "
                + "Al aceptar, el reintegro del dinero debe iniciarse desde el front. "
                + "Al rechazar, se requiere motivoRechazo. "
                + "En ambos casos se notifica al cliente por email y push."
    )
    @ApiResponse(responseCode = "200", description = "Reclamo resuelto")
    @ApiResponse(responseCode = "400", description = "Acción inválida o reclamo ya resuelto")
    @ApiResponse(responseCode = "404", description = "Reclamo no encontrado o no pertenece a este restaurante")
    public ResponseEntity<DTOReclamo> resolver(
            @PathVariable Integer idReclamo,
            @Valid @RequestBody DTOResolverReclamoRequest request) {

        return ResponseEntity.ok(reclamoService.resolverReclamo(idReclamo, request));
    }
}
