package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.LoginDTO;
import com.backend.trego.entity.DTOs.LoginResponseDTO;
import com.backend.trego.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException; // Clase nativa de Spring
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // FLUJO 1 Administrador/Restaurante

    @PostMapping("/login/admin")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            LoginResponseDTO response = authService.login(loginDTO);
            return ResponseEntity.ok(response); // 200 OK

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación"); // 401

        } catch (DisabledException e) {
            // Captura la excepción nativa y mapea el 403 con el mensaje exacto de la UI
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario Deshabilitado"); // 403
        }
    }

    // FLUJO 2 Cliente mediante Google

    @PostMapping("/google")
    public ResponseEntity<?> loginConFirebase(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");
        try {
            LoginResponseDTO response = authService.loginConGoogle(idToken);
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación");

        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario Deshabilitado");

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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación");
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario Deshabilitado");
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(e.getMessage());
        }
    }
}