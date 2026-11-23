package py.edu.uc.lp3.me.cs2;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.me.cs2.domain.JugadaEscenario;
import py.edu.uc.lp3.me.cs2.exceptions.RechazoCompaneroException;
import py.edu.uc.lp3.me.cs2.repository.JugadaEscenarioRepository;
import py.edu.uc.lp3.me.cs2.rest.dto.EscenarioJugadaRequest;
import py.edu.uc.lp3.me.cs2.service.CompaneroApiClient;
import py.edu.uc.lp3.me.cs2.service.EscenarioCruzadoService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EscenarioCruzadoTests {

    @Autowired
    private EscenarioCruzadoService escenarioService;

    @Autowired
    private CompaneroApiClient companeroApiClient;

    @Autowired
    private JugadaEscenarioRepository jugadaRepository;

    private HttpServer mockA2Diana;
    private HttpServer mockA3Fabricio;

    @BeforeEach
    void setUp() throws IOException {
        jugadaRepository.deleteAll();

        // Servidor HTTP embebido simulando A2 (Diana Aquino: POST /api/armas/granadas/simular)
        mockA2Diana = HttpServer.create(new InetSocketAddress(0), 0);
        mockA2Diana.createContext("/api/armas/granadas/simular", exchange -> {
            String query = exchange.getRequestURI().getQuery() != null ? exchange.getRequestURI().getQuery() : "";
            int status = 200;
            String resp = "{\"estado\":\"EXPLOSION_VALIDA\",\"danioFinal\":42.25,\"absorcionKevlar\":\"35%\"}";

            if (query.contains("distancia=-")) {
                status = 400;
                resp = "{\"status\":400,\"error\":\"ParametroInvalidoException\",\"mensaje\":\"La distancia no puede ser negativa\"}";
            } else if (query.contains("equipoAtacante=Terrorista") && query.contains("equipoObjetivo=Terrorista")) {
                status = 422;
                resp = "{\"status\":422,\"error\":\"FuegoAmigoException\",\"mensaje\":\"Fuego amigo bloqueado entre el mismo equipo\"}";
            }

            byte[] bytes = resp.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        mockA2Diana.start();

        // Servidor HTTP embebido simulando A3 (Fabricio Gonzalez: POST /api/reglas/municion)
        mockA3Fabricio = HttpServer.create(new InetSocketAddress(0), 0);
        mockA3Fabricio.createContext("/api/reglas/municion", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            int status = 200;
            String resp = "{\"permitido\":true,\"cargadorAntes\":20,\"balasNecesarias\":3,\"cargadorDespues\":17}";

            if (body.contains("\"disparos\":25") || body.contains("\"cargador\":0")) {
                status = 409;
                resp = "{\"status\":409,\"error\":\"SIN_MUNICION\",\"excepcion\":\"SinMunicionException\",\"mensaje\":\"La munición del cargador no alcanza para la ráfaga\"}";
            }

            byte[] bytes = resp.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        mockA3Fabricio.start();

        companeroApiClient.setA2BaseUrl("http://localhost:" + mockA2Diana.getAddress().getPort());
        companeroApiClient.setA3BaseUrl("http://localhost:" + mockA3Fabricio.getAddress().getPort());
    }

    @AfterEach
    void tearDown() {
        if (mockA2Diana != null) {
            mockA2Diana.stop(0);
        }
        if (mockA3Fabricio != null) {
            mockA3Fabricio.stop(0);
        }
    }

    @Test
    void testEscenarioCaminoFelizA2YA3AceptanYPersisteEnH2() {
        EscenarioJugadaRequest req = new EscenarioJugadaRequest();
        req.setArmaId(1L);
        req.setJugadorObjetivoId(1L);
        req.setEquipoAtacante("Counter-Terrorista");
        req.setEquipoObjetivo("Terrorista");
        req.setDistanciaExplosion(2.0);
        req.setCargadorDisponible(20);
        req.setDisparosRafaga(3);

        JugadaEscenario guardada = escenarioService.ejecutarEscenarioCruzado(req);

        assertNotNull(guardada.getId());
        assertEquals(17, guardada.getCargadorDespues());
        assertEquals("APROBADA_POR_A2_Y_A3_PERSISTIDA_EN_H2", guardada.getEstadoJugada());
        assertEquals(1, escenarioService.listarJugadas().size());
    }

    @Test
    void testRechazoDeA2DianaNoGuardaEnH2YAtribuyeRechazo() {
        EscenarioJugadaRequest req = new EscenarioJugadaRequest();
        req.setEquipoAtacante("Terrorista");
        req.setEquipoObjetivo("Terrorista"); // Provoca 422 FuegoAmigoException en A2 (Diana Aquino)
        req.setDistanciaExplosion(2.0);
        req.setDisparosRafaga(3);

        RechazoCompaneroException ex = assertThrows(RechazoCompaneroException.class,
                () -> escenarioService.ejecutarEscenarioCruzado(req));

        assertEquals("A2", ex.getRolCompanero());
        assertTrue(ex.getRechazadoPor().contains("Diana Aquino"));
        assertEquals(422, ex.getCodigoHttpCompanero());
        assertEquals(0, jugadaRepository.count());
    }

    @Test
    void testRechazoDeA3FabricioNoGuardaEnH2YAtribuyeRechazo() {
        EscenarioJugadaRequest req = new EscenarioJugadaRequest();
        req.setEquipoAtacante("Counter-Terrorista");
        req.setEquipoObjetivo("Terrorista");
        req.setDistanciaExplosion(2.0);
        req.setCargadorDisponible(5);
        req.setDisparosRafaga(25); // Provoca 409 SIN_MUNICION en A3 (Fabricio Gonzalez)

        RechazoCompaneroException ex = assertThrows(RechazoCompaneroException.class,
                () -> escenarioService.ejecutarEscenarioCruzado(req));

        assertEquals("A3", ex.getRolCompanero());
        assertTrue(ex.getRechazadoPor().contains("Fabricio Gonzalez"));
        assertEquals(409, ex.getCodigoHttpCompanero());
        assertEquals(0, jugadaRepository.count());
    }
}
