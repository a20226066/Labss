package pe.edu.gtics.servidor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.gtics.servidor.entity.EndpointCatalogo;

public interface EndpointRepository extends JpaRepository<EndpointCatalogo, Integer> { }
