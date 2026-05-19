package com.backend.trego.config;

/**
 * Representa al usuario autenticado dentro del SecurityContext.
 * Es el Principal que el JwtAuthFilter coloca en SecurityContextHolder.
 *
 * Acceso típico en un controller:
 *
 *   AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder
 *           .getContext().getAuthentication().getPrincipal();
 *   String uid = user.getUid();
 *
 * O con @AuthenticationPrincipal:
 *
 *   @GetMapping("/me")
 *   public ResponseEntity<?> me(@AuthenticationPrincipal AuthenticatedUser user) { ... }
 */
public class AuthenticatedUser {

    private final String uid;          // firebaseUid; puede ser null para admin/restaurante
    private final Integer idUsuario;   // PK en la tabla Usuario
    private final String email;        // subject del JWT
    private final String rol;

    public AuthenticatedUser(String uid, Integer idUsuario, String email, String rol) {
        this.uid = uid;
        this.idUsuario = idUsuario;
        this.email = email;
        this.rol = rol;
    }

    public String getUid() {
        return uid;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }

    @Override
    public String toString() {
        return "AuthenticatedUser{uid=" + uid + ", idUsuario=" + idUsuario + ", email=" + email + ", rol=" + rol + "}";
    }
}
