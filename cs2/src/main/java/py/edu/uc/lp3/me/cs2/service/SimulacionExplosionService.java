package py.edu.uc.lp3.me.cs2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.me.cs2.domain.*;
import py.edu.uc.lp3.me.cs2.exceptions.ResourceNotFoundException;
import py.edu.uc.lp3.me.cs2.exceptions.ValidationException;
import py.edu.uc.lp3.me.cs2.repository.ArmaRepository;
import py.edu.uc.lp3.me.cs2.repository.JugadorRepository;
import py.edu.uc.lp3.me.cs2.rest.dto.ResultadoSimulacion;
import py.edu.uc.lp3.me.cs2.rest.dto.SimulacionExplosionRequest;

import java.util.List;

/**
 * Servicio de Negocio que simula y persiste en H2 el impacto de armas y explosiones en CS2.
 * Participan activamente tres clases del modelo:
 * 1. Granada / ArmaDeFuego (Pistola): Regla 1 de negocio (detonación/consumo de munición y cálculo de daño según distancia y equipo).
 * 2. Jugador: Objetivo persistido en H2 que recibe el impacto.
 * 3. Blindaje: Regla 2 de negocio (absorción de daño por Kevlar y desgaste de durabilidad antes de descontar vida).
 */
@Service
@Transactional
public class SimulacionExplosionService {

    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;

    public SimulacionExplosionService(ArmaRepository armaRepository, JugadorRepository jugadorRepository) {
        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
    }

    /**
     * Ejecuta la simulación sobre entidades persistidas en H2 y guarda el estado resultante.
     * Un GET posterior a /api/armas/{id} o /api/simulacion/explosion/jugadores/{id} mostrará los cambios.
     */
    public ResultadoSimulacion simularImpactoPersistido(SimulacionExplosionRequest request) {
        if (request.getDistanciaMetros() < 0.0) {
            throw new ValidationException("La distancia del impacto no puede ser negativa (recibido: " + request.getDistanciaMetros() + "m).");
        }

        // Si se enviaron objetos en línea sin IDs, delegamos y los persistimos también
        if (request.getArmaId() == null && request.getGranada() != null) {
            Granada granadaGuardada = armaRepository.save(request.getGranada());
            Jugador jugadorObjetivo = (request.getJugador() != null)
                    ? request.getJugador()
                    : obtenerJugadorPorDefecto();
            if (request.getBlindaje() != null) {
                jugadorObjetivo.setBlindaje(request.getBlindaje());
            }
            ResultadoSimulacion res = simularExplosion(granadaGuardada, jugadorObjetivo, jugadorObjetivo.getBlindaje(), request.getDistanciaMetros());
            armaRepository.save(granadaGuardada);
            Jugador jugadorGuardado = jugadorRepository.save(jugadorObjetivo);
            res.setArmaId(granadaGuardada.getId());
            res.setJugadorId(jugadorGuardado.getId());
            res.setPersistidoEnH2(true);
            return res;
        }

        Arma arma = (request.getArmaId() != null)
                ? armaRepository.findById(request.getArmaId())
                        .orElseThrow(() -> new ResourceNotFoundException("No se encontró el arma con ID: " + request.getArmaId()))
                : obtenerArmaPorDefecto();

        Jugador jugador = (request.getJugadorId() != null)
                ? jugadorRepository.findById(request.getJugadorId())
                        .orElseThrow(() -> new ResourceNotFoundException("No se encontró el jugador con ID: " + request.getJugadorId()))
                : obtenerJugadorPorDefecto();

        if (request.getBlindaje() != null) {
            jugador.setBlindaje(request.getBlindaje());
        }

        ResultadoSimulacion resultado;
        if (arma instanceof Granada granada) {
            // Regla de negocio 1 en dominio: activar/detonar granada (verifica que no haya sido detonada antes)
            granada.lanzar(request.getDistanciaMetros());
            resultado = simularExplosion(granada, jugador, jugador.getBlindaje(), request.getDistanciaMetros());
        } else if (arma instanceof ArmaDeFuego armaDeFuego) {
            int disparos = request.getDisparos() > 0 ? request.getDisparos() : 1;
            // Regla de negocio 1 en dominio: consumir munición del cargador y calcular daño efectivo
            double danioBruto = armaDeFuego.disparar(disparos);
            boolean esFuegoAmigo = (armaDeFuego.getEquipo() != Equipo.AMBOS)
                    && (jugador.getEquipo() != null)
                    && (armaDeFuego.getEquipo() == jugador.getEquipo());
            double danioTrasFuegoAmigo = esFuegoAmigo ? danioBruto * 0.35 : danioBruto;

            Blindaje blindajeActivo = jugador.getBlindaje();
            // Regla de negocio 2 en dominio: mitigar daño con Blindaje y descontar vida del Jugador
            double danioFinalAVida = jugador.recibirImpacto(danioTrasFuegoAmigo);
            double danioAbsorbido = danioTrasFuegoAmigo - danioFinalAVida;

            resultado = new ResultadoSimulacion();
            resultado.setEnRadioAlcance(true);
            resultado.setDistanciaMetros(redondear(request.getDistanciaMetros()));
            resultado.setRadioExplosion(0.0);
            resultado.setDanioNominal(redondear(armaDeFuego.getDaño() * disparos));
            resultado.setDanioAtenuadoDistancia(redondear(danioBruto));
            resultado.setFuegoAmigo(esFuegoAmigo);
            resultado.setDanioDespuesFuegoAmigo(redondear(danioTrasFuegoAmigo));
            resultado.setDanioAbsorbidoBlindaje(redondear(danioAbsorbido));
            resultado.setDurabilidadBlindajeRestante(blindajeActivo != null ? redondear(blindajeActivo.getDurabilidad()) : 0.0);
            resultado.setDanioFinalVida(redondear(danioFinalAVida));
            resultado.setVidaRestante(redondear(jugador.getVida()));
            resultado.setEstadoJugador(jugador.isVivo() ? "HERIDO / SOBREVIVIO" : "ELIMINADO / MUERTO");
            resultado.setDetalleRegla(String.format(
                    "Disparo de %s (%d bala(s), munición restante en cargador: %d) contra %s. Blindaje absorbió %.1f pts (durabilidad restante: %.1f%%). Vida restante: %.1f HP.",
                    armaDeFuego.getNombre(), disparos, armaDeFuego.getMunicionCargador(), jugador.getNombre(),
                    danioAbsorbido, resultado.getDurabilidadBlindajeRestante(), jugador.getVida()));
        } else {
            throw new ValidationException("Tipo de arma no soportado en la simulación.");
        }

        // Persistir los cambios en H2
        armaRepository.save(arma);
        jugadorRepository.save(jugador);

        resultado.setArmaId(arma.getId());
        resultado.setArmaUtilizada(arma.getNombre());
        resultado.setJugadorId(jugador.getId());
        resultado.setJugadorObjetivo(jugador.getNombre());
        resultado.setPersistidoEnH2(true);
        return resultado;
    }

