package py.edu.uc.lp3.me.cs2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.edu.uc.lp3.me.cs2.domain.Equipo;
import py.edu.uc.lp3.me.cs2.domain.Jugador;

import java.util.List;
import java.util.Optional;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    Optional<Jugador> findByNombreIgnoreCase(String nombre);

    List<Jugador> findByEquipo(Equipo equipo);

    List<Jugador> findByVivoTrue();
}
