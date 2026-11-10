package py.edu.uc.lp3.me.cs2.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.me.cs2.domain.Blindaje;
import py.edu.uc.lp3.me.cs2.domain.Equipo;
import py.edu.uc.lp3.me.cs2.domain.Granada;
import py.edu.uc.lp3.me.cs2.domain.Jugador;
import py.edu.uc.lp3.me.cs2.rest.dto.ResultadoSimulacion;
import py.edu.uc.lp3.me.cs2.rest.dto.SimulacionExplosionRequest;
import py.edu.uc.lp3.me.cs2.service.SimulacionExplosionService;

import java.util.List;

/**
 * Controlador REST para la simulación persistida de combate y explosiones en Counter-Strike 2.
 * Expone las dos reglas de negocio que conectan tres clases del modelo: Arma (Granada/Pistola), Jugador y Blindaje.
 */
@RestController
@RequestMapping("/api/simulacion/explosion")
public class SimulacionExplosionController {

    private final SimulacionExplosionService simulacionService;

    public SimulacionExplosionController(SimulacionExplosionService simulacionService) {
        this.simulacionService = simulacionService;
    }

    /**
     * Endpoint POST que ejecuta la simulación y persiste el nuevo estado en H2.
     * Acepta IDs de entidades persistidas (armaId, jugadorId, distanciaMetros, disparos) o entidades en línea.
     * POST /api/simulacion/explosion
     */
    @PostMapping
    public ResponseEntity<ResultadoSimulacion> simular(@RequestBody(required = false) SimulacionExplosionRequest request) {
        SimulacionExplosionRequest req = (request != null) ? request : new SimulacionExplosionRequest();
        ResultadoSimulacion resultado = simulacionService.simularImpactoPersistido(req);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Endpoint GET para consultar los jugadores persistidos en H2 (permite verificar el estado antes y después de simular).
     * GET /api/simulacion/explosion/jugadores
     */
    @GetMapping("/jugadores")
    public ResponseEntity<List<Jugador>> listarJugadores() {
        return ResponseEntity.ok(simulacionService.listarJugadores());
    }

    /**
     * Endpoint GET para consultar un jugador específico en H2 por ID.
     * GET /api/simulacion/explosion/jugadores/{id}
     */
    @GetMapping("/jugadores/{id}")
    public ResponseEntity<Jugador> obtenerJugador(@PathVariable Long id) {
        return ResponseEntity.ok(simulacionService.obtenerJugadorPorId(id));
    }

    /**
     * Endpoint GET interactivo con parámetros en URL para pruebas rápidas.
     * Ejemplo: GET /api/simulacion/explosion?distancia=2.5&conBlindaje=true&fuegoAmigo=false
     */
    @GetMapping
    public ResponseEntity<ResultadoSimulacion> simularParametros(
            @RequestParam(defaultValue = "2.5") double distancia,
            @RequestParam(defaultValue = "true") boolean conBlindaje,
            @RequestParam(defaultValue = "false") boolean fuegoAmigo) {

        Equipo equipoJugador = fuegoAmigo ? Equipo.TERRORISTA : Equipo.COUNTER_TERRORISTA;
        Blindaje blindaje = conBlindaje ? new Blindaje("Chaleco Kevlar", 100.0, 50.0) : null;

        Granada granada = new Granada("Granada HE", 300, Equipo.TERRORISTA, 98.0, 8.0, 2.0, true, "Explosión fragmentaria");
        Jugador jugador = new Jugador("Player-Test", 100.0, equipoJugador, blindaje);

        ResultadoSimulacion resultado = simulacionService.simularExplosion(granada, jugador, blindaje, distancia);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Endpoint demostrativo preconfigurado listo para navegador (persiste el impacto en H2).
     * GET /api/simulacion/explosion/demo
     */
    @GetMapping("/demo")
    public ResponseEntity<ResultadoSimulacion> demo() {
        SimulacionExplosionRequest req = new SimulacionExplosionRequest();
        req.setDistanciaMetros(2.0);
        req.setDisparos(2);
        ResultadoSimulacion resultado = simulacionService.simularImpactoPersistido(req);
        return ResponseEntity.ok(resultado);
    }
}
