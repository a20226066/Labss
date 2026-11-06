package pe.edu.gtics.servidor.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.gtics.servidor.dto.TokenRespuesta;
import pe.edu.gtics.servidor.security.JwtService;

@RestController
public class LoginRestController {
    private final JwtService jwtService;
    public LoginRestController(JwtService jwtService) { this.jwtService = jwtService; }

    @GetMapping("/login")
    public TokenRespuesta login(Authentication usuarioAutenticado) {
        // No validamos la contraseña aquí: ya lo hizo Spring Security con BASIC + BCrypt.
        String rol = usuarioAutenticado.getAuthorities().iterator().next().getAuthority();
        String token = jwtService.generar(usuarioAutenticado.getName(), rol);
        return new TokenRespuesta(token, rol.replace("ROLE_", ""),
                jwtService.getDuracionSegundos());
    }
}
