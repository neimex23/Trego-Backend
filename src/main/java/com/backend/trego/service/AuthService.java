package com.backend.trego.service;

import com.backend.trego.entity.Usuario;
import com.backend.trego.entity.Enums.EnumRoles;
import com.backend.trego.entity.Administrador;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.DTOs.LoginDTO;
import com.backend.trego.entity.DTOs.LoginResponseDTO;
import com.backend.trego.entity.DTOs.DTOUsuario; 

import com.backend.trego.repository.UsuarioRepository;
import com.backend.trego.repository.AdministradorRepository;
import com.backend.trego.repository.RestauranteRepository;
import com.backend.trego.config.JWTUtil;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.FirebaseAuthException;

import com.backend.trego.service.TokenBlacklistService;

import java.util.Optional;

@Service
public class AuthService {

    private final AdministradorRepository adminRepo;
    private final RestauranteRepository restauranteRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    
    private final UsuarioRepository usuarioRepository; 
    private final UsuarioService usuarioService;
	private final TokenBlacklistService tokenBlacklistService;

    public AuthService(AdministradorRepository adminRepo, 
                       RestauranteRepository restauranteRepo, 
                       PasswordEncoder passwordEncoder, 
                       JWTUtil jwtUtil, 
                       UsuarioRepository usuarioRepository, 
                       UsuarioService usuarioService,
                       TokenBlacklistService tokenBlacklistService) {
        this.adminRepo = adminRepo;
        this.restauranteRepo = restauranteRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    // Login de admin / restaurante con email y contraseña.
    public LoginResponseDTO login(LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();
        
        Optional<Administrador> adminOpt = adminRepo.findByEmail(email);
        if (adminOpt.isPresent()) {
            Administrador admin = adminOpt.get();
            
            if (!passwordEncoder.matches(password, admin.getPassword())) {
                throw new BadCredentialsException("Error de autenticación");
            }
            
            String token = jwtUtil.generateToken(admin.getEmail(), admin.getRol().name());
            return new LoginResponseDTO(token, admin.getRol().name(), admin.getNombre(), admin.getEmail());
        }

        Optional<Restaurante> restOpt = restauranteRepo.findByEmail(email);
        if (restOpt.isPresent()) {
            Restaurante rest = restOpt.get();
            if (!rest.isHabilitado()) {
                throw new DisabledException("Usuario Deshabilitado");
            }
            if (!passwordEncoder.matches(password, rest.getPassword())) {
                throw new BadCredentialsException("Error de autenticación");
            }
            String token = jwtUtil.generateToken(rest.getEmail(), rest.getRol().name());
            return new LoginResponseDTO(token, rest.getRol().name(), rest.getNombre(), rest.getEmail());
        }

        throw new BadCredentialsException("Error de autenticación");
    }
    
    // Login de cliente con Google: valida el token de Firebase y crea el cliente si no existe.
    public LoginResponseDTO loginConGoogle(String idToken) {
        try {
            
            String uid;
            String email;
            String nombre;
            String fotoPerfil = "http://imagen_de_prueba.com/foto.jpg";

            if ("TOKEN_TEST_GOOGLE".equals(idToken)) {
                uid = "firebase-uid-damaso-123"; 
                email = "damasomai@gmail.com";    
                nombre = "Dámaso Tor";            
                fotoPerfil = "http://imagen_de_prueba.com/foto.jpg";
                System.out.println(">>> [BYPASS] Ejecutando login de Google simulado para: " + email);
            } else {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                uid = decodedToken.getUid();
                email = decodedToken.getEmail();
                nombre = decodedToken.getName();
                fotoPerfil = decodedToken.getPicture();
            }

            Optional<Usuario> usuarioOpt = usuarioRepository.findByFirebaseUid(uid);
            Usuario usuario;

            if (usuarioOpt.isEmpty()) {
                System.out.println(">>> [BYPASS] El usuario no existe en la BD local. Registrando cliente nuevo... \n");
                DTOUsuario nuevoUsuarioDTO = new DTOUsuario();
                nuevoUsuarioDTO.setUid(uid);
                nuevoUsuarioDTO.setEmail(email);
                nuevoUsuarioDTO.setNombre(nombre != null ? nombre : "Usuario Trego");
                nuevoUsuarioDTO.setUrlImagen(fotoPerfil); 
                nuevoUsuarioDTO.setRol(EnumRoles.Cliente); 

                usuario = usuarioService.altaUsuario(nuevoUsuarioDTO);
            } else {
                usuario = usuarioOpt.get();
            }

            int idUsuarioInt = usuario.getIdUsuario();
            String jwt = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().name(), usuario.getFirebaseUid(), idUsuarioInt);
            
            return new LoginResponseDTO(jwt, usuario.getRol().name(), usuario.getNombre(), usuario.getEmail());

        } catch (FirebaseAuthException e) {
            throw new BadCredentialsException("Token de Google inválido");
        }
    }

