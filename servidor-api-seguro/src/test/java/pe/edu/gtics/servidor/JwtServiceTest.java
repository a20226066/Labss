package pe.edu.gtics.servidor;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import pe.edu.gtics.servidor.security.JwtService;
import static org.junit.jupiter.api.Assertions.*;

// Ejemplo de prueba sin necesitar levantar MySQL ni toda la aplicación.
public class JwtServiceTest {
    private final String clave = "Esta_Es_Una_Clave_De_Prueba_Muy_Larga_12345";

    @Test
    void tokenConservaUsuarioYRol() {
        JwtService jwt = new JwtService(clave, 3600);
        String token = jwt.generar("admin", "ROLE_ADMIN");
        assertEquals("admin", jwt.leerYVerificar(token).getSubject());
        assertEquals("ROLE_ADMIN", jwt.leerYVerificar(token).get("rol", String.class));
    }

    @Test
    void firmaDistintaNoEsValida() {
        JwtService primerServidor = new JwtService(clave, 3600);
        JwtService otroServidor = new JwtService("Otra_Clave_Diferente_De_Prueba_Muy_Larga_ABC987", 3600);
        String token = primerServidor.generar("usuario", "ROLE_USER");
        assertThrows(JwtException.class, () -> otroServidor.leerYVerificar(token));
    }
}
