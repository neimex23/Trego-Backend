package com.backend.trego.controller;

import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Enums.EnumRoles;
import com.backend.trego.entity.DTOs.DTOCliente;
import com.backend.trego.entity.DTOs.DTOClienteResponse;
import com.backend.trego.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar Todos los Clientes")
    public List<DTOClienteResponse> listar() {
        return service.listar().stream()
                .map(DTOClienteResponse::desde)
                .toList();
    }

    @PostMapping
    @Operation(summary = "Crear cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<DTOClienteResponse> crear(@RequestBody DTOCliente dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()
                || dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nombre y email son obligatorios");
        }

        Cliente nuevo = service.guardar(new Cliente(
                dto.getNombre(),
                dto.getEmail(),
                dto.getUrlImagen(),
                EnumRoles.Cliente,
                dto.getUidCliente(),
                dto.getTelefono(),
                dto.getDirecciones()
        ));

        URI location = URI.create("/clientes/" + nuevo.getIdUsuario());

        return ResponseEntity.created(location).body(DTOClienteResponse.desde(nuevo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener Cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no existe")
    })
    public DTOClienteResponse obtener(@PathVariable Integer id) {
        Cliente cliente = service.obtener(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        return DTOClienteResponse.desde(cliente);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @ApiResponse(responseCode = "404", description = "Cliente no existe")
    })
    public DTOClienteResponse actualizar(@PathVariable Integer id, @RequestBody DTOCliente dto) {
        Cliente datos = new Cliente(
                dto.getNombre(),
                dto.getEmail(),
                dto.getUrlImagen(),
                EnumRoles.Cliente,
                dto.getUidCliente(),
                dto.getTelefono(),
                dto.getDirecciones()
        );
        return DTOClienteResponse.desde(service.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Cliente por ID")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.obtener(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
