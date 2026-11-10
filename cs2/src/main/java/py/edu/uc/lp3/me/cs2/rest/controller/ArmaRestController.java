package py.edu.uc.lp3.me.cs2.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.me.cs2.domain.Arma;
import py.edu.uc.lp3.me.cs2.service.ArmaService;

import java.util.List;

/**
 * Capa de Presentación REST para el CRUD de Armas.
 * Arquitectura de 3 capas: Delega validación y lógica de negocio a ArmaService.
 */
@RestController
@RequestMapping("/api/armas")
public class ArmaRestController {

    private final ArmaService armaService;

    public ArmaRestController(ArmaService armaService) {
        this.armaService = armaService;
    }

    /**
     * Listar todas las armas persistidas.
     * GET /api/armas
     */
    @GetMapping
    public ResponseEntity<List<Arma>> listarTodas() {
        return ResponseEntity.ok(armaService.listarTodas());
    }

    /**
     * Obtener un arma por su ID.
     * GET /api/armas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Arma> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(armaService.obtenerPorId(id));
    }

    /**
     * Crear una nueva arma (pasa por la capa de validación del servicio).
     * POST /api/armas
     */
    @PostMapping
    public ResponseEntity<Arma> crear(@RequestBody Arma arma) {
        Arma nuevaArma = armaService.crear(arma);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaArma);
    }

    /**
     * Actualizar los datos de un arma existente por su ID.
     * PUT /api/armas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Arma> actualizar(@PathVariable Long id, @RequestBody Arma arma) {
        Arma armaActualizada = armaService.actualizar(id, arma);
        return ResponseEntity.ok(armaActualizada);
    }

    /**
     * Eliminar un arma por su ID.
     * DELETE /api/armas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        armaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Disparar o lanzar un arma persistida (consume munición o detona la granada en H2).
     * POST /api/armas/{id}/disparar?balas=1
     */
    @PostMapping("/{id}/disparar")
    public ResponseEntity<Arma> disparar(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int balas) {
        return ResponseEntity.ok(armaService.dispararArma(id, balas));
    }

    /**
     * Recargar un arma de fuego persistida en H2.
     * POST /api/armas/{id}/recargar
     */
    @PostMapping("/{id}/recargar")
    public ResponseEntity<Arma> recargar(@PathVariable Long id) {
        return ResponseEntity.ok(armaService.recargarArma(id));
    }
}
