package com.backend.trego.service;

import com.backend.trego.config.AuthenticatedUser;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// Acceso cómodo al usuario autenticado (el AuthenticatedUser que dejó el
// JwtAuthFilter en el SecurityContext) desde cualquier servicio.
// Ojo: en código asíncrono el SecurityContext no se propaga solo, capturar
// el uid antes de cambiar de hilo.
@Component
public class CurrentUserService {

    public AuthenticatedUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof AuthenticatedUser)) {
            throw new IllegalStateException("No hay usuario autenticado en el SecurityContext");
        }

        return (AuthenticatedUser) auth.getPrincipal();
    }

    public AuthenticatedUser getCurrentUserOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof AuthenticatedUser)) {
            return null;
        }

        return (AuthenticatedUser) auth.getPrincipal();
    }

    public String getCurrentUid() {
        return getCurrentUser().getUid();
    }

    public Integer getCurrentIdUsuario() {
        return getCurrentUser().getIdUsuario();
    }

    //Solo Disponible para Restaurante o Administrador, lanza IllegalStateException si el rol no coincide o el token no tiene userId.
    public Integer getCurrentId() {
        AuthenticatedUser user = getCurrentUser();
        String rol = user.getRol();

        if (!"Restaurante".equals(rol) && !"Administrador".equals(rol)) {
            throw new IllegalStateException(
                    "getCurrentId() solo está disponible para Restaurante o Administrador (rol actual: " + rol + ")");
        }

        Integer id = user.getIdUsuario();
        if (id == null) {
            throw new IllegalStateException(
                    "El token del usuario autenticado no contiene userId (rol: " + rol + ")");
        }
        return id;
    }

    public String getCurrentEmail() {
        return getCurrentUser().getEmail();
    }

    public String getCurrentRol() {
        return getCurrentUser().getRol();
    }

    public boolean isAuthenticated() {
        return getCurrentUserOrNull() != null;
    }

    public boolean isAdmin() {
        AuthenticatedUser user = getCurrentUserOrNull();
        return user != null && "Administrador".equals(user.getRol());
    }
}
