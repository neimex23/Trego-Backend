package com.backend.trego.controller;

import com.backend.trego.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // Solo necesitamos acoplar el servicio. Él se encarga de hablar con la BD.
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    // FLUJO 1: Administrador / Restaurante
    // Le agregamos la anotación @PostMapping correspondiente que le faltaba al método viejo
    @PostMapping("/login/admin")
    public ResponseEntity<?> loginAdmin(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        try {
            // Llamamos a la lógica que reestructuramos de forma segura en el Service
            LoginResponseDTO response = authService.login(loginDTO);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación \n");
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario Deshabilitado \n");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    // FLUJO 2: Cliente mediante Google
    @PostMapping("/google")
    public ResponseEntity<?> loginConFirebase(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");
        try {
            LoginResponseDTO response = authService.loginConGoogle(idToken);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación \n");
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario Deshabilitado \n");
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(e.getMessage());
        }
    }
    
    // FLUJO 3: Cliente mediante SMS
    @PostMapping("/sms")
    public ResponseEntity<?> loginConSMS(@RequestBody Map<String, String> body) {
        String firebaseToken = body.get("firebaseToken");
        try {
            LoginResponseDTO response = authService.loginConSMS(firebaseToken);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación SMS \n");
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario Deshabilitado SMS \n");
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(e.getMessage());
        }
    }
}
