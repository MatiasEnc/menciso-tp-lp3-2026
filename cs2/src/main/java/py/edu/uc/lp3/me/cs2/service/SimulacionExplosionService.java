package py.edu.uc.lp3.me.cs2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.me.cs2.domain.*;
import py.edu.uc.lp3.me.cs2.exceptions.ParametroInvalidoException;
import py.edu.uc.lp3.me.cs2.exceptions.ResourceNotFoundException;
import py.edu.uc.lp3.me.cs2.exceptions.ValidationException;
import py.edu.uc.lp3.me.cs2.repository.ArmaRepository;
import py.edu.uc.lp3.me.cs2.repository.JugadorRepository;
import py.edu.uc.lp3.me.cs2.rest.dto.ResultadoSimulacion;
import py.edu.uc.lp3.me.cs2.rest.dto.SimulacionExplosionRequest;

import java.util.List;

/**
 * Servicio de Negocio que simula y persiste en H2 el impacto de armas y explosiones en CS2.
 * Concentra las validaciones de los objetos participantes y las dos reglas de negocio:
 * 1. Regla 1 (Consumo de carga/munición y cálculo de daño según distancia y fuego amigo).
 * 2. Regla 2 (Mitigación de daño por blindaje Kevlar, desgaste de durabilidad y descuento de vida del jugador).
 */
@Service
@Transactional
public class SimulacionExplosionService {

    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;
    private final ArmaService armaService;

    public SimulacionExplosionService(ArmaRepository armaRepository,
                                      JugadorRepository jugadorRepository,
                                      ArmaService armaService) {
        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
        this.armaService = armaService;
    }

    /**
     * Ejecuta la simulación sobre entidades persistidas en H2, validando todos los invariantes
     * y guardando el nuevo estado resultante en la base de datos.
     */
    public ResultadoSimulacion simularImpactoPersistido(SimulacionExplosionRequest requestParam) {
        final SimulacionExplosionRequest request = (requestParam != null) ? requestParam : new SimulacionExplosionRequest();
        if (request.getDistanciaMetros() < 0.0) {
            throw new ValidationException("La distancia del impacto no puede ser negativa (recibido: " + request.getDistanciaMetros() + "m).");
        }

        // Caso en que se envían objetos en el JSON sin armaId: validamos todos los invariantes antes de persistir
        if (request.getArmaId() == null && request.getGranada() != null) {
            Granada granadaRecibida = request.getGranada();
            armaService.validarArma(granadaRecibida, granadaRecibida.getId() == null);

            Jugador jugadorObjetivo = (request.getJugador() != null)
                    ? request.getJugador()
                    : obtenerJugadorPorDefecto();
            if (request.getBlindaje() != null) {
                validarBlindaje(request.getBlindaje());
                jugadorObjetivo.setBlindaje(request.getBlindaje());
            }
            validarJugador(jugadorObjetivo);

            armaService.aplicarDetonacionGranada(granadaRecibida, request.getDistanciaMetros());
            Granada granadaGuardada = armaRepository.save(granadaRecibida);
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
        armaService.validarArma(arma, false);

        Jugador jugador = (request.getJugadorId() != null)
                ? jugadorRepository.findById(request.getJugadorId())
                        .orElseThrow(() -> new ResourceNotFoundException("No se encontró el jugador con ID: " + request.getJugadorId()))
                : (request.getJugador() != null ? request.getJugador() : obtenerJugadorPorDefecto());

        if (request.getBlindaje() != null) {
            validarBlindaje(request.getBlindaje());
            jugador.setBlindaje(request.getBlindaje());
        }
        validarJugador(jugador);

        ResultadoSimulacion resultado;
        if (arma instanceof Granada granada) {
            // Regla de negocio 1: activar/detonar granada (verifica que no haya sido detonada antes)
            armaService.aplicarDetonacionGranada(granada, request.getDistanciaMetros());
            resultado = simularExplosion(granada, jugador, jugador.getBlindaje(), request.getDistanciaMetros());
        } else if (arma instanceof ArmaDeFuego armaDeFuego) {
            int disparos = request.getDisparos() > 0 ? request.getDisparos() : 1;
            // Regla de negocio 1: consumir munición del cargador y calcular daño efectivo
            double danioBruto = armaService.aplicarDisparoArmaDeFuego(armaDeFuego, disparos);
            boolean esFuegoAmigo = (armaDeFuego.getEquipo() != Equipo.AMBOS)
                    && (jugador.getEquipo() != null)
                    && (armaDeFuego.getEquipo() == jugador.getEquipo());
            double danioTrasFuegoAmigo = esFuegoAmigo ? danioBruto * 0.35 : danioBruto;

            Blindaje blindajeActivo = jugador.getBlindaje();
            // Regla de negocio 2: mitigar daño con Blindaje y descontar vida del Jugador
            double danioFinalAVida = aplicarImpactoAJugador(jugador, danioTrasFuegoAmigo);
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
                    "Disparo de %s (%d bala(s), munición restante en cargador: %d/%d) contra %s. Blindaje absorbió %.1f pts (durabilidad restante: %.1f%%). Vida restante: %.1f HP.",
                    armaDeFuego.getNombre(), disparos, armaDeFuego.getMunicionCargador(), armaDeFuego.getCapacidadCargador(),
                    jugador.getNombre(), danioAbsorbido, resultado.getDurabilidadBlindajeRestante(), jugador.getVida()));
        } else {
            throw new ValidationException("Tipo de arma no soportado en la simulación.");
        }

