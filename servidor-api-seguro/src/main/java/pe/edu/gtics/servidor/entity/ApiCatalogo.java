package pe.edu.gtics.servidor.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

// Tabla "apis". No se usa @OneToMany: así evitamos referencias circulares en JSON.
@Entity
@Table(name = "apis")
public class ApiCatalogo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String version;

    // LocalDate: solo fecha, como en la guía de fechas/horas de JPA.
    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    private String estado;

    // Muchas APIs pertenecen a un equipo. En BD la FK se llama equipo_id.
    @ManyToOne
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

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
    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }
}
