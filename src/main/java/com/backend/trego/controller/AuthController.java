package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.LoginDTO;
import com.backend.trego.entity.DTOs.LoginResponseDTO;
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

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Login de admin / restaurante (usuario y contraseña)
    @PostMapping("/login/admin")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
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

    // Login de cliente con Google
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
    
    // Login de cliente por SMS
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