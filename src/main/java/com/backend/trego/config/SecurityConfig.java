package com.backend.trego.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() { 
        // Se encarga de comprobar que la contraseña en texto plano coincida con el hash de MySQL.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { 
        // Registra la cadena de filtros encargada de interceptar todas las peticiones HTTP entrantes.
        http
            .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF ya que usamos tokens JWT
            
            // CRITICO: Configurar la política de sesión como STATELESS (Sin estado).
            // Esto le indica a Spring Security que no debe crear sesiones web en memoria,
            // forzándolo a validar el token JWT en cada petición y desactivando el password por consola.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(auth -> auth
                // Permite el acceso público y libre a todos los endpoints dentro de /api/auth/
                // Esto incluye de forma automática: /login/admin, /google y /sms
                .requestMatchers("/api/auth/**").permitAll()
                
                // Cualquier otra ruta del sistema requerirá autenticación previa
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}