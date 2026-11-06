package pe.edu.gtics.cliente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

// Este objeto viaja entre formulario y @Controller mediante @ModelAttribute.
// @DateTimeFormat enlaza un <input type="date"> con un LocalDate.
public class ApiFormulario {
    private Integer id; // null -> POST crear, ID existente -> PUT editar.

    @NotBlank(message = "Escribe el nombre de la API")
    private String nombre;

    @NotBlank(message = "Escribe la versión")
    private String version;

    @NotNull(message = "Selecciona una fecha")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRegistro;

    @NotBlank(message = "Elige el estado")
    private String estado;

    @NotNull(message = "Selecciona un equipo")
    private Integer equipoId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getEquipoId() { return equipoId; }
    public void setEquipoId(Integer equipoId) { this.equipoId = equipoId; }
}
