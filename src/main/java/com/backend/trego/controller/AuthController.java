package com.backend.trego.controller;

import com.backend.trego.config.AuthenticatedUser;
import com.backend.trego.entity.Usuario;
import com.backend.trego.entity.DTOs.DTOUsuario;
import com.backend.trego.entity.DTOs.DTOLogin;
import com.backend.trego.entity.DTOs.DTOLoginResponse;
import com.backend.trego.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@Tag(name = "Autenticación", description = "Login y registro de clientes, restaurantes y administradores. Emisión y revocación de JWT.")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Login de admin / restaurante (usuario y contraseña)
    @PostMapping("/login/admin")
    @Operation(summary = "Login de administrador o restaurante",
            description = "Autentica con email y contraseña. Devuelve el JWT y el DTO del usuario autenticado. Usado por administradores y restaurantes; los clientes ingresan por Google o SMS.")
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    @ApiResponse(responseCode = "403", description = "Usuario deshabilitado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<?> login(@RequestBody DTOLogin loginDTO) {
        try {
            DTOLoginResponse response = authService.login(loginDTO);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación \n");
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario Deshabilitado \n");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor \n");
        }
    }

    // Login de cliente con Google
    @PostMapping("/google")
    @Operation(summary = "Login de cliente con Google",
            description = "Recibe el idToken emitido por Firebase tras la autenticación con Google y lo valida contra Firebase Admin. Si el usuario no existe en Trego, queda a cargo del endpoint de registro. El body debe contener la clave 'idToken'.")
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa")
    @ApiResponse(responseCode = "401", description = "Token inválido")
    @ApiResponse(responseCode = "403", description = "Usuario deshabilitado")
    @ApiResponse(responseCode = "501", description = "Proveedor de identidad no soportado")
    public ResponseEntity<?> loginConFirebase(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");
        try {
            DTOLoginResponse response = authService.loginConGoogle(idToken);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación \n");
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario Deshabilitado \n");
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(e.getMessage());
        }
    }
    
    // Login de cliente por SMS
    @PostMapping("/sms")
    @Operation(summary = "Login de cliente por SMS",
            description = "Recibe el firebaseToken obtenido tras la verificación del código SMS en el cliente y lo valida contra Firebase Admin. El body debe contener la clave 'firebaseToken'.")
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa")
    @ApiResponse(responseCode = "401", description = "Token SMS inválido")
    @ApiResponse(responseCode = "403", description = "Usuario deshabilitado")
    @ApiResponse(responseCode = "501", description = "Proveedor de identidad no soportado")
    public ResponseEntity<?> loginConSMS(@RequestBody Map<String, String> body) {
        String firebaseToken = body.get("firebaseToken");
        try {
            DTOLoginResponse response = authService.loginConSMS(firebaseToken);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación SMS \n");
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario Deshabilitado SMS \n");
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(e.getMessage());
        }
    }
    
    
    @GetMapping("/test-auth")
    @Operation(summary = "Prueba de seguridad", description = "Endpoint para verificar que el JWT funciona. Devuelve el principal autenticado.")
    public ResponseEntity<?> testAuth(@AuthenticationPrincipal AuthenticatedUser user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("No hay usuario autenticado en el contexto de seguridad.");
        }
        return ResponseEntity.ok(Map.of(
                "mensaje", "El filtro JWT funciona correctamente. Estás autenticado.",
                "usuario", user));
    }

    @PostMapping("/cerrarSesion")
    @Operation(summary = "Cerrar sesión",
            description = "Invalida el JWT del header Authorization registrándolo en la lista de tokens revocados. Espera el header 'Authorization: Bearer <token>'.")
    @ApiResponse(responseCode = "200", description = "Sesión cerrada correctamente")
    @ApiResponse(responseCode = "400", description = "Token no proporcionado o formato inválido")
    public ResponseEntity<?> cerrarSesion(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionado o formato inválido \n");
        }

        String token = authHeader.substring(7).trim();
        authService.cerrarSesion(token);
        
        return ResponseEntity.ok("Sesión cerrada exitosamente");
    }

    // FLUJO CU-CLI-01: Registro de Cliente (Google o SMS)
    @PostMapping("/registro")
    @Operation(summary = "Registro de cliente",
            description = "Da de alta un cliente que se autenticó previamente con Google o SMS. Corresponde al caso de uso CU-CLI-01. Devuelve 201 con la URI del recurso creado.")
    @ApiResponse(responseCode = "201", description = "Cliente registrado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya registrado")
    @ApiResponse(responseCode = "500", description = "Error interno al registrar")
    public ResponseEntity<?> registrarCliente(@RequestBody DTOUsuario dto) {
        try {
            Usuario usuario = authService.altaUsuario(dto);
            URI location = URI.create("/api/auth/registro/" + usuario.getIdUsuario());
            return ResponseEntity.created(location).build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al registrar cliente");
        }
    }
}
