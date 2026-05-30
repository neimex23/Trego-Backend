package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOSubCategoria;
import com.backend.trego.entity.Enums.EnumCategoriaProducto;
import com.backend.trego.service.SubCategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Endpoints del catálogo global de subcategorías. Se exponen para que el restaurante
// pueda elegir una al dar de alta un producto.
@RestController
@RequestMapping("/api/subcategorias")
@CrossOrigin("*")
@Tag(name = "SubCategorias", description = "Catálogo global de subcategorías de productos.")
public class SubCategoriaController {

    private final SubCategoriaService subCategoriaService;

    public SubCategoriaController(SubCategoriaService subCategoriaService) {
        this.subCategoriaService = subCategoriaService;
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar subcategorías", description = "Devuelve todas las subcategorías existentes.")
    @ApiResponse(responseCode = "200", description = "Subcategorías listadas")
    public ResponseEntity<List<DTOSubCategoria>> listar() {
        return ResponseEntity.ok(subCategoriaService.listar());
    }

    @GetMapping("/listarPorCategoria/{categoria}")
    @Operation(summary = "Listar subcategorías por categoría",
            description = "Devuelve las subcategorías que pertenecen a una categoría madre.")
    @ApiResponse(responseCode = "200", description = "Subcategorías listadas")
    public ResponseEntity<List<DTOSubCategoria>> listarPorCategoria(@PathVariable EnumCategoriaProducto categoria) {
        return ResponseEntity.ok(subCategoriaService.listarPorCategoria(categoria));
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear subcategoría",
            description = "Da de alta una nueva subcategoría en el catálogo global.")
    @ApiResponse(responseCode = "200", description = "Subcategoría creada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "Ya existe una subcategoría con ese nombre")
    public ResponseEntity<DTOSubCategoria> crear(@RequestBody DTOSubCategoria dto) {
        return ResponseEntity.ok(subCategoriaService.crear(dto));
    }
}
