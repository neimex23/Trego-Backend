package com.backend.trego.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JWTUtil jwtUtil;

    public JwtAuthFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(AUTH_HEADER);

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length()).trim();

            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);
                String uid = jwtUtil.extractUid(token);
                Integer idUsuario = jwtUtil.extractUserId(token);
                String rol = jwtUtil.extractRol(token);

                AuthenticatedUser principal = new AuthenticatedUser(uid, idUsuario, email, rol);

                List<SimpleGrantedAuthority> authorities = (rol == null)
                        ? Collections.emptyList()
                        : Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol));

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("DEBUG: Usuario autenticado correctamente: " + email);
            } else {
                System.out.println("DEBUG: Token inválido");
            }
        }

        filterChain.doFilter(request, response);
    }
}