    /**
     * Ejecuta la simulación completa de detonación e impacto invocando las reglas de negocio del dominio.
     */
    public ResultadoSimulacion simularExplosion(Granada granada, Jugador jugador, Blindaje blindaje, double distanciaMetros) {
        if (granada == null) {
            throw new ValidationException("La granada no puede ser nula para la simulación.");
        }
        if (jugador == null) {
            throw new ValidationException("El jugador objetivo no puede ser nulo para la simulación.");
        }
        if (distanciaMetros < 0.0) {
            throw new ValidationException("La distancia del impacto no puede ser negativa (recibido: " + distanciaMetros + "m).");
        }

        if (blindaje != null) {
            jugador.setBlindaje(blindaje);
        }
        Blindaje blindajeActivo = jugador.getBlindaje();

        ResultadoSimulacion resultado = new ResultadoSimulacion();
        resultado.setArmaId(granada.getId());
        resultado.setArmaUtilizada(granada.getNombre());
        resultado.setJugadorId(jugador.getId());
        resultado.setJugadorObjetivo(jugador.getNombre());
        resultado.setDistanciaMetros(redondear(distanciaMetros));
        resultado.setRadioExplosion(granada.getRadioExplosion());
        resultado.setDanioNominal(granada.getDaño());

        // Verificación del radio de explosión
        if (distanciaMetros > granada.getRadioExplosion()) {
            resultado.setEnRadioAlcance(false);
            resultado.setDanioAtenuadoDistancia(0.0);
            resultado.setFuegoAmigo(false);
            resultado.setDanioDespuesFuegoAmigo(0.0);
            resultado.setDanioAbsorbidoBlindaje(0.0);
            resultado.setDurabilidadBlindajeRestante(blindajeActivo != null ? redondear(blindajeActivo.getDurabilidad()) : 0.0);
            resultado.setDanioFinalVida(0.0);
            resultado.setVidaRestante(redondear(jugador.getVida()));
            resultado.setEstadoJugador(jugador.isVivo() ? "SOBREVIVIO (FUERA DE ALCANCE)" : "ELIMINADO PREVIAMENTE");
            resultado.setDetalleRegla(String.format(
                    "El jugador '%s' se encuentra a %.2fm, fuera del radio de explosión de la %s (%.2fm). No sufrió daños.",
                    jugador.getNombre(), distanciaMetros, granada.getNombre(), granada.getRadioExplosion()));
            return resultado;
        }

        resultado.setEnRadioAlcance(true);

        // Regla 1 (en el dominio Granada): Atenuación del daño por distancia y fuego amigo
        double factorProximidad = (granada.getRadioExplosion() - distanciaMetros) / granada.getRadioExplosion();
        double danioAtenuado = granada.getDaño() * factorProximidad;
        resultado.setDanioAtenuadoDistancia(redondear(danioAtenuado));

        boolean esFuegoAmigo = (granada.getEquipo() != Equipo.AMBOS)
                && (jugador.getEquipo() != null)
                && (granada.getEquipo() == jugador.getEquipo());
        resultado.setFuegoAmigo(esFuegoAmigo);

        double danioTrasFuegoAmigo = granada.calcularDanioExplosion(distanciaMetros, jugador.getEquipo());
        resultado.setDanioDespuesFuegoAmigo(redondear(danioTrasFuegoAmigo));

        // Regla 2 (en el dominio Jugador y Blindaje): Mitigación del impacto por Kevlar y descuento de vida
        double danioFinalAVida = jugador.recibirImpacto(danioTrasFuegoAmigo);
        double danioAbsorbido = danioTrasFuegoAmigo - danioFinalAVida;

        resultado.setDurabilidadBlindajeRestante(blindajeActivo != null ? redondear(blindajeActivo.getDurabilidad()) : 0.0);
        resultado.setDanioAbsorbidoBlindaje(redondear(danioAbsorbido));
        resultado.setDanioFinalVida(redondear(danioFinalAVida));
        resultado.setVidaRestante(redondear(jugador.getVida()));
        resultado.setEstadoJugador(jugador.isVivo() ? "HERIDO / SOBREVIVIO" : "ELIMINADO / MUERTO");

        StringBuilder detalle = new StringBuilder();
        detalle.append(String.format("Detonación de %s (Radio: %.1fm, Daño Base: %.1f) a %.2fm de %s (%s). ",
                granada.getNombre(), granada.getRadioExplosion(), granada.getDaño(), distanciaMetros,
                jugador.getNombre(), jugador.getEquipo()));

        if (esFuegoAmigo) {
            detalle.append("¡FUEGO AMIGO DETECTADO! Daño reducido al 35%. ");
        }

        if (blindajeActivo != null && danioAbsorbido > 0.0) {
            detalle.append(String.format("%s absorbió %.1f puntos de daño (durabilidad restante: %.1f%%). ",
                    blindajeActivo.getTipo(), danioAbsorbido, blindajeActivo.getDurabilidad()));
        } else {
            detalle.append("Sin protección de blindaje efectiva; absorción 0%. ");
        }

        detalle.append(String.format("Daño final a la salud: %.1f. Vida restante: %.1f HP (%s).",
                danioFinalAVida, jugador.getVida(), resultado.getEstadoJugador()));

        resultado.setDetalleRegla(detalle.toString());
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<Jugador> listarJugadores() {
        return jugadorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Jugador obtenerJugadorPorId(Long id) {
        return jugadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el jugador con ID: " + id));
    }

    private Arma obtenerArmaPorDefecto() {
        return armaRepository.findAll().stream()
                .filter(a -> a instanceof Granada g && !g.isLanzada())
                .findFirst()
                .orElseGet(() -> armaRepository.findAll().stream()
                        .findFirst()
                        .orElseGet(() -> armaRepository.save(
                                new Granada("Granada HE", 300, Equipo.TERRORISTA, 98.0, 8.0, 2.0, true, "Explosión fragmentaria"))));
    }

    private Jugador obtenerJugadorPorDefecto() {
        return jugadorRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> jugadorRepository.save(
                        new Jugador("s1mple (CT)", 100.0, Equipo.COUNTER_TERRORISTA,
                                new Blindaje("Kevlar y Casco Militar", 100.0, 50.0))));
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
