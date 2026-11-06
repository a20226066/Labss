package pe.edu.gtics.servidor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.gtics.servidor.entity.ApiCatalogo;

public interface ApiRepository extends JpaRepository<ApiCatalogo, Integer> { }
