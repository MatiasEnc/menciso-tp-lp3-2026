package py.edu.uc.lp3.me.cs2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.me.cs2.domain.*;
import py.edu.uc.lp3.me.cs2.exceptions.MunicionInsuficienteException;
import py.edu.uc.lp3.me.cs2.exceptions.ParametroInvalidoException;
import py.edu.uc.lp3.me.cs2.exceptions.ResourceNotFoundException;
import py.edu.uc.lp3.me.cs2.exceptions.ValidationException;
import py.edu.uc.lp3.me.cs2.repository.ArmaRepository;
import py.edu.uc.lp3.me.cs2.repository.JugadorRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Capa de Negocio y Validación para Armas.
 * Concentra las validaciones de invariantes de CS2, las reglas de disparo/recarga
 * y el lanzamiento de excepciones antes de persistir o modificar datos en H2.
 */
@Service
@Transactional
public class ArmaService {

    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;

    public ArmaService(ArmaRepository armaRepository, JugadorRepository jugadorRepository) {
        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
    }

    @Transactional(readOnly = true)
    public List<Arma> listarTodas() {
        return armaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Arma obtenerPorId(Long id) {
        if (id == null || id <= 0) {
            throw new ParametroInvalidoException("El ID debe ser un número entero positivo.");
        }
        return armaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el arma con ID: " + id));
    }

    public Arma crear(Arma arma) {
        validarArma(arma, true);
        return armaRepository.save(arma);
    }

    public Arma actualizar(Long id, Arma armaActualizada) {
        if (id == null || id <= 0) {
            throw new ParametroInvalidoException("El ID a actualizar debe ser un número entero positivo.");
        }

        Arma armaExistente = obtenerPorId(id);

        validarArma(armaActualizada, false);

        if (!armaExistente.getNombre().equalsIgnoreCase(armaActualizada.getNombre().trim())
                && armaRepository.existsByNombreIgnoreCase(armaActualizada.getNombre().trim())) {
            throw new ValidationException("Ya existe un arma registrada con el nombre: " + armaActualizada.getNombre());
        }

        armaExistente.setNombre(armaActualizada.getNombre().trim());
        armaExistente.setDescripcion(armaActualizada.getDescripcion());
        armaExistente.setPrecio(armaActualizada.getPrecio());
        armaExistente.setEquipo(armaActualizada.getEquipo() != null ? armaActualizada.getEquipo() : Equipo.AMBOS);
        armaExistente.setImagen(armaActualizada.getImagen());
        armaExistente.setSonido(armaActualizada.getSonido());
        armaExistente.setActivo(armaActualizada.getActivo() != null ? armaActualizada.getActivo() : true);

        if (armaExistente instanceof ArmaDeFuego existenteFuego && armaActualizada instanceof ArmaDeFuego nuevaFuego) {
            existenteFuego.setCapacidadCargador(nuevaFuego.getCapacidadCargador());
            existenteFuego.setMunicionCargador(nuevaFuego.getMunicionCargador());
            existenteFuego.setMunicionReserva(nuevaFuego.getMunicionReserva());
            existenteFuego.setDaño(nuevaFuego.getDaño());
            existenteFuego.setPrecision(nuevaFuego.getPrecision());
            existenteFuego.setTiempoRecarga(nuevaFuego.getTiempoRecarga());
            if (existenteFuego instanceof Pistola pExistente && nuevaFuego instanceof Pistola pNueva) {
                pExistente.setEsSilenciada(pNueva.isEsSilenciada());
            } else if (existenteFuego instanceof Francotirador fExistente && nuevaFuego instanceof Francotirador fNueva) {
                fExistente.setMiraDesplegada(fNueva.isMiraDesplegada());
            } else if (existenteFuego instanceof Escopeta eExistente && nuevaFuego instanceof Escopeta eNueva) {
                eExistente.setPerdigonesPorDisparo(eNueva.getPerdigonesPorDisparo());
            }
        } else if (armaExistente instanceof Granada gExistente && armaActualizada instanceof Granada gNueva) {
            gExistente.setDaño(gNueva.getDaño());
            gExistente.setRadioExplosion(gNueva.getRadioExplosion());
            gExistente.setTiempoActivacion(gNueva.getTiempoActivacion());
            gExistente.setEsLetal(gNueva.isEsLetal());
            gExistente.setEfecto(gNueva.getEfecto());
            gExistente.setLanzada(gNueva.isLanzada());
        }

        validarArma(armaExistente, false);
        return armaRepository.save(armaExistente);
    }

    public void eliminar(Long id) {
        Arma armaExistente = obtenerPorId(id);
        armaRepository.delete(armaExistente);
    }

    /**
     * Regla de negocio 1 en Service: Dispara un arma de fuego o detona una granada persistida en H2.
     */
    public Arma dispararArma(Long id, int cantidad) {
        if (cantidad <= 0) {
            throw new ParametroInvalidoException("La cantidad de disparos o activaciones debe ser mayor a cero.");
        }
        Arma arma = obtenerPorId(id);
        if (arma instanceof ArmaDeFuego armaDeFuego) {
            aplicarDisparoArmaDeFuego(armaDeFuego, cantidad);
        } else if (arma instanceof Granada granada) {
            aplicarDetonacionGranada(granada, 2.0);
        }
        return armaRepository.save(arma);
    }

    /**
     * Regla de negocio de consumo de munición en armas de fuego.
     * Valida que la cantidad sea positiva y que haya balas en el cargador.
     */
    public double aplicarDisparoArmaDeFuego(ArmaDeFuego armaDeFuego, int balas) {
        if (balas <= 0) {
            throw new ParametroInvalidoException("La cantidad de disparos debe ser mayor a cero.");
        }
        if (armaDeFuego.getMunicionCargador() <= 0) {
            throw new MunicionInsuficienteException(
                    "El arma '" + armaDeFuego.getNombre() + "' no tiene munición en el cargador (0/"
                            + armaDeFuego.getCapacidadCargador() + "). ¡Debe recargar!");
        }
        int consumidas = Math.min(balas, armaDeFuego.getMunicionCargador());
        armaDeFuego.setMunicionCargador(armaDeFuego.getMunicionCargador() - consumidas);
        return consumidas * armaDeFuego.getDaño();
    }

    /**
     * Regla de negocio de detonación única de granada.
     * Una granada solo puede arrojarse y detonar una vez.
     */
    public void aplicarDetonacionGranada(Granada granada, double distanciaMetros) {
        if (distanciaMetros < 0) {
            throw new ParametroInvalidoException("La distancia de lanzamiento no puede ser negativa.");
        }
        if (granada.isLanzada()) {
            throw new MunicionInsuficienteException(
                    "La granada '" + granada.getNombre() + "' ya fue arrojada y detonada previamente (sin carga disponible).");
        }
        granada.setLanzada(true);
        granada.setActivo(false);
    }

    /**
     * Regla de negocio de recarga de munición en un arma de fuego persistida en H2.
     */
    public Arma recargarArma(Long id) {
        Arma arma = obtenerPorId(id);
        if (arma instanceof ArmaDeFuego armaDeFuego) {
            if (armaDeFuego.getMunicionReserva() <= 0) {
                throw new MunicionInsuficienteException(
                        "El arma '" + armaDeFuego.getNombre() + "' no tiene munición de reserva disponible para recargar (reserva: 0).");
            }
            if (armaDeFuego.getMunicionCargador() < armaDeFuego.getCapacidadCargador()) {
                int necesarias = armaDeFuego.getCapacidadCargador() - armaDeFuego.getMunicionCargador();
                int recarga = Math.min(necesarias, armaDeFuego.getMunicionReserva());
                armaDeFuego.setMunicionCargador(armaDeFuego.getMunicionCargador() + recarga);
                armaDeFuego.setMunicionReserva(armaDeFuego.getMunicionReserva() - recarga);
            }
            return armaRepository.save(armaDeFuego);
        }
        throw new MunicionInsuficienteException("El arma '" + arma.getNombre() + "' no admite recarga de cargador.");
    }

    /**
     * Instancia y valida una Pistola con su constructor completo (para /armas/crear).
     */
    @Transactional(readOnly = true)
    public Pistola instanciarPistolaCompleta(String nombre, int precio, String equipoStr, double dano, int balas, String imagen, String sonido) {
        if (balas <= 0) {
            throw new ParametroInvalidoException("La cantidad de balas debe ser mayor a cero.");
        }
        Equipo eq;
        try {
            eq = Equipo.fromString(equipoStr);
        } catch (IllegalArgumentException ex) {
            throw new ParametroInvalidoException(ex.getMessage());
        }
        Pistola nuevaPistola = new Pistola(
                nombre, precio, eq, dano, 75.0, balas, balas * 3, 2.2, false,
                (imagen != null && !imagen.isBlank()) ? imagen : "glock18.png",
                (sonido != null && !sonido.isBlank()) ? sonido : "pistol_shot.wav"
        );
        validarArma(nuevaPistola, false);
        return nuevaPistola;
    }

    /**
     * Instancia y valida una Pistola con su constructor sobrecargado simple (para /armas/crear-simple).
     */
    @Transactional(readOnly = true)
    public Pistola instanciarPistolaSimple(String nombre, int precio, double dano) {
        Pistola pistolaSimple = new Pistola(nombre, precio, dano);
        validarArma(pistolaSimple, false);
        return pistolaSimple;
    }

    /**
     * Obtiene el comportamiento polimórfico (sobreescritura de ejecutarAccion) de las armas en H2.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerComportamientoPolimorfico() {
        List<Arma> arsenal = armaRepository.findAll();
        if (arsenal.isEmpty()) {
            arsenal = List.of(
                    new Pistola("USP-S", 200, Equipo.COUNTER_TERRORISTA, 35.0, 85.0, 12, 24, 2.2, true),
                    new Granada("Granada Flashbang", 200, Equipo.AMBOS, 0.0, 10.0, 1.5, false, "Ceguera temporal")
            );
        }

        List<Map<String, Object>> respuesta = new ArrayList<>();
        for (Arma arma : arsenal) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", arma.getId());
            item.put("arma", arma.getNombre());
            item.put("tipo", arma.getClass().getSimpleName());
            item.put("precio", arma.getPrecio());
            item.put("equipo", arma.getEquipo());
            item.put("imagen", arma.getImagen());
            item.put("sonido", arma.getSonido());
            item.put("resultadoAccion", arma.ejecutarAccion());
            respuesta.add(item);
        }
        return respuesta;
    }

    /**
     * Obtiene el comportamiento sobrecargado (ejecutarAccion(int)) validando el parámetro en el service.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerComportamientoSobrecargado(int repeticiones) {
        if (repeticiones <= 0) {
            throw new ParametroInvalidoException("Las repeticiones deben ser mayores a cero.");
        }
        List<Arma> arsenal = List.of(
                new Pistola("Glock-18", 200, Equipo.TERRORISTA, 28.0, 75.0, 20, 60, 2.2, false),
                new Granada("Granada HE", 300, Equipo.AMBOS, 98.0, 8.0, 2.0, true, "Explosión fragmentaria")
        );

        List<Map<String, Object>> respuesta = new ArrayList<>();
        for (Arma arma : arsenal) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("arma", arma.getNombre());
            item.put("repeticionesOSegundos", repeticiones);
            item.put("imagen", arma.getImagen());
            item.put("sonido", arma.getSonido());
            item.put("resultadoSobrecargado", arma.ejecutarAccion(repeticiones));
            respuesta.add(item);
        }
        return respuesta;
    }

    /**
     * Reúne las entidades persistidas de dos familias distintas (Arma y Jugador) bajo la interfaz UICapable.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerCatalogoUI() {
        List<UICapable> elementosUI = new ArrayList<>();
        elementosUI.addAll(armaRepository.findAll());
        elementosUI.addAll(jugadorRepository.findAll());

        List<Map<String, Object>> res = new ArrayList<>();
        for (UICapable elemento : elementosUI) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("clase", elemento.getClass().getSimpleName());
            m.put("jerarquia", (elemento instanceof Arma) ? "Familia Arma" : "Familia Jugador");
            if (elemento instanceof Arma a) {
                m.put("id", a.getId());
                m.put("nombre", a.getNombre());
            } else if (elemento instanceof Jugador j) {
                m.put("id", j.getId());
                m.put("nombre", j.getNombre());
            }
            m.put("imagen", elemento.getImagen());
            m.put("sonido", elemento.getSonido());
            res.add(m);
        }
        return res;
    }

    /**
     * Valida todos los invariantes de negocio de un Arma y sus subclases en Counter-Strike 2.
     * Se invoca al crear, al actualizar y antes de ejecutar simulaciones.
     */
    public void validarArma(Arma arma, boolean esCreacion) {
        if (arma == null) {
            throw new ValidationException("El cuerpo del arma no puede ser nulo.");
        }

        if (esCreacion && arma.getId() != null) {
            throw new ValidationException("No se debe especificar un ID al crear una nueva arma (el ID es autogenerado).");
        }

        if (arma.getNombre() == null || arma.getNombre().isBlank()) {
            throw new ValidationException("El nombre del arma es obligatorio y no puede estar vacío.");
        }

        if (arma.getNombre().trim().length() < 2) {
            throw new ValidationException("El nombre del arma debe tener como mínimo 2 caracteres.");
        }

        if (esCreacion && armaRepository.existsByNombreIgnoreCase(arma.getNombre().trim())) {
            throw new ValidationException("Ya existe un arma registrada con el nombre: " + arma.getNombre());
        }

        if (arma.getPrecio() < 0) {
            throw new ValidationException("El precio del arma no puede ser negativo (precio recibido: " + arma.getPrecio() + ").");
        }

        if (arma instanceof ArmaDeFuego fuego) {
            if (fuego.getDaño() < 0) {
                throw new ValidationException("El daño del arma de fuego no puede ser negativo (recibido: " + fuego.getDaño() + ").");
            }
            if (fuego.getPrecision() < 0.0 || fuego.getPrecision() > 100.0) {
                throw new ValidationException("La precisión del arma debe estar entre 0.0 y 100.0 (recibido: " + fuego.getPrecision() + ").");
            }
            if (fuego.getCapacidadCargador() <= 0) {
                throw new ValidationException("La capacidad del cargador debe ser mayor a cero (recibido: " + fuego.getCapacidadCargador() + ").");
            }
            if (fuego.getMunicionCargador() < 0) {
                throw new ValidationException("La munición en el cargador no puede ser negativa (recibido: " + fuego.getMunicionCargador() + ").");
            }
            if (fuego.getMunicionReserva() < 0) {
                throw new ValidationException("La munición de reserva no puede ser negativa (recibido: " + fuego.getMunicionReserva() + ").");
            }
            if (fuego.getMunicionCargador() > fuego.getCapacidadCargador()) {
                throw new ValidationException(
                        "Invariante de CS2 violado: la munición cargada (" + fuego.getMunicionCargador()
                                + ") no puede superar la capacidad máxima del cargador (" + fuego.getCapacidadCargador() + ").");
            }
            if (fuego.getTiempoRecarga() < 0) {
                throw new ValidationException("El tiempo de recarga no puede ser negativo (recibido: " + fuego.getTiempoRecarga() + ").");
            }
            if (fuego instanceof Escopeta escopeta && escopeta.getPerdigonesPorDisparo() <= 0) {
                throw new ValidationException("La cantidad de perdigones por disparo debe ser mayor a cero.");
            }
        } else if (arma instanceof Granada granada) {
            if (granada.getDaño() < 0) {
                throw new ValidationException("El daño explosivo de la granada no puede ser negativo (recibido: " + granada.getDaño() + ").");
            }
            if (granada.getRadioExplosion() <= 0) {
                throw new ValidationException("El radio de explosión de la granada debe ser mayor a cero (recibido: " + granada.getRadioExplosion() + ").");
            }
            if (granada.getTiempoActivacion() < 0) {
                throw new ValidationException("El tiempo de activación de la granada no puede ser negativo (recibido: " + granada.getTiempoActivacion() + ").");
            }
        }

        if (arma.getEquipo() == null) {
            arma.setEquipo(Equipo.AMBOS);
        }
        if (arma.getImagen() == null || arma.getImagen().isBlank()) {
            arma.setImagen("default_weapon.png");
        }
        if (arma.getSonido() == null || arma.getSonido().isBlank()) {
            arma.setSonido("default_sound.wav");
        }
        if (arma.getActivo() == null) {
            arma.setActivo(true);
        }
    }
}
