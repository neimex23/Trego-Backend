package com.backend.trego.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${app.cors.allowed-origins}")
    private List<String> corsAllowedOrigins;

    @Value("${app.cors.allowed-methods}")
    private List<String> corsAllowedMethods;

    @Value("${app.cors.allowed-headers}")
    private List<String> corsAllowedHeaders;

    @Value("${app.cors.allow-credentials:true}")
    private boolean corsAllowCredentials;

    @Value("${app.cors.max-age:3600}")
    private long corsMaxAge;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // /error: permitir para que el front pueda leer los mensajes de error
                .requestMatchers("/error").permitAll()
                // Auth: test-auth debe estar autenticado (va ANTES del permitAll)
                .requestMatchers("/api/auth/test-auth").authenticated()
                .requestMatchers("/api/auth/cerrarSesion").authenticated()
                .requestMatchers("/api/auth/**").permitAll()
                // Registro de restaurante (flujo por código de verificación)
                .requestMatchers("/api/usuarios/registrar-restaurante/**").permitAll()
                // Menú público de restaurante
                .requestMatchers("/api/pedido/restaurante/*/verMenu").permitAll()
                // Webhook de MercadoPago
                .requestMatchers("/api/pagos/webhook").permitAll()
                // Consulta de estado de pago: el front la usa al volver del checkout,
                // tras el redirect de MP (cuando puede no tener el token a mano).
                .requestMatchers("/api/pagos/estado/**").permitAll()
                // Swagger
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Página DEV de login Firebase (solo testing local)
                .requestMatchers("/dev-login.html").permitAll()
                // Carrito
                .requestMatchers("/api/carrito/**").authenticated()
                // Pedidos
                .requestMatchers("/api/pedido/**").authenticated()
                // Pagos (resto de endpoints, ej. crear preferencia)
                .requestMatchers("/api/pagos/**").authenticated()
                // Productos y restaurantes
                .requestMatchers("/api/productos/**").authenticated()
                .requestMatchers("/api/restaurantes/**").authenticated()
                // Clientes y usuarios
                .requestMatchers("/api/clientes/**").authenticated()
                .requestMatchers("/api/usuarios/**").authenticated()
                // Notificaciones
                .requestMatchers("/api/notificaciones/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Valores configurables desde application.properties (app.cors.*)
        configuration.setAllowedOrigins(corsAllowedOrigins);
        configuration.setAllowedMethods(corsAllowedMethods);
        configuration.setAllowedHeaders(corsAllowedHeaders);
        configuration.setAllowCredentials(corsAllowCredentials);
        configuration.setMaxAge(corsMaxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        //PrintCorsConfig(configuration);
        return source;
    }


    @SuppressWarnings("unused")
    private void PrintCorsConfig(CorsConfiguration config) {
        System.out.println("CORS Configuration:");
        System.out.println("Allowed Origins: " + config.getAllowedOrigins());
        System.out.println("Allowed Methods: " + config.getAllowedMethods());
        System.out.println("Allowed Headers: " + config.getAllowedHeaders());
        System.out.println("Allow Credentials: " + config.getAllowCredentials());
        System.out.println("Max Age: " + config.getMaxAge());
    }
}