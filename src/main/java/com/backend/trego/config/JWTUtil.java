package com.backend.trego.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {

    // Se define la firma secreta del backend. Es la clave matemática con la que se encriptan y desencriptan los tokens para asegurar que nadie los altere en el camino.
    // Se obtiene desde application.properties (jwt.secret) para no exponerla en el código.
    private final SecretKey key;

    public JWTUtil(@Value("${jwt.secret}") String secretKey) {
        // Convierte la cadena de texto en una clave criptográfica real basada en el algoritmo HMAC, necesaria para firmar el token de forma segura.
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Genera un JWT con el email como subject y, como claims adicionales,
     * el rol, el firebaseUid (si existe) y el idUsuario (PK en la BD).
     * El email se mantiene como subject para mantener compatibilidad y porque es
     * el identificador estable para flujos de admin/restaurante que no tienen uid.
     */
    public String generateToken(String email, String rol, String firebaseUid, Integer idUsuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", rol); // Corregido: 'role' a 'rol' para que coincida con el parámetro
        claims.put("uid", firebaseUid); // guarda el UID en los claims del token
    
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email) // Corregido para que respete tu comentario (email como subject)
                .claim("rol", rol)
                .claim("uid", firebaseUid)       // puede ser null para admin/restaurante
                .claim("userId", idUsuario)      // PK numérica de Usuario en la BD
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 8)) // 8 horas
                .signWith(key) // Firma digitalmente el token usando la clave secreta.
                .compact();
    }
    
    public String getUidFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key) // Sintaxis actualizada para evitar el error de parseClaimsJws
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return (String) claims.get("uid");
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
            System.out.println("DEBUG: Error al validar token: " + e.getMessage());
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
            System.out.println("DEBUG: Error al extraeri el Userid: " + e.getMessage());
            return null;
        }
    }
}
