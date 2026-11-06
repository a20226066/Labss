package pe.edu.gtics.servidor.entity;

import jakarta.persistence.*;

// Cada endpoint pertenece a una API: relación muchos-a-uno del curso.
@Entity
@Table(name = "endpoints")
public class EndpointCatalogo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String ruta;

    @Column(name = "metodo_http")
    private String metodoHttp;

    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "api_id", nullable = false)
    private ApiCatalogo api;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getRuta() { return ruta; }
    public void setRuta(String ruta) { this.ruta = ruta; }
    public String getMetodoHttp() { return metodoHttp; }
    public void setMetodoHttp(String metodoHttp) { this.metodoHttp = metodoHttp; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public ApiCatalogo getApi() { return api; }
    public void setApi(ApiCatalogo api) { this.api = api; }
}
