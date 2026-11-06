package pe.edu.gtics.servidor.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// JWT: requisito NUEVO del laboratorio (no es una anotación JPA ni un Query Method).
// Un token es un texto firmado. NO contiene la contraseña.
@Service
public class JwtService {
    private final SecretKey clave;
    private final long duracionSegundos;

    public JwtService(@Value("${seguridad.jwt.secret}") String secreto,
                      @Value("${seguridad.jwt.duracion-segundos}") long duracionSegundos) {
        // HS256 requiere como mínimo 32 bytes de clave.
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.duracionSegundos = duracionSegundos;
    }

    public String generar(String usuario, String rol) {
        Date ahora = new Date();
        Date vencimiento = new Date(ahora.getTime() + duracionSegundos * 1000);
        return Jwts.builder()
                .subject(usuario)          // Usuario que inició sesión.
                .claim("rol", rol)         // Autoridad validada al iniciar sesión.
                .issuedAt(ahora)
                .expiration(vencimiento)  // Expira después de 1 hora (configurable).
                .signWith(clave, Jwts.SIG.HS256)
                .compact();
    }

    public Claims leerYVerificar(String token) {
        // Verifica firma y vencimiento. Si falla, la librería lanza una excepción.
        return Jwts.parser().verifyWith(clave).build()
                .parseSignedClaims(token).getPayload();
    }

    public long getDuracionSegundos() { return duracionSegundos; }
}
