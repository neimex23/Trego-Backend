package com.backend.trego.service;

import com.backend.trego.entity.Administrador;
import com.backend.trego.entity.DTOs.DTOUsuario;
import com.backend.trego.repository.AdministradorRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Gestión del administrador autenticado.
@Service
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final CurrentUserService currentUserService;

    public AdministradorService(AdministradorRepository administradorRepository,
            CurrentUserService currentUserService) {
        this.administradorRepository = administradorRepository;
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
        Administrador admin = administradorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Administrador autenticado no encontrado con id: " + id));

        return new DTOUsuario(
                admin.getIdUsuario(),
                admin.getFirebaseUid(),
                admin.getNombre(),
                admin.getEmail(),
                null,
                admin.getFotoPerfil(),
                null,
                admin.getRol());
    }
}
