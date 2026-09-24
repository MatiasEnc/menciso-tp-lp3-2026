package py.edu.uc.lp3.me.cs2;

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

    public ArmaDeFuego(String nombre, int precio, Equipo equipo, double daño, double precision,
                       int municionCargador, int municionReserva, double tiempoRecarga) {
        super(nombre, precio, equipo);
        if (municionCargador < 0 || municionReserva < 0) {
            throw new IllegalArgumentException("La munición no puede ser negativa");
        }
        this.daño = daño;
        this.precision = precision;
        this.municionCargador = municionCargador;
        this.municionReserva = municionReserva;
        this.tiempoRecarga = tiempoRecarga;
        this.capacidadCargador = municionCargador;
    }

    public void disparar() {
        if (municionCargador > 0) {
            municionCargador--;
            System.out.println("[" + nombre + "] ¡Bang! Balas restantes: " + municionCargador + "/" + capacidadCargador);
        } else {
            System.out.println("[" + nombre + "] *Click* Sin munición en el cargador.");
        }
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
        disparar();
        return String.format("[%s] Disparo efectuado. Daño: %.1f | Balas: %d/%d",
                nombre, daño, municionCargador, capacidadCargador);
    }

    public double getDaño() { return daño; }
    public double getPrecision() { return precision; }
    public int getMunicionCargador() { return municionCargador; }
    public int getCapacidadCargador() { return capacidadCargador; }
}
