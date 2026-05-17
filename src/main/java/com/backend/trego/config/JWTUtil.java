package com.backend.trego.config;

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
    //Convierte la cadena de texto en una clave criptográfica real basada en el algoritmo HMAC, necesaria para firmar el token de forma segura.
    public String generateToken(String email, String rol) {
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 8)) // 8 horas
                .signWith(key) //Firma digitalmente el token completo utilizando la clave secreta generada al principio para garantizar la integridad de los datos.
                .compact();
    }
    //Método público que recibe la identidad del usuario y su rol para empaquetarlos en el token.
}