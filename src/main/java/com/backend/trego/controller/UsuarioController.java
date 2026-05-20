package com.backend.trego.controller;

import com.backend.trego.config.AuthenticatedUser;
import com.backend.trego.entity.DTOs.DTORegistroRestaurante;
import com.backend.trego.entity.DTOs.DTOUsuario;
import com.backend.trego.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints REST para la gestión de Usuarios (clientes, restaurantes y
 * administradores).
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios y Restaurantes", description = "Endpoints para la gestión de cuentas y registro de locales")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint 1: Recibe email y contraseña. Lanza las excepciones automáticas si
     * no cumplen
     * con las anotaciones de validación (Campos vacíos/Contraseña débil).
     */
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

    /**
     * Endpoint 2: Recibe el código y confirma el alta en la Base de Datos.
     */
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

    /**
     * Flujo Alternativo 7.1: El usuario solicita reenviar el
     * código de verificación porque el anterior era inválido o caducó.
     */
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

    @GetMapping("/test-auth")
    @Operation(summary = "Prueba de seguridad", description = "Endpoint para verificar que el JWT funciona.")
    public ResponseEntity<String> testAuth() {
        return ResponseEntity.ok("¡El filtro JWT funciona correctamente! Estás autenticado. \n");
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(@AuthenticationPrincipal AuthenticatedUser user) {
        // 'user' contiene el idUsuario, email, uid y rol que extrajiste en el filtro
        return ResponseEntity.ok(user);
    }

}
