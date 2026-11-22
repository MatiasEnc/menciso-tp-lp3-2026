package py.edu.uc.lp3.me.cs2.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.me.cs2.domain.Pistola;
import py.edu.uc.lp3.me.cs2.service.ArmaService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para endpoints de demostración de constructores, polimorfismo
 * e interfaz UICapable. Respeta estrictamente la arquitectura de 3 capas delegando
 * toda operación, validación y consulta a ArmaService.
 */
@RestController
@RequestMapping("/armas")
public class ArmaController {

    private final ArmaService armaService;

    public ArmaController(ArmaService armaService) {
        this.armaService = armaService;
    }

    /**
     * Construcción completa por URL usando constructor principal a través de ArmaService.
     * Ejemplo: GET /armas/crear?nombre=Glock-18&precio=200&dano=28&balas=20
     */
    @GetMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearArma(
            @RequestParam String nombre,
            @RequestParam int precio,
            @RequestParam(defaultValue = "AMBOS") String equipo,
            @RequestParam(defaultValue = "25.0") double dano,
            @RequestParam(defaultValue = "15") int balas,
            @RequestParam(required = false) String imagen,
            @RequestParam(required = false) String sonido) {

        Pistola nuevaPistola = armaService.instanciarPistolaCompleta(nombre, precio, equipo, dano, balas, imagen, sonido);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("estado", "Instanciada exitosamente (constructor completo)");
        resp.put("nombre", nuevaPistola.getNombre());
        resp.put("precio", nuevaPistola.getPrecio());
        resp.put("equipo", nuevaPistola.getEquipo());
        resp.put("imagen", nuevaPistola.getImagen());
        resp.put("sonido", nuevaPistola.getSonido());
        resp.put("comportamiento", nuevaPistola.ejecutarAccion());
        return ResponseEntity.ok(resp);
    }

    /**
     * Construcción por URL usando constructor simple sobrecargado a través de ArmaService.
     * Ejemplo: GET /armas/crear-simple?nombre=Desert-Eagle&precio=700&dano=53.0
     */
    @GetMapping("/crear-simple")
    public ResponseEntity<Map<String, Object>> crearArmaSimple(
            @RequestParam String nombre,
            @RequestParam int precio,
            @RequestParam double dano) {

        Pistola pistolaSimple = armaService.instanciarPistolaSimple(nombre, precio, dano);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("estado", "Instanciada exitosamente (constructor sobrecargado simple)");
        resp.put("nombre", pistolaSimple.getNombre());
        resp.put("precio", pistolaSimple.getPrecio());
        resp.put("equipo", pistolaSimple.getEquipo());
        resp.put("imagen", pistolaSimple.getImagen());
        resp.put("sonido", pistolaSimple.getSonido());
        resp.put("comportamiento", pistolaSimple.ejecutarAccion());
        return ResponseEntity.ok(resp);
    }

    /**
     * Endpoint polimórfico (Sobreescritura):
     * Delega en ArmaService para invocar ejecutarAccion() sobre el tipo padre 'Arma'.
     * Ejemplo: GET /armas/comportamiento
     */
    @GetMapping("/comportamiento")
    public ResponseEntity<List<Map<String, Object>>> obtenerComportamiento() {
        return ResponseEntity.ok(armaService.obtenerComportamientoPolimorfico());
    }

    /**
     * Endpoint polimórfico demostrando la Sobrecarga de método de dominio:
     * Delega en ArmaService para invocar ejecutarAccion(repeticiones) sobre el tipo padre 'Arma'.
     * Ejemplo: GET /armas/comportamiento-sobrecargado?repeticiones=3
     */
    @GetMapping("/comportamiento-sobrecargado")
    public ResponseEntity<List<Map<String, Object>>> obtenerComportamientoSobrecargado(
            @RequestParam(defaultValue = "2") int repeticiones) {
        return ResponseEntity.ok(armaService.obtenerComportamientoSobrecargado(repeticiones));
    }

    /**
     * Catálogo UI (Consigna 3 - Interfaz del dominio):
     * Delega en ArmaService para devolver elementos persistidos de dos familias distintas (Arma y Jugador)
     * utilizando la interfaz común UICapable.
     * Ejemplo: GET /armas/catalogo-ui
     */
    @GetMapping("/catalogo-ui")
    public ResponseEntity<List<Map<String, Object>>> catalogoUI() {
        return ResponseEntity.ok(armaService.obtenerCatalogoUI());
    }
}
