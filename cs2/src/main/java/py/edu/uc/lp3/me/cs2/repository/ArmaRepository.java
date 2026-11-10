package py.edu.uc.lp3.me.cs2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.edu.uc.lp3.me.cs2.domain.Arma;
import py.edu.uc.lp3.me.cs2.domain.Equipo;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArmaRepository extends JpaRepository<Arma, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Arma> findByNombreIgnoreCase(String nombre);

    List<Arma> findByEquipo(Equipo equipo);

    List<Arma> findByActivoTrue();
}