        armaRepository.save(arma);
        Jugador jugadorGuardado = jugadorRepository.save(jugador);

        resultado.setArmaId(arma.getId());
        resultado.setArmaUtilizada(arma.getNombre());
        resultado.setJugadorId(jugadorGuardado.getId());
        resultado.setJugadorObjetivo(jugadorGuardado.getNombre());
        resultado.setPersistidoEnH2(true);
        return resultado;
    }

    /**
     * Ejecuta la simulación completa de detonación e impacto validando los tres objetos del dominio.
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

        armaService.validarArma(granada, false);
        if (blindaje != null) {
            validarBlindaje(blindaje);
            jugador.setBlindaje(blindaje);
        }
        validarJugador(jugador);

        Blindaje blindajeActivo = jugador.getBlindaje();

        ResultadoSimulacion resultado = new ResultadoSimulacion();
        resultado.setArmaId(granada.getId());
        resultado.setArmaUtilizada(granada.getNombre());
        resultado.setJugadorId(jugador.getId());
        resultado.setJugadorObjetivo(jugador.getNombre());
        resultado.setDistanciaMetros(redondear(distanciaMetros));
        resultado.setRadioExplosion(granada.getRadioExplosion());
        resultado.setDanioNominal(granada.getDaño());

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

        // Regla 1 en Service: Atenuación del daño por distancia y penalización del 35% por fuego amigo
        double factorProximidad = (granada.getRadioExplosion() - distanciaMetros) / granada.getRadioExplosion();
        double danioAtenuado = granada.getDaño() * factorProximidad;
        resultado.setDanioAtenuadoDistancia(redondear(danioAtenuado));

        boolean esFuegoAmigo = (granada.getEquipo() != Equipo.AMBOS)
                && (jugador.getEquipo() != null)
                && (granada.getEquipo() == jugador.getEquipo());
        resultado.setFuegoAmigo(esFuegoAmigo);

        double danioTrasFuegoAmigo = calcularDanioExplosion(granada, distanciaMetros, jugador.getEquipo());
        resultado.setDanioDespuesFuegoAmigo(redondear(danioTrasFuegoAmigo));

        // Regla 2 en Service: Mitigación del impacto por Kevlar y descuento de vida
        double danioFinalAVida = aplicarImpactoAJugador(jugador, danioTrasFuegoAmigo);
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

    /**
     * Prepara los objetos del dominio en la capa Service para la simulación por parámetros GET (sin modificar H2).
     */
    @Transactional(readOnly = true)
    public ResultadoSimulacion simularConParametros(double distancia, boolean conBlindaje, boolean fuegoAmigo) {
        Equipo equipoJugador = fuegoAmigo ? Equipo.TERRORISTA : Equipo.COUNTER_TERRORISTA;
        Blindaje blindaje = conBlindaje ? new Blindaje("Chaleco Kevlar", 100.0, 50.0) : null;
        Granada granada = new Granada("Granada HE", 300, Equipo.TERRORISTA, 98.0, 8.0, 2.0, true, "Explosión fragmentaria");
        Jugador jugador = new Jugador("Player-Test", 100.0, equipoJugador, blindaje);
        return simularExplosion(granada, jugador, blindaje, distancia);
    }

    /**
     * Ejecuta una demostración de lectura para GET /api/simulacion/explosion/demo sin mutar datos en H2.
     */
    @Transactional(readOnly = true)
    public ResultadoSimulacion simularDemoLectura() {
        Granada he = new Granada("Granada HE", 300, Equipo.TERRORISTA, 98.0, 8.0, 2.0, true, "Explosión fragmentaria");
        Blindaje kevlar = new Blindaje("Kevlar y Casco Militar", 100.0, 50.0);
        Jugador ct = new Jugador("s1mple (CT)", 100.0, Equipo.COUNTER_TERRORISTA, kevlar);
        return simularExplosion(he, ct, kevlar, 2.0);
    }

    /**
     * Regla de negocio 1: Calcula el daño explosivo según la distancia al epicentro y el fuego amigo.
     */
    public double calcularDanioExplosion(Granada granada, double distanciaMetros, Equipo equipoObjetivo) {
        if (distanciaMetros < 0) {
            throw new ParametroInvalidoException("La distancia al epicentro no puede ser negativa.");
        }
        if (distanciaMetros > granada.getRadioExplosion() || granada.getRadioExplosion() <= 0) {
            return 0.0;
        }
        double factorProximidad = (granada.getRadioExplosion() - distanciaMetros) / granada.getRadioExplosion();
        double danioAtenuado = granada.getDaño() * factorProximidad;

        boolean esFuegoAmigo = (granada.getEquipo() != Equipo.AMBOS)
                && (equipoObjetivo != null)
                && (granada.getEquipo() == equipoObjetivo);
        if (esFuegoAmigo) {
            danioAtenuado *= 0.35;
        }
        return danioAtenuado;
    }

    /**
     * Regla de negocio 2: Mitiga el daño con el blindaje Kevlar desgastando su durabilidad
     * y descuenta el daño residual de la vida del jugador.
     */
    public double aplicarImpactoAJugador(Jugador jugador, double danioEntrante) {
        if (danioEntrante < 0) {
            throw new ParametroInvalidoException("El daño entrante no puede ser negativo.");
        }
        double danioFinal = danioEntrante;
        Blindaje blindaje = jugador.getBlindaje();
        if (blindaje != null && blindaje.getDurabilidad() > 0.0) {
            double danioObjetivoAbsorcion = danioEntrante * (blindaje.getPorcentajeAbsorcion() / 100.0);
            if (blindaje.getDurabilidad() >= danioObjetivoAbsorcion) {
                blindaje.setDurabilidad(blindaje.getDurabilidad() - danioObjetivoAbsorcion);
                danioFinal = Math.max(0.0, danioEntrante - danioObjetivoAbsorcion);
            } else {
                danioFinal = Math.max(0.0, danioEntrante - blindaje.getDurabilidad());
                blindaje.setDurabilidad(0.0);
            }
        }
        double nuevaVida = Math.max(0.0, jugador.getVida() - danioFinal);
        jugador.setVida(nuevaVida);
        if (nuevaVida <= 0.0) {
            jugador.setVivo(false);
            jugador.setSonido("player_death.wav");
        }
        return danioFinal;
    }

    public void validarJugador(Jugador jugador) {
        if (jugador == null) {
            throw new ValidationException("El jugador no puede ser nulo.");
        }
        if (jugador.getNombre() == null || jugador.getNombre().isBlank()) {
            throw new ValidationException("El nombre del jugador no puede estar vacío.");
        }
        if (jugador.getVida() < 0.0 || jugador.getVida() > 100.0) {
            throw new ValidationException("La vida del jugador debe estar entre 0.0 y 100.0 (recibido: " + jugador.getVida() + ").");
        }
        if (jugador.getBlindaje() != null) {
            validarBlindaje(jugador.getBlindaje());
        }
    }

    public void validarBlindaje(Blindaje blindaje) {
        if (blindaje == null) {
            return;
        }
        if (blindaje.getDurabilidad() < 0.0 || blindaje.getDurabilidad() > 100.0) {
            throw new ValidationException("La durabilidad del blindaje debe estar entre 0.0 y 100.0 (recibido: " + blindaje.getDurabilidad() + ").");
        }
        if (blindaje.getPorcentajeAbsorcion() < 0.0 || blindaje.getPorcentajeAbsorcion() > 100.0) {
            throw new ValidationException("El porcentaje de absorción del blindaje debe estar entre 0.0 y 100.0 (recibido: " + blindaje.getPorcentajeAbsorcion() + ").");
        }
    }

    @Transactional(readOnly = true)
    public List<Jugador> listarJugadores() {
        return jugadorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Jugador obtenerJugadorPorId(Long id) {
        if (id == null || id <= 0) {
            throw new ParametroInvalidoException("El ID del jugador debe ser un número entero positivo.");
        }
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
