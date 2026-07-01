package com.backend.trego.service;

import com.backend.trego.entity.Usuario;
import com.backend.trego.entity.Enums.EnumRoles;
import com.backend.trego.entity.Administrador;
import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.DTOs.DTOLogin;
import com.backend.trego.entity.DTOs.DTOLoginResponse;
import com.backend.trego.entity.DTOs.DTOUsuario; 

import com.backend.trego.repository.UsuarioRepository;
import com.backend.trego.config.JWTUtil;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.Optional;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
	private final TokenBlacklistService tokenBlacklistService;

    public AuthService(PasswordEncoder passwordEncoder,
                       JWTUtil jwtUtil,
                       UsuarioRepository usuarioRepository,
                       UsuarioService usuarioService,
                       TokenBlacklistService tokenBlacklistService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    // Login de admin / restaurante con email y contraseña.
    public DTOLoginResponse login(DTOLogin loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();
        
        Optional<Administrador> adminOpt = usuarioRepository.findAdministradorByEmail(email);
        if (adminOpt.isPresent()) {
            Administrador admin = adminOpt.get();
            
            if (!passwordEncoder.matches(password, admin.getPassword())) {
                throw new BadCredentialsException("Error de autenticación");
            }
            
            String token = jwtUtil.generateToken(admin.getEmail(), admin.getRol().name(), null, admin.getIdUsuario());
            return new DTOLoginResponse(token, admin.getRol().name(), admin.getNombre(), admin.getEmail());
        }

        Optional<Restaurante> restOpt = usuarioRepository.findRestauranteByEmail(email);
        if (restOpt.isPresent()) {
            Restaurante rest = restOpt.get();
            if (!passwordEncoder.matches(password, rest.getPassword())) {
                throw new BadCredentialsException("Error de autenticación");
            }
            if (!rest.isCuentaHabilitada()) {
                throw new DisabledException("Usuario deshabilitado");
            }
            String token = jwtUtil.generateToken(rest.getEmail(), rest.getRol().name(), null, rest.getIdUsuario());
            return new DTOLoginResponse(token, rest.getRol().name(), rest.getNombre(), rest.getEmail());
        }

        throw new BadCredentialsException("Error de autenticación");
    }
    
    // Login de cliente con Google: valida el token de Firebase y crea el cliente si no existe.
    public DTOLoginResponse loginConGoogle(String idToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String nombre = decodedToken.getName();
            String fotoPerfil = decodedToken.getPicture();
            String telefono = (String) decodedToken.getClaims().get("phone_number");

            Optional<Cliente> usuarioOpt = usuarioRepository.findByUidCliente(uid);
            Cliente cliente;

            if (usuarioOpt.isEmpty()) {
                System.out.println("El usuario no existe en la BD local. Registrando cliente nuevo...");
                DTOUsuario nuevoUsuarioDTO = new DTOUsuario(
                        null,
                        uid,
                        nombre != null ? nombre : "Usuario Trego",
                        email,
                        null,
                        fotoPerfil,
                        telefono,
                        EnumRoles.Cliente);

                cliente = (Cliente) usuarioService.altaUsuario(nuevoUsuarioDTO);
            } else {
                cliente = usuarioOpt.get();
                sincronizarCamposCliente(cliente, uid, email, nombre, fotoPerfil, telefono);
            }

            if (!cliente.isHabilitado()) {
                throw new DisabledException("Usuario deshabilitado");
            }

            Integer idUsuario = cliente.getIdUsuario();
            String identificador = cliente.getEmail() != null ? cliente.getEmail() : uid;
            String jwt = jwtUtil.generateToken(identificador, cliente.getRol().name(), uid, idUsuario);

            return new DTOLoginResponse(jwt, cliente.getRol().name(), cliente.getNombre(), cliente.getEmail());

        } catch (FirebaseAuthException e) {
            throw new BadCredentialsException("Token de Google inválido");
        }
    }

    // Login de cliente por SMS: mismo esquema que Google pero usando el teléfono.
    public DTOLoginResponse loginConSMS(String firebaseToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            String uid = decodedToken.getUid();
            String telefono = (String) decodedToken.getClaims().get("phone_number");
            String email = decodedToken.getEmail();
            String nombre = decodedToken.getName();
            String fotoPerfil = decodedToken.getPicture();

            Optional<Cliente> usuarioOpt = usuarioRepository.findByUidCliente(uid);
            Cliente cliente;

            if (usuarioOpt.isEmpty()) {
                DTOUsuario nuevoUsuarioDTO = new DTOUsuario(
                        null,
                        uid,
                        nombre != null ? nombre : "Nuevo Cliente SMS",
                        email,
                        null,
                        fotoPerfil,
                        telefono,
                        EnumRoles.Cliente);

                cliente = (Cliente) usuarioService.altaUsuario(nuevoUsuarioDTO);
            } else {
                cliente = usuarioOpt.get();
                sincronizarCamposCliente(cliente, uid, email, nombre, fotoPerfil, telefono);
            }

            if (!cliente.isHabilitado()) {
                throw new DisabledException("Usuario deshabilitado");
            }

            String identificador = cliente.getEmail() != null ? cliente.getEmail() : uid;
            Integer idUsuario = cliente.getIdUsuario();

            String jwt = jwtUtil.generateToken(identificador, cliente.getRol().name(), uid, idUsuario);

            return new DTOLoginResponse(jwt, cliente.getRol().name(), cliente.getNombre(), cliente.getEmail());

        } catch (FirebaseAuthException e) {
            throw new BadCredentialsException("Token de SMS inválido");
        }
    }

    // Rellena en el Cliente solo los campos que esten vacios con lo que traiga el token de Firebase. 
    private void sincronizarCamposCliente(Cliente cliente, String uid, String email,
                                          String nombre, String fotoPerfil, String telefono) {
        boolean cambio = false;

        if (esVacio(cliente.getUidCliente()) && !esVacio(uid)) {
            cliente.setUidCliente(uid);
            cambio = true;
        }
        if (esVacio(cliente.getEmail()) && !esVacio(email)) {
            cliente.setEmail(email);
            cambio = true;
        }
        if (esVacio(cliente.getTelefono()) && !esVacio(telefono)) {
            cliente.setTelefono(telefono);
            cambio = true;
        }
        if (esVacio(cliente.getFotoPerfil()) && !esVacio(fotoPerfil)) {
            cliente.setFotoPerfil(fotoPerfil);
            cambio = true;
        }
        if ((esVacio(cliente.getNombre()) || "Nuevo Cliente SMS".equals(cliente.getNombre()))
                && !esVacio(nombre)) {
            cliente.setNombre(nombre);
            cambio = true;
        }

        if (cambio) {
            usuarioRepository.save(cliente);
        }
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }
    
    // Vincula un segundo proveedor (SMS <-> Google) a la cuenta del cliente autenticado.n.
    @Transactional
    public DTOUsuario vincularProveedor(Integer idUsuario, String firebaseToken) {
        if (idUsuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No hay un cliente autenticado");
        }

        Cliente cliente = usuarioRepository.findClienteById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Solo los clientes pueden vincular proveedores"));

        if (!cliente.isHabilitado()) {
            throw new DisabledException("Usuario deshabilitado");
        }

        FirebaseToken decodedToken;
        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
        } catch (FirebaseAuthException e) {
            throw new BadCredentialsException("Token de Firebase inválido");
        }

        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        String nombre = decodedToken.getName();
        String fotoPerfil = decodedToken.getPicture();
        String telefono = (String) decodedToken.getClaims().get("phone_number");

        // El token debe pertenecer a la misma cuenta Firebase (linkWithCredential mantiene el UID).
        if (!esVacio(cliente.getUidCliente()) && !esVacio(uid)
                && !cliente.getUidCliente().equals(uid)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El token pertenece a otra cuenta de Firebase. Vinculá los proveedores en la app antes de sincronizar.");
        }

        // Evitar colisiones con otra cuenta distinta que ya use ese uid/telefono/email.
        verificarNoPerteneceAOtroCliente(idUsuario, uid, email, telefono);

        sincronizarCamposCliente(cliente, uid, email, nombre, fotoPerfil, telefono);

        return new DTOUsuario(
                cliente.getIdUsuario(),
                cliente.getUidCliente(),
                cliente.getNombre(),
                cliente.getEmail(),
                null,
                cliente.getFotoPerfil(),
                cliente.getTelefono(),
                cliente.getRol());
    }

    // Si el uid/telefono/email entrante ya pertenece a OTRO cliente, se aborta para no
    // duplicar identidades entre cuentas distintas.
    private void verificarNoPerteneceAOtroCliente(Integer idActual, String uid, String email, String telefono) {
        if (!esVacio(uid)) {
            usuarioRepository.findByUidCliente(uid)
                    .filter(c -> !c.getIdUsuario().equals(idActual))
                    .ifPresent(c -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                "Ese proveedor ya está vinculado a otra cuenta");
                    });
        }
        if (!esVacio(telefono)) {
            usuarioRepository.findClienteByTelefono(telefono)
                    .filter(c -> !c.getIdUsuario().equals(idActual))
                    .ifPresent(c -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                "Ese teléfono ya está vinculado a otra cuenta");
                    });
        }
        if (!esVacio(email)) {
            usuarioRepository.findClienteByEmail(email)
                    .filter(c -> !c.getIdUsuario().equals(idActual))
                    .ifPresent(c -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                "Ese email ya está vinculado a otra cuenta");
                    });
        }
    }

    // Sesión JWT tras confirmar el registro de un restaurante (sin volver a loguearse).
    public DTOLoginResponse crearSesionRestauranteRegistrado(DTOUsuario usuario) {
        String token = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getRol().name(),
                null,
                usuario.getIdUsuario());
        return new DTOLoginResponse(token, usuario.getRol().name(), usuario.getNombre(), usuario.getEmail());
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
    @Transactional
    public Usuario altaUsuario(DTOUsuario dto) {
        if (dto.getUid() == null || dto.getUid().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El UID de Firebase es obligatorio");
        }

        Optional<Cliente> existente = usuarioRepository.findByUidCliente(dto.getUid());
        if (existente.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El cliente ya se encuentra registrado");
        }

        try {
            DTOUsuario clienteDTO = new DTOUsuario(
                    dto.getIdUsuario(),
                    dto.getUid(),
                    dto.getNombre(),
                    dto.getEmail(),
                    dto.getPassword(),
                    dto.getUrlImagen(),
                    dto.getTelefono(),
                    EnumRoles.Cliente);
            return usuarioService.altaUsuario(clienteDTO); // Paso 3 CU: persiste en DB
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el cliente");
        }
    }
}
