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

    // Clave secreta para firmar y validar los tokens. Viene de application.properties (jwt.secret).
    private final SecretKey key;

    public JWTUtil(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // El email va como subject porque admin y restaurante no tienen uid de Firebase.
    @SuppressWarnings("deprecation")
    public String generateToken(String email, String rol, String firebaseUid, Integer idUsuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", rol);
        claims.put("uid", firebaseUid);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .claim("rol", rol)
                .claim("uid", firebaseUid)       // null en admin/restaurante
                .claim("userId", idUsuario)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 8)) // 8 horas
                .signWith(key)
                .compact();
    }

    public String getUidFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return (String) claims.get("uid");
    }

    // Versión vieja, no usar en flujos nuevos: deja el token sin uid ni userId.
    @Deprecated
    public String generateToken(String email, String rol) {
        return generateToken(email, rol, null, null);
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

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
        var claims = parseClaims(token);
        Object rol = claims.get("rol");
        if (rol == null) {
            rol = claims.get("role");
        }
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
