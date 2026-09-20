/**
 * Representa las armas de fuego secundarias tipo Pistola en CS2 (ej. USP-S, Glock-18, Desert Eagle).
 * Hereda de ArmaDeFuego e incorpora la posibilidad de silenciador.
 */
public class Pistola extends ArmaDeFuego {
    private boolean esSilenciada;

    /**
     * Constructor para Pistola.
     *
     * @param nombre           Nombre de la pistola.
     * @param precio           Costo en la tienda.
     * @param equipo           Bando al que pertenece.
     * @param daño             Daño base por impacto.
     * @param precision        Precisión base.
     * @param municionCargador Balas en cargador.
     * @param municionReserva  Balas de reserva.
     * @param tiempoRecarga    Tiempo en segundos para recargar.
     * @param esSilenciada     Indica si el arma cuenta con silenciador acoplado/activo.
     */
    public Pistola(String nombre, int precio, Equipo equipo, double daño, double precision,
                   int municionCargador, int municionReserva, double tiempoRecarga, boolean esSilenciada) {
        super(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga);
        this.esSilenciada = esSilenciada;
    }

    /**
     * Alterna la colocación o extracción del silenciador en la pistola.
     */
    public void alternarSilenciador() {
        this.esSilenciada = !this.esSilenciada;
        if (this.esSilenciada) {
            System.out.println("[" + nombre + "] Silenciador acoplado. Disparos sigilosos y precisión mejorada.");
        } else {
            System.out.println("[" + nombre + "] Silenciador retirado. Disparos con sonido convencional.");
        }
    }

    @Override
    public void disparar() {
        if (municionCargador > 0) {
            municionCargador--;
            if (esSilenciada) {
                System.out.println("[" + nombre + "] *Pffft* (Disparo silenciado con sigilo). Daño: " + daño 
                        + " | Balas restantes: " + municionCargador + "/" + capacidadCargador);
            } else {
                System.out.println("[" + nombre + "] ¡PUM! (Disparo de pistola estándar). Daño: " + daño 
                        + " | Balas restantes: " + municionCargador + "/" + capacidadCargador);
            }
        } else {
            super.disparar(); // Imprime mensaje de cargador vacío
        }
    }

    // --- Getters y Setters ---

    public boolean isEsSilenciada() {
        return esSilenciada;
    }

    public void setEsSilenciada(boolean esSilenciada) {
        this.esSilenciada = esSilenciada;
    }

    @Override
    public void mostrarInformacion() {
        super.mostrarInformacion();
        System.out.println("Categoría: Pistola");
        System.out.println("¿Cuenta con silenciador?: " + (esSilenciada ? "Sí" : "No"));
    }
}
