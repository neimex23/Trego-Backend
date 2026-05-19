package com.backend.trego.service;

import com.backend.trego.config.AuthenticatedUser;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper para obtener el usuario autenticado actual desde cualquier @Service.
 *
 * El JwtAuthFilter coloca un AuthenticatedUser como Principal dentro del
 * SecurityContextHolder. Esta clase expone accesos cómodos al uid (Firebase),
 * al idUsuario y al objeto completo.
 *
 * Uso típico:
 *
 *   @Service
 *   public class MiServicio {
 *
 *       private final CurrentUserService currentUserService;
 *
 *       public MiServicio(CurrentUserService currentUserService) {
 *           this.currentUserService = currentUserService;
 *       }
 *
 *       public void hacerAlgo() {
 *           String uid = currentUserService.getCurrentUid();
 *           // ...
 *       }
 *   }
 *
 * Nota: en código asíncrono (@Async, hilos nuevos) el SecurityContext NO se
 * propaga por defecto. Captura el uid antes de saltar de hilo o configura
 * DelegatingSecurityContextExecutor.
 */
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
