package pe.edu.gtics.servidor.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pe.edu.gtics.servidor.exception.TokenExpiradoException;
import pe.edu.gtics.servidor.exception.TokenInvalidoException;
import java.io.IOException;
import java.util.List;

// FILTRO del tema Spring Security: se ejecuta ANTES que el @RestController.
// Por eso, para llegar al @ControllerAdvice, reenviamos los errores al resolver.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(JwtService jwtService, HandlerExceptionResolver resolver) {
        this.jwtService = jwtService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String cabecera = request.getHeader("Authorization");
        // Si no envía token, Spring Security decidirá que la ruta requiere autenticación.
        if (cabecera == null || !cabecera.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = cabecera.substring(7); // Quitar el texto 'Bearer '.
            Claims datos = jwtService.leerYVerificar(token);
            String rol = datos.get("rol", String.class);
            if (!"ROLE_USER".equals(rol) && !"ROLE_ADMIN".equals(rol)) {
                throw new TokenInvalidoException("El JWT no contiene un rol permitido.");
            }
            var autoridad = new SimpleGrantedAuthority(rol);
            var autenticacion = new UsernamePasswordAuthenticationToken(
                    datos.getSubject(), null, List.of(autoridad));
            SecurityContextHolder.getContext().setAuthentication(autenticacion);
            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            resolver.resolveException(request, response, null, new TokenExpiradoException());
        } catch (JwtException | IllegalArgumentException | TokenInvalidoException e) {
            SecurityContextHolder.clearContext();
            resolver.resolveException(request, response, null,
                    new TokenInvalidoException("El token JWT es inválido o está mal formado."));
        }
    }
}
