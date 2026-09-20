/**
 * Clase abstracta que representa todas las armas de fuego en CS2.
 * Hereda de Arma e incorpora la gestión de balística, cargador y recarga.
 */
public abstract class ArmaDeFuego extends Arma {
    protected double daño;
    protected double precision;
    protected int municionCargador;
    protected int municionReserva;
    protected double tiempoRecarga; // En segundos
    protected int capacidadCargador; // Capacidad máxima del cargador

    /**
     * Constructor para armas de fuego.
     *
     * @param nombre           Nombre del arma.
     * @param precio           Precio de compra en dólares.
     * @param equipo           Bando al que pertenece.
     * @param daño             Daño base infligido por disparo.
     * @param precision        Porcentaje o índice de precisión (0.0 a 100.0).
     * @param municionCargador Cantidad actual de balas en el cargador.
     * @param municionReserva  Cantidad de munición en reserva.
     * @param tiempoRecarga    Tiempo estimado de recarga en segundos.
     */
    public ArmaDeFuego(String nombre, int precio, Equipo equipo, double daño, double precision,
                       int municionCargador, int municionReserva, double tiempoRecarga) {
        super(nombre, precio, equipo);
        this.daño = daño;
        this.precision = precision;
        this.municionCargador = municionCargador;
        this.municionReserva = municionReserva;
        this.tiempoRecarga = tiempoRecarga;
        this.capacidadCargador = municionCargador;
    }

    /**
     * Ejecuta un disparo del arma de fuego consumiendo una bala del cargador.
     */
    public void disparar() {
        if (municionCargador > 0) {
            municionCargador--;
            System.out.println("[" + nombre + "] ¡Bang! Disparo efectuado. Daño base: " + daño 
                    + " | Balas restantes en cargador: " + municionCargador + "/" + capacidadCargador);
        } else {
            System.out.println("[" + nombre + "] *¡Click, click!* Sin munición en el cargador. ¡Necesitas recargar!");
        }
    }

    /**
     * Recarga el cargador del arma transfiriendo balas desde la reserva.
     */
    public void recargar() {
        if (municionCargador >= capacidadCargador) {
            System.out.println("[" + nombre + "] El cargador ya se encuentra lleno (" + municionCargador + "/" + capacidadCargador + ").");
            return;
        }

        if (municionReserva <= 0) {
            System.out.println("[" + nombre + "] No queda munición en la reserva para recargar.");
            return;
        }

        int balasNecesarias = capacidadCargador - municionCargador;
        int balasARecargar = Math.min(balasNecesarias, municionReserva);

        municionCargador += balasARecargar;
        municionReserva -= balasARecargar;

        System.out.println("[" + nombre + "] Recargando... (" + tiempoRecarga + "s)");
        System.out.println("[" + nombre + "] Recarga completada. Cargador: " + municionCargador 
                + "/" + capacidadCargador + " | Reserva restante: " + municionReserva);
    }

    // --- Getters y Setters ---

    public double getDaño() {
        return daño;
    }

    public void setDaño(double daño) {
        this.daño = daño;
    }

    // Alias para evitar inconvenientes con codificación de caracteres en entornos que no soporten 'ñ'
    public double getDanio() {
        return daño;
    }

    public void setDanio(double danio) {
        this.daño = danio;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public int getMunicionCargador() {
        return municionCargador;
    }

    public void setMunicionCargador(int municionCargador) {
        this.municionCargador = municionCargador;
    }

    public int getMunicionReserva() {
        return municionReserva;
    }

    public void setMunicionReserva(int municionReserva) {
        this.municionReserva = municionReserva;
    }

    public double getTiempoRecarga() {
        return tiempoRecarga;
    }

    public void setTiempoRecarga(double tiempoRecarga) {
        this.tiempoRecarga = tiempoRecarga;
    }

    public int getCapacidadCargador() {
        return capacidadCargador;
    }

    public void setCapacidadCargador(int capacidadCargador) {
        this.capacidadCargador = capacidadCargador;
    }

    @Override
    public void mostrarInformacion() {
        super.mostrarInformacion();
        System.out.println("Daño: " + daño);
        System.out.println("Precisión: " + precision + "%");
        System.out.println("Munición: " + municionCargador + " / " + municionReserva);
        System.out.println("Tiempo de Recarga: " + tiempoRecarga + "s");
    }
}
