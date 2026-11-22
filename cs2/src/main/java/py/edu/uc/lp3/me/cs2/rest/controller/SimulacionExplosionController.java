package py.edu.uc.lp3.me.cs2.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.me.cs2.domain.Jugador;
import py.edu.uc.lp3.me.cs2.rest.dto.ResultadoSimulacion;
import py.edu.uc.lp3.me.cs2.rest.dto.SimulacionExplosionRequest;
import py.edu.uc.lp3.me.cs2.service.SimulacionExplosionService;

import java.util.List;

/**
 * Controlador REST para la simulación persistida de combate y explosiones en Counter-Strike 2.
 * Respeta la arquitectura de 3 capas delegando la construcción, validación y reglas de negocio
 * exclusivamente a SimulacionExplosionService.
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
     * POST /api/simulacion/explosion
     */
    @PostMapping
    public ResponseEntity<ResultadoSimulacion> simular(@RequestBody(required = false) SimulacionExplosionRequest request) {
        ResultadoSimulacion resultado = simulacionService.simularImpactoPersistido(request);
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
     * Endpoint GET interactivo con parámetros en URL para pruebas de lectura (no muta H2).
     * Ejemplo: GET /api/simulacion/explosion?distancia=2.5&conBlindaje=true&fuegoAmigo=false
     */
    @GetMapping
    public ResponseEntity<ResultadoSimulacion> simularParametros(
            @RequestParam(defaultValue = "2.5") double distancia,
            @RequestParam(defaultValue = "true") boolean conBlindaje,
            @RequestParam(defaultValue = "false") boolean fuegoAmigo) {
        return ResponseEntity.ok(simulacionService.simularConParametros(distancia, conBlindaje, fuegoAmigo));
    }

    /**
     * Endpoint demostrativo de solo lectura listo para navegador (no modifica H2 en un GET).
     * GET /api/simulacion/explosion/demo
     */
    @GetMapping("/demo")
    public ResponseEntity<ResultadoSimulacion> demo() {
        return ResponseEntity.ok(simulacionService.simularDemoLectura());
    }
}
