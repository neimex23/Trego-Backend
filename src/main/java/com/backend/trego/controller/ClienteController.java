package com.backend.trego.controller;

import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.DTOs.DTOCliente;
import com.backend.trego.entity.DTOs.DTOClienteResponse;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/clientes")
@CrossOrigin("*")
@Tag(name = "Clientes", description = "Gestión de clientes")
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
        var nuevo = service.crear(dto);
        URI location = URI.create("/clientes/" + nuevo.getIdUsuario());
        return ResponseEntity.created(location).body(DTOClienteResponse.desde(nuevo));
    }

    @GetMapping("/actual")
    @Operation(summary = "Obtener restaurante actual", description = "Devuelve los datos del restaurante actualmente autenticado, según el token JWT.")
    @ApiResponse(responseCode = "200", description = "Restaurante autenticado encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado o token sin rol Restaurante")
    @ApiResponse(responseCode = "404", description = "Restaurante autenticado no encontrado")
    public ResponseEntity<DTOCliente> obtenerActual() {
        return ResponseEntity.ok(service.obtenerClienteActual());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener Cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no existe")
    })
    public DTOClienteResponse obtener(@PathVariable Integer id) {
        return DTOClienteResponse.desde(service.obtenerOFallar(id));
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Actualizar Cliente", description = "Actualiza los datos del cliente autenticado con los valores no nulos seteados en la llamada. El ID del cliente se obtiene del token de autenticación,"
            +
            " No Modifica: ID, Habilitado,Direcciones ni Contraseña.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @ApiResponse(responseCode = "404", description = "Cliente no existe")
    })
    public DTOClienteResponse actualizar(@RequestBody DTOCliente dto) {
        return DTOClienteResponse.desde(service.actualizar(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Cliente por ID")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/fcm-token")
    @Operation(summary = "Registrar/actualizar token FCM del dispositivo del cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Token actualizado"),
            @ApiResponse(responseCode = "404", description = "Cliente no existe")
    })
    public ResponseEntity<Void> actualizarFcmToken(@PathVariable Integer id,
            @RequestBody FcmTokenRequest body) {
        service.actualizarFcmToken(id, body != null ? body.token() : null);
        return ResponseEntity.noContent().build();
    }

    public record FcmTokenRequest(String token) {
    }

}
