package com.backend.trego.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    // Ruta externa al service-account.json (se monta en runtime en la EC2).
    // Si esta vacia, se busca en el classpath (modo desarrollo).
    @Value("${firebase.credentials.path:}")
    private String credentialsPath;

    @PostConstruct
    public void initializeFirebase() {
        try {
            InputStream serviceAccount;
            if (credentialsPath != null && !credentialsPath.isBlank()) {
                serviceAccount = new FileInputStream(credentialsPath);
            } else {
                serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Evitamos inicializarla dos veces
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println(">>> [Firebase Admin SDK] Inicializado con éxito en Trego.");
            }
        } catch (IOException e) {
            System.err.println(">>> [ERROR] No se pudo leer el archivo firebase-service-account.json: " + e.getMessage());
        }
    }
}