package com.backend.trego.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {
        try {
            // Lee el archivo JSON desde la carpeta src/main/resources
            ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
            InputStream serviceAccount = resource.getInputStream();

            // Configura las opciones de conexión con las credenciales de Google
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Inicializa la aplicación de Firebase si no fue inicializada previamente
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println(">>> [Firebase Admin SDK] Inicializado con éxito en Trego.");
            }
        } catch (IOException e) {
            System.err.println(">>> [ERROR] No se pudo leer el archivo firebase-service-account.json: " + e.getMessage());
        }
    }
}