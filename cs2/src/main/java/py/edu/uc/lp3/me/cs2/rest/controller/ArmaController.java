package py.edu.uc.lp3.me.cs2.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.me.cs2.domain.*;
import py.edu.uc.lp3.me.cs2.exceptions.ArmaException;

import java.util.*;

@RestController
@RequestMapping("/armas")
public class ArmaController {

    /**
     * Construcción completa por URL usando constructor principal.
     * Ejemplo: GET /armas/crear?nombre=Glock-18&precio=200&dano=28&balas=20
     */
    @GetMapping("/crear")
    public ResponseEntity<?> crearArma(
            @RequestParam String nombre,
            @RequestParam int precio,
            @RequestParam(defaultValue = "AMBOS") String equipo,
            @RequestParam(defaultValue = "25.0") double dano,
            @RequestParam(defaultValue = "15") int balas) {
        try {
            Equipo eq = Equipo.valueOf(equipo.toUpperCase());
            Pistola nuevaPistola = new Pistola(nombre, precio, eq, dano, 75.0, balas, balas * 3, 2.2, false);
            
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("estado", "Instanciada exitosamente (constructor completo)");
            resp.put("nombre", nuevaPistola.getNombre());
            resp.put("precio", nuevaPistola.getPrecio());
            resp.put("equipo", nuevaPistola.getEquipo());
            resp.put("comportamiento", nuevaPistola.ejecutarAccion());
            return ResponseEntity.ok(resp);
        } catch (ArmaException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Construcción por URL usando constructor simple sobrecargado.
     * Ejemplo: GET /armas/crear-simple?nombre=Desert-Eagle&precio=700&dano=53.0
     */
    @GetMapping("/crear-simple")
    public ResponseEntity<?> crearArmaSimple(
            @RequestParam String nombre,
            @RequestParam int precio,
            @RequestParam double dano) {
        try {
            Pistola pistolaSimple = new Pistola(nombre, precio, dano);
            
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("estado", "Instanciada exitosamente (constructor sobrecargado simple)");
            resp.put("nombre", pistolaSimple.getNombre());
            resp.put("precio", pistolaSimple.getPrecio());
            resp.put("equipo", pistolaSimple.getEquipo());
            resp.put("comportamiento", pistolaSimple.ejecutarAccion());
            return ResponseEntity.ok(resp);
        } catch (ArmaException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint polimórfico (Sobreescritura):
     * Habla con las clases hijas únicamente a través del tipo padre 'Arma'.
     * Ejemplo: GET /armas/comportamiento
     */
    @GetMapping("/comportamiento")
    public ResponseEntity<List<Map<String, Object>>> obtenerComportamiento() {
        List<Arma> arsenal = List.of(
            new Pistola("USP-S", 200, Equipo.COUNTER_TERRORISTA, 35.0, 85.0, 12, 24, 2.2, true),
            new Granada("Granada Flashbang", 200, Equipo.AMBOS, 0.0, 10.0, 1.5, false, "Ceguera temporal")
        );

        List<Map<String, Object>> respuesta = new ArrayList<>();
        for (Arma arma : arsenal) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("arma", arma.getNombre());
            item.put("precio", arma.getPrecio());
            item.put("equipo", arma.getEquipo());
            item.put("resultadoAccion", arma.ejecutarAccion());
            respuesta.add(item);
        }
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Endpoint polimórfico demostrando la Sobrecarga de método de dominio:
     * Invoca ejecutarAccion(repeticiones) sobre el tipo padre 'Arma'.
     * Ejemplo: GET /armas/comportamiento-sobrecargado?repeticiones=3
     */
    @GetMapping("/comportamiento-sobrecargado")
    public ResponseEntity<?> obtenerComportamientoSobrecargado(
            @RequestParam(defaultValue = "2") int repeticiones) {
        try {
            List<Arma> arsenal = List.of(
                new Pistola("Glock-18", 200, Equipo.TERRORISTA, 28.0, 75.0, 20, 60, 2.2, false),
                new Granada("Granada HE", 300, Equipo.AMBOS, 98.0, 8.0, 2.0, true, "Explosión fragmentaria")
            );

            List<Map<String, Object>> respuesta = new ArrayList<>();
            for (Arma arma : arsenal) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("arma", arma.getNombre());
                item.put("repeticionesOSegundos", repeticiones);
                item.put("resultadoSobrecargado", arma.ejecutarAccion(repeticiones));
                respuesta.add(item);
            }
            return ResponseEntity.ok(respuesta);
        } catch (ArmaException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
