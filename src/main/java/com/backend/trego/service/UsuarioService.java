package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOUsuario;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio encargado de la gestión de usuarios del sistema:
 * alta, búsqueda, validación de direcciones, verificación por email, etc.
 *
 * Las firmas siguen el Documento de Diseño (Tabla 1 - UsuarioService).
 */
@Service
public class UsuarioService {

    public UsuarioService() {
        // TODO: inyectar repositorios necesarios (Cliente, Restaurante, Administrador)
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
     * Registra un restaurante a partir de email y contraseña.
     */
    public DTOUsuario registrarRestaurante(String email, String password) {
        // TODO: implementar
        return null;
    }

    /**
     * Verifica si existe un usuario con el email indicado.
     */
    public Boolean existeUsuario(String email) {
        // TODO: implementar
        return false;
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
    public Boolean verificarCodigo(String codigo) {
        // TODO: implementar
        return false;
    }
}
