package pe.edu.gtics.cliente.dto;

import java.time.LocalDate;

// Forma del JSON que DEVUELVE el servidor. Tiene un Equipo anidado.
public class ApiResumen {
    private Integer id;
    private String nombre;
    private String version;
    private LocalDate fechaRegistro;
    private String estado;
    private EquipoResumen equipo;

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
    public EquipoResumen getEquipo() { return equipo; }
    public void setEquipo(EquipoResumen equipo) { this.equipo = equipo; }
}
