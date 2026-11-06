package pe.edu.gtics.servidor.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.gtics.servidor.dto.ApiSolicitud;
import pe.edu.gtics.servidor.entity.ApiCatalogo;
import pe.edu.gtics.servidor.service.ApiService;
import java.util.List;

// API REST: GET lee, POST crea, PUT edita, DELETE borra.
@RestController
@RequestMapping("/api/api")
public class ApiRestController {
    private final ApiService servicio;
    public ApiRestController(ApiService servicio) { this.servicio = servicio; }

    @GetMapping
    public List<ApiCatalogo> listar() { return servicio.listar(); }

    @GetMapping("/{id}")
    public ApiCatalogo porId(@PathVariable Integer id) { return servicio.obtener(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiCatalogo crear(@Valid @RequestBody ApiSolicitud datos) {
        // @RequestBody: convierte el JSON de entrada en un objeto Java.
        // @Valid: valida las anotaciones del DTO (clase Validación de formularios).
        return servicio.crear(datos);
    }

    @PutMapping("/{id}")
    public ApiCatalogo editar(@PathVariable Integer id, @Valid @RequestBody ApiSolicitud datos) {
        return servicio.editar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) { servicio.eliminar(id); }
}
