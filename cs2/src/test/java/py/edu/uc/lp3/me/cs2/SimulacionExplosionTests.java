package py.edu.uc.lp3.me.cs2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.me.cs2.domain.Blindaje;
import py.edu.uc.lp3.me.cs2.domain.Equipo;
import py.edu.uc.lp3.me.cs2.domain.Granada;
import py.edu.uc.lp3.me.cs2.domain.Jugador;
import py.edu.uc.lp3.me.cs2.exceptions.ValidationException;
import py.edu.uc.lp3.me.cs2.repository.ArmaRepository;
import py.edu.uc.lp3.me.cs2.repository.JugadorRepository;
import py.edu.uc.lp3.me.cs2.rest.dto.ResultadoSimulacion;
import py.edu.uc.lp3.me.cs2.rest.dto.SimulacionExplosionRequest;
import py.edu.uc.lp3.me.cs2.service.SimulacionExplosionService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SimulacionExplosionTests {

    @Autowired
    private SimulacionExplosionService simulacionService;

    @Autowired
    private ArmaRepository armaRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Test
    void testExplosionConBlindajeMitigaDanio() {
        Granada granada = new Granada("Granada HE", 300, Equipo.TERRORISTA, 98.0, 8.0, 2.0, true, "Explosión");
        Blindaje blindaje = new Blindaje("Kevlar", 100.0, 50.0);
        Jugador jugador = new Jugador("CT-1", 100.0, Equipo.COUNTER_TERRORISTA, blindaje);

        // A 2 metros de distancia en radio de 8 metros (factor proximidad = 0.75 -> daño aten. = 73.5)
        ResultadoSimulacion resultado = simulacionService.simularExplosion(granada, jugador, blindaje, 2.0);

        assertTrue(resultado.isEnRadioAlcance());
        assertFalse(resultado.isFuegoAmigo());
        assertEquals(73.5, resultado.getDanioAtenuadoDistancia(), 0.1);
        assertEquals(36.75, resultado.getDanioAbsorbidoBlindaje(), 0.1);
        assertEquals(36.75, resultado.getDanioFinalVida(), 0.1);
        assertEquals(63.25, resultado.getVidaRestante(), 0.1);
        assertEquals("HERIDO / SOBREVIVIO", resultado.getEstadoJugador());
    }

    @Test
    void testSimulacionPersisteCambiosEnH2() {
        Granada he = armaRepository.save(new Granada("Granada HE Test", 300, Equipo.TERRORISTA, 98.0, 8.0, 2.0, true, "Explosión"));
        Jugador ct = jugadorRepository.save(new Jugador("CT-Persistido", 100.0, Equipo.COUNTER_TERRORISTA, new Blindaje("Kevlar", 100.0, 50.0)));

        SimulacionExplosionRequest req = new SimulacionExplosionRequest();
        req.setArmaId(he.getId());
        req.setJugadorId(ct.getId());
        req.setDistanciaMetros(2.0);

        ResultadoSimulacion res = simulacionService.simularImpactoPersistido(req);
        assertTrue(res.isPersistidoEnH2());

        Jugador jugadorDespues = jugadorRepository.findById(ct.getId()).orElseThrow();
        assertEquals(63.25, jugadorDespues.getVida(), 0.1);
        assertTrue(jugadorDespues.getBlindaje().getDurabilidad() < 100.0);
    }

    @Test
    void testExplosionSinBlindajeDanioCompleto() {
        Granada granada = new Granada("Granada HE", 300, Equipo.TERRORISTA, 98.0, 8.0, 2.0, true, "Explosión");
        Jugador jugador = new Jugador("CT-SinBlindaje", 100.0, Equipo.COUNTER_TERRORISTA, null);

        ResultadoSimulacion resultado = simulacionService.simularExplosion(granada, jugador, null, 2.0);

        assertTrue(resultado.isEnRadioAlcance());
        assertEquals(0.0, resultado.getDanioAbsorbidoBlindaje());
        assertEquals(73.5, resultado.getDanioFinalVida(), 0.1);
        assertEquals(26.5, resultado.getVidaRestante(), 0.1);
    }

    @Test
    void testExplosionFueraDeRadioCeroDanio() {
        Granada granada = new Granada("Granada HE", 300, Equipo.TERRORISTA, 98.0, 8.0, 2.0, true, "Explosión");
        Jugador jugador = new Jugador("CT-Lejos", 100.0, Equipo.COUNTER_TERRORISTA);

        ResultadoSimulacion resultado = simulacionService.simularExplosion(granada, jugador, null, 12.0);

        assertFalse(resultado.isEnRadioAlcance());
        assertEquals(0.0, resultado.getDanioFinalVida());
        assertEquals(100.0, resultado.getVidaRestante());
    }

    @Test
    void testExplosionFuegoAmigoReduceDanio() {
        // Mismo equipo (TERRORISTA)
        Granada granada = new Granada("Granada HE", 300, Equipo.TERRORISTA, 98.0, 8.0, 2.0, true, "Explosión");
        Jugador companiero = new Jugador("T-Amigo", 100.0, Equipo.TERRORISTA, null);

        ResultadoSimulacion resultado = simulacionService.simularExplosion(granada, companiero, null, 2.0);

        assertTrue(resultado.isFuegoAmigo());
        // Daño normal 73.5 * 0.35 = 25.73
        assertEquals(25.73, resultado.getDanioFinalVida(), 0.1);
        assertEquals(74.27, resultado.getVidaRestante(), 0.1);
    }

    @Test
    void testExplosionEliminaJugadorConPocaVida() {
        Granada granada = new Granada("Granada HE", 300, Equipo.TERRORISTA, 98.0, 8.0, 2.0, true, "Explosión");
        Jugador jugadorDebil = new Jugador("CT-Debil", 20.0, Equipo.COUNTER_TERRORISTA, null);

        ResultadoSimulacion resultado = simulacionService.simularExplosion(granada, jugadorDebil, null, 1.0);

        assertEquals(0.0, resultado.getVidaRestante());
        assertEquals("ELIMINADO / MUERTO", resultado.getEstadoJugador());
        assertFalse(jugadorDebil.isVivo());
    }

    @Test
    void testDistanciaNegativaLanzaExcepcion() {
        Granada granada = new Granada("Granada HE", 300, 8.0);
        Jugador jugador = new Jugador("CT-Test", 100.0, Equipo.COUNTER_TERRORISTA);

        assertThrows(ValidationException.class, () ->
                simulacionService.simularExplosion(granada, jugador, null, -1.0));
    }
}
