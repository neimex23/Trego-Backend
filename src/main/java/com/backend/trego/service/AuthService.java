package com.backend.trego.service;

import com.backend.trego.entity.Usuario;
import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Enums.EnumRoles;
import com.backend.trego.entity.Administrador;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.DTOs.LoginDTO;
import com.backend.trego.entity.DTOs.LoginResponseDTO;

import com.backend.trego.repository.UsuarioRepository;
import com.backend.trego.repository.ClienteRepository;
import com.backend.trego.repository.AdministradorRepository;
import com.backend.trego.repository.RestauranteRepository;
import com.backend.trego.config.JWTUtil;
import com.backend.trego.entity.DTOs.DTDireccion;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException; // Clase nativa de Spring
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Imports necesarios para Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service

public class AuthService {

    private final AdministradorRepository adminRepo;
    private final RestauranteRepository restauranteRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final UsuarioRepository usuarioRepo;
    private final ClienteRepository clienteRepo;
    public AuthService(AdministradorRepository adminRepo, RestauranteRepository restauranteRepo, 
                       PasswordEncoder passwordEncoder, JWTUtil jwtUtil, UsuarioRepository usuarioRepo, ClienteRepository clienteRepo) {
        this.adminRepo = adminRepo;
        this.restauranteRepo = restauranteRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.usuarioRepo = usuarioRepo;
        this.clienteRepo = clienteRepo;
    }
    
    //FLUJO 1 de Administrador / Restaurante
     
    public LoginResponseDTO login(LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();
        // 1 Intenta validar Administrador si no sale se jode todo
        Optional<Administrador> adminOpt = adminRepo.findByEmail(email);
        if (adminOpt.isPresent()) {
            Administrador admin = adminOpt.get();
            
            if (!passwordEncoder.matches(password, admin.getPassword())) {
                throw new BadCredentialsException("Error de autenticación");
            }
            
            String token = jwtUtil.generateToken(admin.getEmail(), admin.getRol().name());
            return new LoginResponseDTO(token, admin.getRol().name(), admin.getNombre(), admin.getEmail());
        }

        // 2 Intenta validar a un Restaurante
        Optional<Restaurante> restOpt = restauranteRepo.findByEmail(email);
        if (restOpt.isPresent()) {
            Restaurante rest = restOpt.get();
            // Rutina de control de estado usando la excepcion de Spring Security. Pero se pude crear clases personalizadas.
            if (!rest.isHabilitado()) {
                throw new DisabledException("Usuario Deshabilitado");
            }
            if (!passwordEncoder.matches(password, rest.getPassword())) {
                throw new BadCredentialsException("Error de autenticación");
            }
            String token = jwtUtil.generateToken(rest.getEmail(), rest.getRol().name());
            return new LoginResponseDTO(token, rest.getRol().name(), rest.getNombre(), rest.getEmail());
        }

        // 3 Si no coincide el email lanza excepcion
        throw new BadCredentialsException("Error de autenticación");
    }
    
/**
     * FLUJO 2: Cliente mediante Google
     */
    public LoginResponseDTO loginConGoogle(String idToken) {
        try {
            // 1. Validar rigurosamente el token contra Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String nombre = (String) decodedToken.getClaims().get("name");
            String foto = (String) decodedToken.getClaims().get("picture");

            System.out.println(">>> [Firebase Auth] Token de Google válido. UID: " + uid);

            // 2. Buscar si el usuario ya existe en nuestra BD local
            Optional<Usuario> usuarioOpt = usuarioRepo.findByFirebaseUid(uid);
            Usuario usuario;

            if (usuarioOpt.isPresent()) {
                usuario = usuarioOpt.get();
                System.out.println(">>> [Auth] Cliente recurrente detectado: " + usuario.getEmail());
            } else {
                // 3. REGISTRO AUTOMÁTICO EN PRIMER LOGIN
                System.out.println(">>> [Auth] Cliente nuevo detectado. Registrando de forma automática...");
                List<DTDireccion> direccionesVaciasGoogle = new ArrayList<>();
                Cliente nuevoCliente = new Cliente(nombre, email, foto, EnumRoles.Cliente, uid, null, direccionesVaciasGoogle);
                usuario = clienteRepo.save(nuevoCliente);
            }

            // 4. Firmar el JWT local de Trego y retornar la respuesta de sesión
            String tokenLocal = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().name());
            return new LoginResponseDTO(tokenLocal, usuario.getRol().name(), usuario.getNombre(), usuario.getEmail());

        } catch (FirebaseAuthException e) {
            System.err.println(">>> [Firebase Auth ERROR] Token de Google inválido: " + e.getMessage());
            throw new BadCredentialsException("Error de autenticación: Token federado inválido");
        }
    }

/**
     * FLUJO 3: Cliente mediante SMS
     */
    public LoginResponseDTO loginConSMS(String firebaseToken) {
        try {
            // 1. Validar el token telefónico contra Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            
            String uid = decodedToken.getUid();
            String telefono = (String) decodedToken.getClaims().get("phone_number"); 

            System.out.println(">>> [Firebase Auth] Token de SMS válido. UID: " + uid);

            // 2. Buscar si el usuario ya existe localmente
            Optional<Usuario> usuarioOpt = usuarioRepo.findByFirebaseUid(uid);
            Usuario usuario;

            if (usuarioOpt.isPresent()) {
                usuario = usuarioOpt.get();
                System.out.println(">>> [Auth] Cliente por SMS recurrente detectado: " + usuario.getTelefono());
            } else {
                // 3. REGISTRO AUTOMÁTICO EN PRIMER LOGIN (Sin email inicial)
                System.out.println(">>> [Auth] Cliente SMS nuevo detectado. Registrando de forma automática...");
                List<DTDireccion> direccionesVaciasSMS = new ArrayList<>();
                String nombrePorDefecto = "Usuario SMS " + uid.substring(0, 5);
                Cliente nuevoCliente = new Cliente(nombrePorDefecto, null, null, EnumRoles.Cliente, uid, telefono, direccionesVaciasSMS);
                usuario = clienteRepo.save(nuevoCliente);
            }

            // 4. Firmar el JWT local usando el teléfono o identificador como sujeto
            String sujetoToken = (usuario.getEmail() != null) ? usuario.getEmail() : usuario.getTelefono();
            String tokenLocal = jwtUtil.generateToken(sujetoToken, usuario.getRol().name());
            
            return new LoginResponseDTO(tokenLocal, usuario.getRol().name(), usuario.getNombre(), sujetoToken);

        } catch (FirebaseAuthException e) {
            System.err.println(">>> [Firebase Auth ERROR] Token de SMS inválido: " + e.getMessage());
            throw new BadCredentialsException("Error de autenticación: Token federado inválido");
        }
    }
}