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
import com.backend.trego.service.TokenBlacklistService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JWTUtil jwtUtil;
	private final TokenBlacklistService tokenBlacklistService;  // lista negra para evitar que se utilice el mismo token una vez que se dio cerrar sesion 
    
	public JwtAuthFilter(JWTUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(AUTH_HEADER);

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length()).trim();

			// Si el token esta en la lista negra se bloquea, no se procesa la autenticacion por el return
            if (tokenBlacklistService.isBlacklisted(token)) {
                System.out.println("DEBUG: Intento de acceso con token revocado (Sesión cerrada)");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("La sesion ha sido cerrada. Token invalido. ");
                return;  // fin de respuestas salir 
            }

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
