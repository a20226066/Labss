package pe.edu.gtics.servidor.controller;

import org.springframework.web.bind.annotation.*;
import pe.edu.gtics.servidor.entity.EndpointCatalogo;
import pe.edu.gtics.servidor.exception.RecursoNoEncontradoException;
import pe.edu.gtics.servidor.repository.EndpointRepository;
import java.util.List;

@RestController
@RequestMapping("/api/endpoint")
public class EndpointRestController {
    private final EndpointRepository repository;
    public EndpointRestController(EndpointRepository repository) { this.repository = repository; }

    @GetMapping
    public List<EndpointCatalogo> listar() { return repository.findAll(); }

    @GetMapping("/{id}")
    public EndpointCatalogo buscar(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("No existe el endpoint " + id));
    }
}
