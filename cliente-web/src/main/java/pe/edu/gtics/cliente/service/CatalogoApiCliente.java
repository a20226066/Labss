package pe.edu.gtics.cliente.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pe.edu.gtics.cliente.dto.*;

import java.util.Arrays;
import java.util.List;

// Este es el ÚNICO archivo con llamadas HTTP al servidor.
// RestClient es la parte adicional necesaria para CONSUMIR servicios web.
// El resto utiliza @Controller, Model y Thymeleaf como en tus diapositivas.
@Service
public class CatalogoApiCliente {
    private final RestClient restClient;

    public CatalogoApiCliente(RestClient.Builder builder,
                              @Value("${servidor.api.base-url}") String url) {
        // Builder de Spring Boot: usa el mismo ObjectMapper y snake_case configurado.
        restClient = builder.baseUrl(url).build();
    }

    public TokenRespuesta iniciarSesion(String usuario, String contrasena) {
        // Authorization: Basic = usuario + contraseña, SOLO al solicitar el token.
        return restClient.get().uri("/login")
                .headers(cabecera -> cabecera.setBasicAuth(usuario, contrasena))
                .retrieve().body(TokenRespuesta.class);
    }

    public List<ApiResumen> listar(String token) {
        // Authorization: Bearer <token>: a partir de aquí NO se envía la contraseña.
        ApiResumen[] datos = restClient.get().uri("/api/api")
                .headers(cabecera -> cabecera.setBearerAuth(token))
                .retrieve().body(ApiResumen[].class);
        return datos == null ? List.of() : Arrays.asList(datos);
    }

    public ApiResumen buscarPorId(String token, Integer id) {
        return restClient.get().uri("/api/api/{id}", id)
                .headers(cabecera -> cabecera.setBearerAuth(token))
                .retrieve().body(ApiResumen.class);
    }

    public List<EquipoResumen> listarEquipos(String token) {
        EquipoResumen[] equipos = restClient.get().uri("/api/equipo")
                .headers(cabecera -> cabecera.setBearerAuth(token))
                .retrieve().body(EquipoResumen[].class);
        return equipos == null ? List.of() : Arrays.asList(equipos);
    }

    public void crear(String token, ApiFormulario datos) {
        // POST + JSON. Jackson convierte fechaRegistro a fecha_registro.
        restClient.post().uri("/api/api")
                .headers(cabecera -> cabecera.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON).body(datos)
                .retrieve().toBodilessEntity();
    }

    public void editar(String token, ApiFormulario datos) {
        restClient.put().uri("/api/api/{id}", datos.getId())
                .headers(cabecera -> cabecera.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON).body(datos)
                .retrieve().toBodilessEntity();
    }

    public void eliminar(String token, Integer id) {
        restClient.delete().uri("/api/api/{id}", id)
                .headers(cabecera -> cabecera.setBearerAuth(token))
                .retrieve().toBodilessEntity();
    }
}
