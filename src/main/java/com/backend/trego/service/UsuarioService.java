package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOUsuario;
import com.backend.trego.repository.UsuarioRepository;
import com.backend.trego.entity.RegistroTemporal;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.Usuario;
import com.backend.trego.entity.Cliente;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;

// Gestión de usuarios: alta, búsqueda, direcciones y verificación por email.
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final NotificacionesService notificacionesService;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    // Mapa en memoria: email -> datos del registro pendiente (código + contraseña)
    private final Map<String, RegistroTemporal> registrosPendientes = new ConcurrentHashMap<>();

    public UsuarioService(UsuarioRepository usuarioRepository, NotificacionesService notificacionesService,
            PasswordEncoder passwordEncoder, CurrentUserService currentUserService) {
        this.usuarioRepository = usuarioRepository;
        this.notificacionesService = notificacionesService;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
    }

    // Da de alta un cliente nuevo a partir del DTO y lo devuelve ya con su id.
    public Usuario altaUsuario(DTOUsuario usuarioDTO) {
        Cliente nuevoCliente = new Cliente();

        nuevoCliente.setFirebaseUid(usuarioDTO.getUid());
        nuevoCliente.setEmail(usuarioDTO.getEmail());
        nuevoCliente.setNombre(usuarioDTO.getNombre());
        nuevoCliente.setRol(usuarioDTO.getRol());

        return usuarioRepository.save(nuevoCliente);
    }

    // Arranca el registro de un restaurante: chequea que el email no exista,
    // manda el código y guarda los datos en el mapa hasta que lo confirme.
    public void iniciarRegistroRestaurante(String email, String password) {
        if (existeUsuario(email)) {
            throw new IllegalArgumentException("El correo electrónico ya se encuentra ingresado en el sistema.");
        }
        String codigo = this.enviarCodigoVerificacion(email);

        RegistroTemporal registro = new RegistroTemporal(email, password, codigo);
        registrosPendientes.put(email, registro);
    }

    public DTOUsuario registrarRestaurante(String email, String password) {

        String passwordCifrada = passwordEncoder.encode(password);

        Restaurante nuevoRestaurante = new Restaurante(email, passwordCifrada);

        Restaurante restauranteGuardado = usuarioRepository.save(nuevoRestaurante);

        registrosPendientes.remove(email);

        DTOUsuario usuario = new DTOUsuario(
                restauranteGuardado.getIdUsuario(),
                null,
                null,
                restauranteGuardado.getEmail(),
                null,
                null,
                null,
                restauranteGuardado.getRol());

        return usuario;
    }

    public Boolean existeUsuario(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public String enviarCodigoVerificacion(String email) {
        return notificacionesService.codigoVerificacionEmail(email);
    }

    // Valida el código que el usuario ingresó y, si es correcto, da de alta el restaurante.
    public DTOUsuario verificarCodigo(String email, String codigo) {
        RegistroTemporal pendiente = registrosPendientes.get(email);

        if (pendiente == null || pendiente.estaExpiradoCodigo() || !pendiente.getCodigoVerificacion().equals(codigo)) {
            throw new IllegalArgumentException("Código inválido o expirado");
        }

        return registrarRestaurante(pendiente.getEmail(), pendiente.getPassword());

    }

    // Reenvía el código reutilizando los datos que quedaron guardados en el mapa.
    public void reenviarCodigoVerificacion(String email) {
        RegistroTemporal pendiente = registrosPendientes.get(email);

        // Si ya no está en el mapa es porque expiró del todo y lo borró el @Scheduled
        if (pendiente == null) {
            throw new IllegalArgumentException(
                    "El tiempo de registro ha expirado por completo. Por favor, vuelve a ingresar tus datos.");
        }

        String nuevoCodigo = this.enviarCodigoVerificacion(email);

        RegistroTemporal registroActualizado = new RegistroTemporal(email, pendiente.getPassword(), nuevoCodigo);
        registrosPendientes.put(email, registroActualizado);

        System.out.println("[Caché] Nuevo código generado y guardado para: " + email);
    }

    // Cada 5 minutos limpia del mapa los registros que quedaron sin completar.
    @Scheduled(fixedRate = 300000)
    public void limpiarRegistrosExpirados() {
        if (registrosPendientes.isEmpty()) {
            return;
        }

        System.out.println("[Caché] Iniciando limpieza automática de registros expirados...");
        int sizeInitial = registrosPendientes.size();

        registrosPendientes.entrySet().removeIf(entry -> {
            boolean expirado = entry.getValue().estaExpiradoCacheReenvio();
            if (expirado) {
                System.out.println("[Caché] Removiendo registro colgado del email: " + entry.getKey());
            }
            return expirado;
        });

        int eliminados = sizeInitial - registrosPendientes.size();
        if (eliminados > 0) {
            System.out.println("[Caché] Limpieza terminada. Se eliminaron " + eliminados + " registros colgados.");
        }
    }

    // Devuelve el usuario autenticado actualmente, resuelto desde el token.
    // Cliente: se busca por firebaseUid; Restaurante/Administrador: por idUsuario.
    public DTOUsuario obtenerUsuarioActual() {
        var auth = currentUserService.getCurrentUser();
        Usuario u = null;

        if (auth.getIdUsuario() != null) {
            u = usuarioRepository.findById(auth.getIdUsuario()).orElse(null);
        }
        if (u == null && auth.getUid() != null) {
            u = usuarioRepository.findByFirebaseUid(auth.getUid()).orElse(null);
        }
        if (u == null && auth.getEmail() != null) {
            u = usuarioRepository.findByEmail(auth.getEmail()).orElse(null);
        }
        if (u == null) {
            throw new IllegalStateException("No se encontró el usuario autenticado en la base de datos.");
        }

        return new DTOUsuario(
                u.getIdUsuario(),
                u.getFirebaseUid(),
                u.getNombre(),
                u.getEmail(),
                null,
                u.getFotoPerfil(),
                null,
                u.getRol());
    }

    public List<DTODireccion> obtenerDirecciones() {
        String uid = currentUserService.getCurrentUid();
        return usuarioRepository.findDireccionesByFirebaseUid(uid);
    }

}
