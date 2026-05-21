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

    public String getCurrentEmail() {
        return getCurrentUser().getEmail();
    }

    public String getCurrentRol() {
        return getCurrentUser().getRol();
    }

    public boolean isAuthenticated() {
        return getCurrentUserOrNull() != null;
    }
}
