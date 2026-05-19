package com.backend.trego.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtil {

    private final String SECRET_KEY = "UnaClaveSecretaMuyLargaParaTregoAplicacion2026!";
    // Se define la firma secreta del backend. Es la clave matemática con la que se encriptan y desencriptan los tokens para asegurar que nadie los altere en el camino.
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    // Convierte la cadena de texto en una clave criptográfica real basada en el algoritmo HMAC, necesaria para firmar el token de forma segura.

    /**
     * Genera un JWT con el email como subject y, como claims adicionales,
     * el rol, el firebaseUid (si existe) y el idUsuario (PK en la BD).
     * El email se mantiene como subject para mantener compatibilidad y porque es
     * el identificador estable para flujos de admin/restaurante que no tienen uid.
     */
    public String generateToken(String email, String rol, String firebaseUid, Integer idUsuario) {
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .claim("uid", firebaseUid)       // puede ser null para admin/restaurante
                .claim("userId", idUsuario)      // PK numérica de Usuario en la BD
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 8)) // 8 horas
                .signWith(key) // Firma digitalmente el token usando la clave secreta.
                .compact();
    }

    /**
     * Overload de compatibilidad con código existente. Se conserva pero NO debe
     * usarse en nuevos flujos porque deja el token sin uid ni userId.
     */
    @Deprecated
    public String generateToken(String email, String rol) {
        return generateToken(email, rol, null, null);
    }

    /**
     * Parsea y valida el JWT. Lanza JwtException si el token es inválido o expiró.
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Devuelve true si el token es parseable, está firmado correctamente y no expiró.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRol(String token) {
        Object rol = parseClaims(token).get("rol");
        return rol == null ? null : rol.toString();
    }

    public String extractUid(String token) {
        Object uid = parseClaims(token).get("uid");
        return uid == null ? null : uid.toString();
    }

    public Integer extractUserId(String token) {
        Object userId = parseClaims(token).get("userId");
        if (userId == null) return null;
        if (userId instanceof Number) return ((Number) userId).intValue();
        try {
            return Integer.parseInt(userId.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
