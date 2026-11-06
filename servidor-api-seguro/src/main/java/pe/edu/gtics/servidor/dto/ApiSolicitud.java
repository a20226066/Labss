package pe.edu.gtics.servidor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

// DTO: datos que el cliente MANDA en POST/PUT. Evita recibir una entidad JPA completa.
// Jackson traduce equipo_id (JSON) a equipoId (Java) por application.properties.
public class ApiSolicitud {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre debe tener máximo 150 caracteres")
    private String nombre;

    @NotBlank(message = "La versión es obligatoria")
    private String version;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fechaRegistro;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    @NotNull(message = "Debe elegir un equipo")
    private Integer equipoId;

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
