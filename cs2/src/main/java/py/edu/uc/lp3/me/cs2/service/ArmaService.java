package py.edu.uc.lp3.me.cs2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.me.cs2.domain.*;
import py.edu.uc.lp3.me.cs2.exceptions.MunicionInsuficienteException;
import py.edu.uc.lp3.me.cs2.exceptions.ParametroInvalidoException;
import py.edu.uc.lp3.me.cs2.exceptions.ResourceNotFoundException;
import py.edu.uc.lp3.me.cs2.exceptions.ValidationException;
import py.edu.uc.lp3.me.cs2.repository.ArmaRepository;

import java.util.List;

/**
 * Capa de Negocio y Validación para Armas.
 * Aplica validaciones estrictas antes de persistir o modificar datos en H2.
 */
@Service
@Transactional
public class ArmaService {

    private final ArmaRepository armaRepository;

    public ArmaService(ArmaRepository armaRepository) {
        this.armaRepository = armaRepository;
    }

    @Transactional(readOnly = true)
    public List<Arma> listarTodas() {
        return armaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Arma obtenerPorId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("El ID debe ser un número entero positivo.");
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
            throw new ValidationException("El ID a actualizar debe ser un número entero positivo.");
        }

        Arma armaExistente = obtenerPorId(id);

        validarArma(armaActualizada, false);

        // Validación de unicidad de nombre si fue modificado
        if (!armaExistente.getNombre().equalsIgnoreCase(armaActualizada.getNombre().trim())
                && armaRepository.existsByNombreIgnoreCase(armaActualizada.getNombre().trim())) {
            throw new ValidationException("Ya existe un arma registrada con el nombre: " + armaActualizada.getNombre());
        }

        armaExistente.setNombre(armaActualizada.getNombre().trim());
        armaExistente.setDescripcion(armaActualizada.getDescripcion());
        armaExistente.setPrecio(armaActualizada.getPrecio());
        if (armaActualizada.getEquipo() != null) {
            armaExistente.setEquipo(armaActualizada.getEquipo());
        }
        if (armaActualizada.getImagen() != null && !armaActualizada.getImagen().isBlank()) {
            armaExistente.setImagen(armaActualizada.getImagen());
        }
        if (armaActualizada.getSonido() != null && !armaActualizada.getSonido().isBlank()) {
            armaExistente.setSonido(armaActualizada.getSonido());
        }
        if (armaActualizada.getActivo() != null) {
            armaExistente.setActivo(armaActualizada.getActivo());
        }

        // Actualizar atributos específicos de subclases si coinciden
        if (armaExistente instanceof ArmaDeFuego existenteFuego && armaActualizada instanceof ArmaDeFuego nuevaFuego) {
            existenteFuego.setDaño(nuevaFuego.getDaño());
            existenteFuego.setPrecision(nuevaFuego.getPrecision());
            existenteFuego.setMunicionCargador(nuevaFuego.getMunicionCargador());
            existenteFuego.setMunicionReserva(nuevaFuego.getMunicionReserva());
            existenteFuego.setTiempoRecarga(nuevaFuego.getTiempoRecarga());
            if (existenteFuego instanceof Pistola pExistente && nuevaFuego instanceof Pistola pNueva) {
                pExistente.setEsSilenciada(pNueva.isEsSilenciada());
            }
        } else if (armaExistente instanceof Granada gExistente && armaActualizada instanceof Granada gNueva) {
            gExistente.setDaño(gNueva.getDaño());
            gExistente.setRadioExplosion(gNueva.getRadioExplosion());
            gExistente.setTiempoActivacion(gNueva.getTiempoActivacion());
            gExistente.setEsLetal(gNueva.isEsLetal());
            gExistente.setEfecto(gNueva.getEfecto());
            gExistente.setLanzada(gNueva.isLanzada());
        }

        return armaRepository.save(armaExistente);
    }

    public void eliminar(Long id) {
        Arma armaExistente = obtenerPorId(id);
        armaRepository.delete(armaExistente);
    }

    /**
     * Dispara o activa un arma persistida en H2 aplicando las reglas de negocio del dominio.
     */
    public Arma dispararArma(Long id, int cantidad) {
        Arma arma = obtenerPorId(id);
        if (arma instanceof ArmaDeFuego armaDeFuego) {
            armaDeFuego.disparar(cantidad);
        } else if (arma instanceof Granada granada) {
            if (cantidad <= 0) {
                throw new ParametroInvalidoException("La cantidad a activar debe ser positiva.");
            }
            granada.lanzar(2.0);
        }
        return armaRepository.save(arma);
    }

    /**
     * Recarga un arma de fuego persistida en H2.
     */
    public Arma recargarArma(Long id) {
        Arma arma = obtenerPorId(id);
        if (arma instanceof ArmaDeFuego armaDeFuego) {
            armaDeFuego.recargar();
            return armaRepository.save(arma);
        }
        throw new MunicionInsuficienteException("El arma '" + arma.getNombre() + "' no admite recarga de cargador.");
    }

    /**
     * Capa de validación de reglas de negocio para Armas.
     */
    private void validarArma(Arma arma, boolean esCreacion) {
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

        if (arma.getEquipo() == null) {
            arma.setEquipo(Equipo.AMBOS);
        }

        if (arma.getActivo() == null) {
            arma.setActivo(true);
        }
    }
}
