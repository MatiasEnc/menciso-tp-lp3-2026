package py.edu.uc.lp3.me.cs2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.edu.uc.lp3.me.cs2.domain.JugadaEscenario;

@Repository
public interface JugadaEscenarioRepository extends JpaRepository<JugadaEscenario, Long> {
}
