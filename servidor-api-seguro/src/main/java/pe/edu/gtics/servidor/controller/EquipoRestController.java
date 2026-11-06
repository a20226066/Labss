package pe.edu.gtics.servidor.controller;

import org.springframework.web.bind.annotation.*;
import pe.edu.gtics.servidor.entity.Equipo;
import pe.edu.gtics.servidor.exception.RecursoNoEncontradoException;
import pe.edu.gtics.servidor.repository.EquipoRepository;
import java.util.List;

@RestController // Diferencia con @Controller: devuelve JSON en vez de una vista Thymeleaf.
@RequestMapping("/api/equipo")
public class EquipoRestController {
    private final EquipoRepository repository;
    public EquipoRestController(EquipoRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Equipo> listar() { return repository.findAll(); }

    @GetMapping("/{id}")
    public Equipo buscar(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("No existe el equipo " + id));
    }
}
