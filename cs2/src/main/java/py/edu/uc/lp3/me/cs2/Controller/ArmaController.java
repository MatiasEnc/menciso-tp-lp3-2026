package py.edu.uc.lp3.me.cs2.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.me.cs2.*;

import java.util.*;

@RestController
@RequestMapping("/armas")
public class ArmaController {

    /**
     * Parte E: Construcción por URL con @RequestParam.
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
            resp.put("estado", "Instanciada exitosamente");
            resp.put("nombre", nuevaPistola.getNombre());
            resp.put("precio", nuevaPistola.getPrecio());
            resp.put("equipo", nuevaPistola.getEquipo());
            resp.put("comportamiento", nuevaPistola.ejecutarAccion());
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Parte F: Endpoint JSON con las dos hijas independientes.
     * El controller trata a ambas como el tipo padre 'Arma' (sin ningún if por tipo).
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
}
