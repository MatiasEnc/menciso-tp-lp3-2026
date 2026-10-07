package py.edu.uc.lp3.me.cs2.domain;

import py.edu.uc.lp3.me.cs2.exceptions.ArmaException;

/**
 * Clase abstracta que representa todas las armas de fuego en CS2.
 */
public abstract class ArmaDeFuego extends Arma {
    protected double daño;
    protected double precision;
    protected int municionCargador;
    protected int municionReserva;
    protected double tiempoRecarga;
    protected int capacidadCargador;

    /**
     * Constructor completo.
     */
    public ArmaDeFuego(String nombre, int precio, Equipo equipo, double daño, double precision,
                       int municionCargador, int municionReserva, double tiempoRecarga) {
        super(nombre, precio, equipo);
        if (daño < 0) {
            throw new ArmaException("El daño no puede ser negativo");
        }
        if (precision < 0.0 || precision > 100.0) {
            throw new ArmaException("La precisión debe estar entre 0.0 y 100.0");
        }
        if (municionCargador < 0 || municionReserva < 0) {
            throw new ArmaException("La munición no puede ser negativa");
        }
        this.daño = daño;
        this.precision = precision;
        this.municionCargador = municionCargador;
        this.municionReserva = municionReserva;
        this.tiempoRecarga = tiempoRecarga;
        this.capacidadCargador = municionCargador;
    }

    /**
     * Constructor simple sobrecargado con valores por defecto.
     */
    public ArmaDeFuego(String nombre, int precio, double daño, int municionCargador) {
        this(nombre, precio, Equipo.AMBOS, daño, 75.0, municionCargador, municionCargador * 3, 2.2);
    }

    public void disparar() {
        disparar(1);
    }

    /**
     * Sobrecarga del método disparar para múltiples disparos en ráfaga.
     */
    public void disparar(int balas) {
        if (balas <= 0) {
            throw new ArmaException("La cantidad de disparos debe ser mayor a cero");
        }
        int consumidas = Math.min(balas, municionCargador);
        municionCargador -= consumidas;
    }

    public void recargar() {
        if (municionCargador >= capacidadCargador || municionReserva <= 0) return;
        int necesarias = capacidadCargador - municionCargador;
        int recarga = Math.min(necesarias, municionReserva);
        municionCargador += recarga;
        municionReserva -= recarga;
    }

    @Override
    public String ejecutarAccion() {
        return ejecutarAccion(1);
    }

    @Override
    public String ejecutarAccion(int repeticiones) {
        if (repeticiones <= 0) {
            throw new ArmaException("Las repeticiones deben ser mayores a cero");
        }
        if (municionCargador >= repeticiones) {
            disparar(repeticiones);
            return String.format("[%s] Disparo efectuado (%d bala(s)). Daño base: %.1f | Balas restantes: %d/%d",
                    nombre, repeticiones, daño, municionCargador, capacidadCargador);
        } else if (municionCargador > 0) {
            int gastadas = municionCargador;
            disparar(gastadas);
            return String.format("[%s] Ráfaga parcial de %d bala(s) (cargador agotado). Balas restantes: 0/%d",
                    nombre, gastadas, capacidadCargador);
        } else {
            return String.format("[%s] *Click* Sin munición en el cargador. ¡Necesita recargar!", nombre);
        }
    }

    public double getDaño() { return daño; }
    public double getPrecision() { return precision; }
    public int getMunicionCargador() { return municionCargador; }
    public int getCapacidadCargador() { return capacidadCargador; }
}
