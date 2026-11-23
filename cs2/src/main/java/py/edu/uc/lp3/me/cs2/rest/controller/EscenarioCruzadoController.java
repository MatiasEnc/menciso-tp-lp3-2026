package py.edu.uc.lp3.me.cs2.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.me.cs2.domain.JugadaEscenario;
import py.edu.uc.lp3.me.cs2.rest.dto.EscenarioJugadaRequest;
import py.edu.uc.lp3.me.cs2.service.EscenarioCruzadoService;

import java.util.List;

/**
 * Controlador REST del Escenario Cruzado de POO-09 (A1 = Matias Enciso).
 * Orquesta las validaciones contra A2 (Diana Aquino) y A3 (Fabricio Gonzalez)
 * a través de EscenarioCruzadoService y persiste la jugada en H2 solo si ambos aceptan.
 */
@RestController
@RequestMapping("/api/escenario/jugadas")
public class EscenarioCruzadoController {

    private final EscenarioCruzadoService escenarioCruzadoService;

    public EscenarioCruzadoController(EscenarioCruzadoService escenarioCruzadoService) {
        this.escenarioCruzadoService = escenarioCruzadoService;
    }

    /**
     * Ejecuta el escenario cruzado consultando primero a A2 (Diana Aquino) y luego a A3 (Fabricio Gonzalez).
     * Acepta tanto body JSON como parámetros en la URL.
     * POST /api/escenario/jugadas
     */
    @PostMapping
    public ResponseEntity<JugadaEscenario> ejecutarJugada(
            @RequestBody(required = false) EscenarioJugadaRequest bodyRequest,
            @RequestParam(required = false) Long armaId,
            @RequestParam(required = false) Long jugadorObjetivoId,
            @RequestParam(required = false) String equipoAtacante,
            @RequestParam(required = false) String equipoObjetivo,
            @RequestParam(required = false) Double distanciaExplosion,
            @RequestParam(required = false) Integer cargadorDisponible,
            @RequestParam(required = false) Integer disparosRafaga) {

        EscenarioJugadaRequest req = (bodyRequest != null) ? bodyRequest : new EscenarioJugadaRequest();
        if (armaId != null) req.setArmaId(armaId);
        if (jugadorObjetivoId != null) req.setJugadorObjetivoId(jugadorObjetivoId);
        if (equipoAtacante != null) req.setEquipoAtacante(equipoAtacante);
        if (equipoObjetivo != null) req.setEquipoObjetivo(equipoObjetivo);
        if (distanciaExplosion != null) req.setDistanciaExplosion(distanciaExplosion);
        if (cargadorDisponible != null) req.setCargadorDisponible(cargadorDisponible);
        if (disparosRafaga != null) req.setDisparosRafaga(disparosRafaga);

        JugadaEscenario jugadaGuardada = escenarioCruzadoService.ejecutarEscenarioCruzado(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(jugadaGuardada);
    }

    /**
     * Lista todas las jugadas de escenario persistidas en el H2 de A1 (Matias Enciso).
     * GET /api/escenario/jugadas
     */
    @GetMapping
    public ResponseEntity<List<JugadaEscenario>> listarJugadas() {
        return ResponseEntity.ok(escenarioCruzadoService.listarJugadas());
    }

    /**
     * Obtiene una jugada de escenario por su ID en H2.
     * GET /api/escenario/jugadas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<JugadaEscenario> obtenerJugada(@PathVariable Long id) {
        return ResponseEntity.ok(escenarioCruzadoService.obtenerJugadaPorId(id));
    }
}
