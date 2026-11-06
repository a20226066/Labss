package pe.edu.gtics.cliente.dto;

// Objetos simples para leer el JSON de /api/equipo.
// El cliente NO tiene JPA ni base de datos: la información viene del servidor.
public class EquipoResumen {
    private Integer id;
    private String nombre;
    private String area;
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
}
