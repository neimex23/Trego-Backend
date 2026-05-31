package com.backend.trego.service;

import com.backend.trego.entity.Administrador;
import com.backend.trego.entity.DTOs.DTOUsuario;
import com.backend.trego.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Gestión del administrador autenticado.
@Service
public class AdministradorService {

    private final UsuarioRepository usuarioRepository;
    private final CurrentUserService currentUserService;

    public AdministradorService(UsuarioRepository usuarioRepository,
            CurrentUserService currentUserService) {
        this.usuarioRepository = usuarioRepository;
        this.currentUserService = currentUserService;
    }

    // Devuelve el administrador autenticado, resuelto desde el id del token.
    public DTOUsuario obtenerAdministradorActual() {
        String rol = currentUserService.getCurrentRol();
        if (!"Administrador".equals(rol)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El endpoint solo está disponible para administradores (rol actual: " + rol + ")");
        }

        Integer id = currentUserService.getCurrentId();
        Administrador admin = usuarioRepository.findAdministradorById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Administrador autenticado no encontrado con id: " + id));

        return new DTOUsuario(
                admin.getIdUsuario(),
                null, // Admin no tine uid de Firebase, se ignora en el DTO.
                admin.getNombre(),
                admin.getEmail(),
                null, // Nunca se expone la contraseña, se ignora en el DTO.
                admin.getFotoPerfil(),
                null, // Admin no tiene teléfono, se ignora en el DTO.
                admin.getRol()
        );
    }
}
