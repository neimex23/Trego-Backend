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
import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.Usuario;
import com.backend.trego.repository.UsuarioRepository;
import com.backend.trego.service.TokenBlacklistService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JWTUtil jwtUtil;
	private final TokenBlacklistService tokenBlacklistService;  // lista negra para evitar que se utilice el mismo token una vez que se dio cerrar sesion
    private final UsuarioRepository usuarioRepository;

	public JwtAuthFilter(JWTUtil jwtUtil, TokenBlacklistService tokenBlacklistService, UsuarioRepository usuarioRepository) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.usuarioRepository = usuarioRepository;
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

                // Verificar que el usuario sigue habilitado (Clientes y Restaurantes pueden ser deshabilitados)
                if (idUsuario != null) {
                    Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
                    if (usuarioOpt.isPresent()) {
                        Usuario u = usuarioOpt.get();
                        boolean deshabilitado = (u instanceof Cliente c && !c.isHabilitado())
                                             || (u instanceof Restaurante r && !r.isHabilitado());
                        if (deshabilitado) {
                            System.out.println("DEBUG: Acceso denegado — usuario deshabilitado: " + email);
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("Usuario deshabilitado.");
                            return;
                        }
                    }
                }

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
