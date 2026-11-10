package py.edu.uc.lp3.me.cs2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.me.cs2.domain.Arma;
import py.edu.uc.lp3.me.cs2.domain.Equipo;
import py.edu.uc.lp3.me.cs2.domain.Granada;
import py.edu.uc.lp3.me.cs2.domain.Pistola;
import py.edu.uc.lp3.me.cs2.exceptions.MunicionInsuficienteException;
import py.edu.uc.lp3.me.cs2.exceptions.ParametroInvalidoException;
import py.edu.uc.lp3.me.cs2.exceptions.ResourceNotFoundException;
import py.edu.uc.lp3.me.cs2.exceptions.ValidationException;
import py.edu.uc.lp3.me.cs2.repository.ArmaRepository;
import py.edu.uc.lp3.me.cs2.service.ArmaService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ArmaCrudTests {

    @Autowired
    private ArmaService armaService;

    @Autowired
    private ArmaRepository armaRepository;

    @BeforeEach
    void setUp() {
        armaRepository.deleteAll();
    }

    @Test
    void testCrearArmaValida() {
        Pistola arma = new Pistola("USP-S Tactical", 200, Equipo.COUNTER_TERRORISTA, 35.0, 85.0, 12, 24, 2.2, true, "usps.png", "silenced_shot.wav");
        arma.setDescripcion("Pistola silenciada táctica");
        Arma guardada = armaService.crear(arma);

        assertNotNull(guardada.getId());
        assertEquals("USP-S Tactical", guardada.getNombre());
        assertEquals(200, guardada.getPrecio());
        assertEquals(Equipo.COUNTER_TERRORISTA, guardada.getEquipo());
        assertNotNull(guardada.getFechaCreacion());
        assertTrue(guardada.getActivo());
        assertTrue(guardada instanceof Pistola);
    }

    @Test
    void testValidacionNombreVacioLanzaExcepcion() {
        assertThrows(ParametroInvalidoException.class, () -> {
            new Pistola("   ", 200, 28.0);
        });
    }

    @Test
    void testValidacionPrecioNegativoLanzaExcepcion() {
        assertThrows(ParametroInvalidoException.class, () -> {
            new Pistola("Glock Negativa", -100, 28.0);
        });
    }

    @Test
    void testValidacionNombreDuplicadoLanzaExcepcion() {
        Arma arma1 = new Pistola("Glock-18", 200, 28.0);
        armaService.crear(arma1);

        Arma arma2 = new Pistola("glock-18", 300, 30.0);
        assertThrows(ValidationException.class, () -> armaService.crear(arma2));
    }

    @Test
    void testActualizarArma() {
        Pistola arma = new Pistola("Desert Eagle", 700, 53.0);
        Arma creada = armaService.crear(arma);

        Pistola datosNuevos = new Pistola("Desert Eagle Blaze", 850, 55.0);
        datosNuevos.setDescripcion("Skin icónica");
        Arma resultado = armaService.actualizar(creada.getId(), datosNuevos);

        assertEquals("Desert Eagle Blaze", resultado.getNombre());
        assertEquals(850, resultado.getPrecio());
        assertEquals("Skin icónica", resultado.getDescripcion());
    }

    @Test
    void testEliminarArma() {
        Arma arma = new Granada("Granada HE", 300, 8.0);
        Arma creada = armaService.crear(arma);

        armaService.eliminar(creada.getId());

        assertThrows(ResourceNotFoundException.class, () -> armaService.obtenerPorId(creada.getId()));
    }

    @Test
    void testListarTodasConPolimorfismo() {
        armaService.crear(new Pistola("Glock-18", 200, 28.0));
        armaService.crear(new Granada("Granada HE", 300, 8.0));

        List<Arma> lista = armaService.listarTodas();
        assertEquals(2, lista.size());
    }

    @Test
    void testExcepcionHijaMunicionInsuficiente() {
        Pistola sinBalas = new Pistola("Glock Vacía", 200, Equipo.TERRORISTA, 28.0, 75.0, 0, 0, 2.2, false);
        Arma guardada = armaService.crear(sinBalas);

        assertThrows(MunicionInsuficienteException.class, () -> armaService.dispararArma(guardada.getId(), 1));
    }
}
