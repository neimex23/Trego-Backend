package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.service.GeoapifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geo")
@CrossOrigin("*")
@Tag(name = "Geocoding", description = "Resolución de coordenadas a dirección legible")
public class GeoController {

    private final GeoapifyService geoapifyService;

    public GeoController(GeoapifyService geoapifyService) {
        this.geoapifyService = geoapifyService;
    }

    @GetMapping("/reverse")
    @Operation(summary = "Reverse geocoding", description = "Convierte lat/lon en calle y etiqueta usando Geoapify del servidor")
    // 404 si Geoapify no devuelve calle ni tag utilizables.
    public ResponseEntity<DTODireccion> reverse(
            @RequestParam double lat,
            @RequestParam double lon) {
        DTODireccion direccion = geoapifyService.obtenerDireccion(lat, lon);
        if (direccion == null || !tieneTextoUtil(direccion)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(direccion);
    }

    private static boolean tieneTextoUtil(DTODireccion d) {
        return (d.getCalle() != null && !d.getCalle().isBlank())
                || (d.getTag() != null && !d.getTag().isBlank());
    }
}
