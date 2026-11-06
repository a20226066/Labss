package pe.edu.gtics.servidor.service;

import org.springframework.stereotype.Service;
import pe.edu.gtics.servidor.dto.ApiSolicitud;
import pe.edu.gtics.servidor.entity.ApiCatalogo;
import pe.edu.gtics.servidor.entity.Equipo;
import pe.edu.gtics.servidor.exception.RecursoNoEncontradoException;
import pe.edu.gtics.servidor.repository.ApiRepository;
import pe.edu.gtics.servidor.repository.EquipoRepository;

import java.util.List;

// @Service separa la lógica de negocio del controlador (estilo MVC).
@Service
public class ApiService {
    private final ApiRepository apiRepository;
    private final EquipoRepository equipoRepository;

    // Inyección por constructor, como se trabajó en la clase de Spring Data JPA.
    public ApiService(ApiRepository apiRepository, EquipoRepository equipoRepository) {
        this.apiRepository = apiRepository;
        this.equipoRepository = equipoRepository;
    }

    public List<ApiCatalogo> listar() { return apiRepository.findAll(); }

    public ApiCatalogo obtener(Integer id) {
        // findById devuelve Optional: si no existe, retornamos error HTTP 404.
        return apiRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("No se encontró la API " + id));
    }

    public ApiCatalogo crear(ApiSolicitud datos) {
        ApiCatalogo api = new ApiCatalogo(); // Nuevo objeto -> INSERT con save().
        copiarCampos(api, datos);
        return apiRepository.save(api);
    }

    public ApiCatalogo editar(Integer id, ApiSolicitud datos) {
        ApiCatalogo api = obtener(id); // Objeto existente -> UPDATE con save().
        copiarCampos(api, datos);
        return apiRepository.save(api);
    }

    public void eliminar(Integer id) {
        obtener(id); // Primero verificamos que exista.
        apiRepository.deleteById(id);
        // La FK de endpoints tiene ON DELETE CASCADE en el script de práctica.
    }

    private void copiarCampos(ApiCatalogo api, ApiSolicitud datos) {
        Equipo equipo = equipoRepository.findById(datos.getEquipoId()).orElseThrow(
                () -> new RecursoNoEncontradoException("El equipo no existe: " + datos.getEquipoId()));
        api.setNombre(datos.getNombre());
        api.setVersion(datos.getVersion());
        api.setFechaRegistro(datos.getFechaRegistro());
        api.setEstado(datos.getEstado());
        api.setEquipo(equipo); // La relación @ManyToOne recibe un Equipo, NO solo su ID.
    }
}
