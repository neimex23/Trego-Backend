package com.backend.trego.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Blacklist en memoria de JWT invalidados por logout: se pierde al reiniciar la
// app y no purga entradas (aceptable porque los tokens expiran a las 8 horas).
@Service
public class TokenBlacklistService {
    
    // Usamos un Set concurrente para operaciones seguras en hilos (Thread-safe)
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    public void addToBlacklist(String token) {
        blacklist.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
