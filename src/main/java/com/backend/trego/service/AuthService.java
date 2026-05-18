package com.backend.trego.service;

import com.backend.trego.entity.Administrador;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.repository.AdministradorRepository;
import com.backend.trego.repository.RestauranteRepository;
import com.backend.trego.config.JWTUtil;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final AdministradorRepository adminRepo;
    private final RestauranteRepository restauranteRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public AuthService(AdministradorRepository adminRepo, RestauranteRepository restauranteRepo, 
                       PasswordEncoder passwordEncoder, JWTUtil jwtUtil) {
        this.adminRepo = adminRepo;
        this.restauranteRepo = restauranteRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, String> authenticate(String email, String password) {
        // 1. Intentar validar si el usuario corresponde a un Administrador
        Optional<Administrador> adminOpt = adminRepo.findByEmail(email);
        if (adminOpt.isPresent()) {
            Administrador admin = adminOpt.get();
            if (passwordEncoder.matches(password, admin.getPassword())) {
                String token = jwtUtil.generateToken(admin.getEmail(), admin.getRol().name());
                
                Map<String, String> response = new HashMap<>();
                response.put("jwtToken", token);
                response.put("rol", admin.getRol().name());
                response.put("nombre", admin.getNombre());
                return response;
            }
        }

        // 2. Intentar validar si el usuario corresponde a un Restaurante
        Optional<Restaurante> restOpt = restauranteRepo.findByEmail(email);
        if (restOpt.isPresent()) {
            Restaurante rest = restOpt.get();
            
            // Verificación del atributo de habilitación (Restricción RU-12 del Modelo de Dominio)
            if (!rest.isHabilitado()) {
                throw new RuntimeException("Usuario Deshabilitado");
            }

            if (passwordEncoder.matches(password, rest.getPassword())) {
                String token = jwtUtil.generateToken(rest.getEmail(), rest.getRol().name());
                
                Map<String, String> response = new HashMap<>();
                response.put("jwtToken", token);
                response.put("rol", rest.getRol().name());
                response.put("nombre", rest.getNombre());
                return response;
            }
        }

        // 3. Excepción en caso de credenciales inválidas (BadCredentialsException)
        throw new BadCredentialsException("Credenciales inválidas");
    }
}