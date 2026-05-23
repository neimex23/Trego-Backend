package com.backend.trego.controller;

import com.backend.trego.entity.Usuario;
import com.backend.trego.entity.DTOs.DTOUsuario;
import com.backend.trego.entity.DTOs.DTOLogin;
import com.backend.trego.entity.DTOs.DTOLoginResponse;
import com.backend.trego.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Login de admin / restaurante (usuario y contraseña)
    @PostMapping("/login/admin")
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
    
    @PostMapping("/cerrarSesion")
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
