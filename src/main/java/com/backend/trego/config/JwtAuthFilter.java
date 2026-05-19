package com.backend.trego.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Filtro que se ejecuta una vez por request.
 *
 * Si la request trae un header "Authorization: Bearer <jwt>" válido, arma un
 * AuthenticatedUser con email/uid/idUsuario/rol y lo deja disponible en
 * SecurityContextHolder. Después, cualquier controller puede obtenerlo via
 * @AuthenticationPrincipal AuthenticatedUser user.
 *
 * Si el header falta o el token es inválido, simplemente no autentica.
 * Spring Security se encargará de rechazar la request en los endpoints protegidos.
 */
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

        if (header != null && header.startsWith(BEARER_PREFIX)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

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

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
