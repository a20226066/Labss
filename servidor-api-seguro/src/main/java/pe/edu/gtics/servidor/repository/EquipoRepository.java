package pe.edu.gtics.servidor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.gtics.servidor.entity.Equipo;

// JpaRepository ya trae findAll(), findById() y save().
public interface EquipoRepository extends JpaRepository<Equipo, Integer> { }