    // Login de cliente por SMS: mismo esquema que Google pero usando el teléfono.
    public LoginResponseDTO loginConSMS(String firebaseToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            String uid = decodedToken.getUid();
            String telefono = (String) decodedToken.getClaims().get("phone_number");

            Optional<Usuario> usuarioOpt = usuarioRepository.findByFirebaseUid(uid);
            Usuario usuario;

            if (usuarioOpt.isEmpty()) {
                DTOUsuario nuevoUsuarioDTO = new DTOUsuario();
                nuevoUsuarioDTO.setUid(uid);
                nuevoUsuarioDTO.setTelefono(telefono);
                nuevoUsuarioDTO.setNombre("Nuevo Cliente SMS");
                nuevoUsuarioDTO.setRol(EnumRoles.Cliente);

                usuario = usuarioService.altaUsuario(nuevoUsuarioDTO);
            } else {
                usuario = usuarioOpt.get();
            }

            String identificador = usuario.getEmail() != null ? usuario.getEmail() : usuario.getFirebaseUid();
            int idUsuarioInt = usuario.getIdUsuario();
            
            String jwt = jwtUtil.generateToken(identificador, usuario.getRol().name(), usuario.getFirebaseUid(), idUsuarioInt);
            
            return new LoginResponseDTO(jwt, usuario.getRol().name(), usuario.getNombre(), usuario.getEmail());

        } catch (FirebaseAuthException e) {
            throw new BadCredentialsException("Token de SMS inválido");
        }
    }
    
    // Implementación del cierre de sesión
    public void cerrarSesion(String token) {
        // 1. Extrae los datos necesarios desde JWT
        String uid = jwtUtil.extractUid(token);

        // 2. [opt Inicio Firebase] Si el usuario tiene UID de Firebase, revocar tokens remotos
        if (uid != null && !uid.trim().isEmpty()) {
            try {
                FirebaseAuth.getInstance().revokeRefreshTokens(uid);
                System.out.println("DEBUG: Tokens revocados en Firebase para UID: " + uid);
            } catch (FirebaseAuthException e) {
                System.err.println("DEBUG: Error revocando tokens en Firebase (posiblemente usuario no de Firebase): " + e.getMessage());
            }
        }

        // 3. eliminarToken(jwt) - Agregar a la lista negra local
        tokenBlacklistService.addToBlacklist(token);
        System.out.println("DEBUG: JWT agregado a la lista negra (invalidado)");
    }

    // FLUJO CU-CLI-01: Registro de Cliente (Google/SMS)
    public Usuario altaUsuario(DTOUsuario dto) {
        // Verificar si el cliente ya existe por UID
        if (dto.getUid() == null || dto.getUid().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El UID de Firebase es obligatorio");
        }

        Optional<Usuario> existente = usuarioRepository.findByFirebaseUid(dto.getUid());
        if (existente.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El cliente ya se encuentra registrado");
        }

        try {
            dto.setRol(EnumRoles.Cliente); // Paso 2 CU: rol por defecto
            return usuarioService.altaUsuario(dto); // Paso 3 CU: persiste en DB
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            // El diagrama indica que las excepciones se propagan hacia AuthController
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el cliente");
        }
    }
}
