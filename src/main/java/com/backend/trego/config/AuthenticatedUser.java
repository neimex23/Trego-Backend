package com.backend.trego.config;

// Principal que JwtAuthFilter mete en el SecurityContext. Se puede leer en los
// controllers con @AuthenticationPrincipal AuthenticatedUser user.
public class AuthenticatedUser {

    private final String uid;          // firebaseUid; null para admin/restaurante
    private final Integer idUsuario;   // PK en la tabla Usuario
    private final String email;
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
