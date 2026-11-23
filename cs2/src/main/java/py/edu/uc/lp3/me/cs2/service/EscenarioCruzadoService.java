package py.edu.uc.lp3.me.cs2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.me.cs2.domain.Arma;
import py.edu.uc.lp3.me.cs2.domain.ArmaDeFuego;
import py.edu.uc.lp3.me.cs2.domain.JugadaEscenario;
import py.edu.uc.lp3.me.cs2.domain.Jugador;
import py.edu.uc.lp3.me.cs2.exceptions.ParametroInvalidoException;
import py.edu.uc.lp3.me.cs2.exceptions.ResourceNotFoundException;
import py.edu.uc.lp3.me.cs2.repository.ArmaRepository;
import py.edu.uc.lp3.me.cs2.repository.JugadaEscenarioRepository;
import py.edu.uc.lp3.me.cs2.repository.JugadorRepository;
import py.edu.uc.lp3.me.cs2.rest.dto.EscenarioJugadaRequest;

import java.util.List;

/**
 * Servicio que orquesta el Escenario Cruzado de POO-09 ("Asalto Táctico Coordinado CS2: Granada + Ráfaga"):
 * 1. Consulta primero a A2 (Diana Aquino) para validar la explosión de granada de apertura (distancia y ausencia de fuego amigo).
 * 2. Consulta luego a A3 (Fabricio Gonzalez) para validar que el cargador alcanza para la ráfaga de disparos de remate.
 * 3. Solo si AMBOS compañeros responden 200 OK, actualiza el arma y el jugador en el H2 local y guarda la JugadaEscenario.
 * Si cualquiera de los dos rechaza, el H2 de A1 (Matias Enciso) queda intacto y se informa de quién fue el rechazo.
 */
@Service
@Transactional
public class EscenarioCruzadoService {

    private final CompaneroApiClient companeroApiClient;
    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;
    private final JugadaEscenarioRepository jugadaEscenarioRepository;
    private final SimulacionExplosionService simulacionExplosionService;

    public EscenarioCruzadoService(CompaneroApiClient companeroApiClient,
                                   ArmaRepository armaRepository,
                                   JugadorRepository jugadorRepository,
                                   JugadaEscenarioRepository jugadaEscenarioRepository,
                                   SimulacionExplosionService simulacionExplosionService) {
        this.companeroApiClient = companeroApiClient;
        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
        this.jugadaEscenarioRepository = jugadaEscenarioRepository;
        this.simulacionExplosionService = simulacionExplosionService;
    }

    public JugadaEscenario ejecutarEscenarioCruzado(EscenarioJugadaRequest request) {
        if (request == null) {
            request = new EscenarioJugadaRequest();
        }

        Long armaId = request.getArmaId() != null ? request.getArmaId() : 1L;
        Long jugadorId = request.getJugadorObjetivoId() != null ? request.getJugadorObjetivoId() : 1L;

        Arma arma = armaRepository.findById(armaId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el arma con ID: " + armaId));
        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el jugador con ID: " + jugadorId));

        String equipoAtacante = (request.getEquipoAtacante() != null && !request.getEquipoAtacante().isBlank())
                ? request.getEquipoAtacante()
                : "Counter-Terrorista";
        String equipoObjetivo = (request.getEquipoObjetivo() != null && !request.getEquipoObjetivo().isBlank())
                ? request.getEquipoObjetivo()
                : "Terrorista";
        double distancia = request.getDistanciaExplosion();
        int disparos = request.getDisparosRafaga() != 0 ? request.getDisparosRafaga() : 3;

        int cargadorAntes = (request.getCargadorDisponible() != null)
                ? request.getCargadorDisponible()
                : ((arma instanceof ArmaDeFuego af) ? af.getMunicionCargador() : 1);

        // Paso 1: Consultar validación de A2 (Diana Aquino — Explosión de granada y Fuego Amigo)
        String respuestaA2 = companeroApiClient.consultarReglaA2Diana(equipoAtacante, equipoObjetivo, distancia);

        // Paso 2: Consultar validación de A3 (Fabricio Gonzalez — Munición suficiente para la ráfaga)
        String respuestaA3 = companeroApiClient.consultarReglaA3Fabricio(cargadorAntes, disparos);

        // Paso 3: Ambas APIs aceptaron (200 OK). Aplicamos y persistimos la jugada en el H2 local de A1
        double vidaAntes = jugador.getVida();
        int cargadorDespues = Math.max(0, cargadorAntes - disparos);
        double danioBaseArma = 28.0;

        if (arma instanceof ArmaDeFuego armaDeFuego) {
            danioBaseArma = armaDeFuego.getDaño();
            armaDeFuego.setMunicionCargador(cargadorDespues);
            armaRepository.save(armaDeFuego);
        }

        double danioTotalImpacto = Math.max(10.0, danioBaseArma * disparos * 0.5);
        simulacionExplosionService.aplicarImpactoAJugador(jugador, danioTotalImpacto);
        jugadorRepository.save(jugador);

        double durabilidadRestante = (jugador.getBlindaje() != null)
                ? Math.round(jugador.getBlindaje().getDurabilidad() * 100.0) / 100.0
                : 0.0;
        double vidaDespues = Math.round(jugador.getVida() * 100.0) / 100.0;

        JugadaEscenario jugada = new JugadaEscenario(
                "Asalto Táctico Coordinado CS2 (Granada A2 + Ráfaga A3)",
                arma.getId(),
                arma.getNombre(),
                jugador.getId(),
                jugador.getNombre(),
                equipoAtacante,
                equipoObjetivo,
                distancia,
                cargadorAntes,
                disparos,
                cargadorDespues,
                vidaAntes,
                vidaDespues,
                durabilidadRestante,
                recortar(respuestaA2),
                recortar(respuestaA3),
                "APROBADA_POR_A2_Y_A3_PERSISTIDA_EN_H2"
        );

        return jugadaEscenarioRepository.save(jugada);
    }

    @Transactional(readOnly = true)
    public List<JugadaEscenario> listarJugadas() {
        return jugadaEscenarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public JugadaEscenario obtenerJugadaPorId(Long id) {
        if (id == null || id <= 0) {
            throw new ParametroInvalidoException("El ID de la jugada debe ser un entero positivo.");
        }
        return jugadaEscenarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la jugada de escenario con ID: " + id));
    }

    private String recortar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.length() > 950 ? texto.substring(0, 950) : texto;
    }
}
