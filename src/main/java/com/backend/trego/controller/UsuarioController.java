package com.backend.trego.controller;

import com.backend.trego.config.AuthenticatedUser;
import com.backend.trego.entity.DTOs.DTORegistroRestaurante;
import com.backend.trego.entity.DTOs.DTOUsuario;
import com.backend.trego.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// Endpoints de usuarios: clientes, restaurantes y administradores.
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin("*")
@Tag(name = "Usuarios y Restaurantes", description = "Endpoints para la gestión de cuentas y registro de locales")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Paso 1 del registro: recibe email y contraseña y dispara el código de verificación.
    @PostMapping("/registrar-restaurante/solicitar")
    @Operation(summary = "Paso 1: Solicitar Registro", description = "Recibe el correo y contraseña del restaurante. Valida que no exista el correo y despacha el código de verificación.")
    @ApiResponse(responseCode = "200", description = "Código de verificación enviado con éxito.")
    @ApiResponse(responseCode = "400", description = "Datos inválidos (contraseña débil) o correo ya registrado.")
    public ResponseEntity<String> solicitarRegistro(@Valid @RequestBody DTORegistroRestaurante dto) {
        try {
            usuarioService.iniciarRegistroRestaurante(dto.getEmail(), dto.getPassword());
            return ResponseEntity.ok("Código de verificación enviado al correo electrónico.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }

    // Paso 2: valida el código y da de alta el restaurante.
    @PostMapping("/registrar-restaurante/confirmar")
    @Operation(summary = "Paso 2: Confirmar Código", description = "Verifica el código enviado por email. Si es correcto, registra permanentemente al restaurante deshabilitado en la base de datos.")
    @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente. Retorna el DTO de sesión.")
    @ApiResponse(responseCode = "400", description = "Código inválido o expirado.")
    public ResponseEntity<?> confirmarRegistro(@RequestParam String email, @RequestParam String codigo) {
        try {
            DTOUsuario usuarioDTO = usuarioService.verificarCodigo(email, codigo);
            return ResponseEntity.ok(usuarioDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Reenvía el código cuando el anterior caducó o no llegó.
    @PostMapping("/registrar-restaurante/reenviar-codigo")
    @Operation(summary = "Reenviar Código", description = "Envía un nuevo código de verificación si el anterior expiró o es incorrecto")
    @ApiResponse(responseCode = "200", description = "Código reenviado exitosamente")
    @ApiResponse(responseCode = "400", description = "El tiempo de registro ha expirado o datos inválidos")
    public ResponseEntity<String> reenviarCodigo(@RequestParam String email) {
        try {
            usuarioService.reenviarCodigoVerificacion(email);
            return ResponseEntity.ok("Se ha enviado un nuevo código de verificación a tu correo electrónico.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/obtenerDirecciones")
    @Operation(summary = "Obtener Direcciones", description = "Retorna las direcciones asociadas al cliente autenticado")
    @ApiResponse(responseCode = "200", description = "Direcciones obtenidas exitosamente")
    @ApiResponse(responseCode = "401", description = "No autorizado")
    public ResponseEntity<?> obtenerDirecciones() {
        try {
            return ResponseEntity.ok(usuarioService.obtenerDirecciones());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }
    }

    @GetMapping("/test-auth")
    @Operation(summary = "Prueba de seguridad", description = "Endpoint para verificar que el JWT funciona.")
    public ResponseEntity<String> testAuth() {
        return ResponseEntity.ok("¡El filtro JWT funciona correctamente! Estás autenticado. \n");
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(user);
    }

}
