package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOUsuario;
import com.backend.trego.repository.UsuarioRepository;
import com.backend.trego.entity.RegistroTemporal;
import com.backend.trego.entity.Restaurante;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Servicio encargado de la gestión de usuarios del sistema:
 * alta, búsqueda, validación de direcciones, verificación por email, etc.
 *
 * Las firmas siguen el Documento de Diseño (Tabla 1 - UsuarioService).
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final NotificacionesService notificacionesService;
    private final PasswordEncoder passwordEncoder;
    // Caché temporal en memoria para asociar emails con sus códigos y contraseñas
    private final Map<String, RegistroTemporal> registrosPendientes = new ConcurrentHashMap<>();

    public UsuarioService(UsuarioRepository usuarioRepository, NotificacionesService notificacionesService,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.notificacionesService = notificacionesService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Da de alta un nuevo usuario en el sistema.
     */
    public boolean altaUsuario(DTOUsuario usuarioDTO) {
        // TODO: implementar
        return false;
    }

    /**
     * Obtiene la lista de direcciones asociadas a un usuario por su UID.
     */
    public List<DTODireccion> obtenerDirecciones(String uid) {
        // TODO: implementar
        return List.of();
    }

    /**
     * Valida una dirección a partir de su latitud y longitud
     * (ej. mediante un servicio de geocoding).
     */
    public DTODireccion validarDireccion(String lat, String lng) {
        // TODO: implementar
        return null;
    }

    /**
     * CU-RES-01 - Paso 4 y 5: Inicia el proceso de registro, valida duplicados y
     * envía el correo.
     * 
     * @param email
     * @param password
     */
    public void iniciarRegistroRestaurante(String email, String password) {
        if (existeUsuario(email)) {
            throw new IllegalArgumentException("El correo electrónico ya se encuentra ingresado en el sistema.");
        }
        // Generamos el código y enviamos el correo electrónico usando tu servicio de
        // notificaciones
        String codigo = notificacionesService.codigoVerificacionEmail(email);

        // Guardamos los datos de forma temporal hasta que introduzca el código
        RegistroTemporal registro = new RegistroTemporal(email, password, codigo);
        registrosPendientes.put(email, registro);
    }

    /**
     * Registra un restaurante a partir de email y contraseña.
     */
    public DTOUsuario registrarRestaurante(String email, String password) {

        String passwordCifrada = passwordEncoder.encode(password);

        Restaurante nuevoRestaurante = new Restaurante(email, passwordCifrada);

        Restaurante restauranteGuardado = usuarioRepository.save(nuevoRestaurante);

        registrosPendientes.remove(email);

        DTOUsuario usuario = new DTOUsuario();
        usuario.setEmail(restauranteGuardado.getEmail());
        usuario.setIdUsuario(restauranteGuardado.getIdUsuario());
        usuario.setRol(restauranteGuardado.getRol());

        return usuario;
    }

    /**
     * Verifica si existe un usuario con el email indicado.
     */
    public Boolean existeUsuario(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    /**
     * Envía un código de verificación al correo electrónico indicado.
     */
    public Boolean enviarCodigoVerificacion(String email) {
        // TODO: implementar
        return false;
    }

    /**
     * Verifica un código de verificación previamente enviado al usuario.
     */
    public DTOUsuario verificarCodigo(String email, String codigo) {
        RegistroTemporal pendiente = registrosPendientes.get(email);

        // Flujo Alternativo 7.1: Código incorrecto o expirado
        if (pendiente == null || pendiente.estaExpirado() || !pendiente.getCodigoVerificacion().equals(codigo)) {
            throw new IllegalArgumentException("Código inválido o expirado");
        }

        return registrarRestaurante(pendiente.getEmail(), pendiente.getPassword());

    }

    /**
     * Tarea programada en segundo plano que limpia el caché de registros colgados.
     * Son aquellos que no llegan a completar el registro
     * * fixedRate = 300000: Se ejecuta cada 5 minutos (300,000 milisegundos).
     */
    @Scheduled(fixedRate = 300000)
    public void limpiarRegistrosExpirados() {
        if (registrosPendientes.isEmpty()) {
            return;
        }

        System.out.println("[Caché] Iniciando limpieza automática de registros expirados...");
        int tamañoInicial = registrosPendientes.size();

        // Removemos de forma segura todas las entradas del mapa que cumplan la
        // condición 'estaExpirado()'
        registrosPendientes.entrySet().removeIf(entry -> {
            boolean expirado = entry.getValue().estaExpirado();
            if (expirado) {
                System.out.println("[Caché] Removiendo registro colgado del email: " + entry.getKey());
            }
            return expirado;
        });

        int eliminados = tamañoInicial - registrosPendientes.size();
        if (eliminados > 0) {
            System.out.println("[Caché] Limpieza terminada. Se eliminaron " + eliminados + " registros colgados.");
        }
    }

}
