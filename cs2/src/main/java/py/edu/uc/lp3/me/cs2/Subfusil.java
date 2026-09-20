/**
 * Representa un subfusil (SMG) en CS2 (ej. MP9, MAC-10, MP7, MP5-SD, P90, PP-Bizon).
 * Hereda de ArmaDeFuego e incorpora una alta cadencia de disparo (RPM).
 */
public class Subfusil extends ArmaDeFuego {
    private int cadenciaDisparo; // Disparos por minuto (RPM - Rounds Per Minute)

    /**
     * Constructor para Subfusil.
     *
     * @param nombre           Nombre del subfusil.
     * @param precio           Costo en la tienda.
     * @param equipo           Bando al que pertenece.
     * @param daño             Daño individual por impacto.
     * @param precision        Precisión en movimiento/estática.
     * @param municionCargador Balas en el cargador.
     * @param municionReserva  Balas en la reserva.
     * @param tiempoRecarga    Tiempo en segundos para recargar.
     * @param cadenciaDisparo  Velocidad de fuego medida en rondas por minuto (RPM).
     */
    public Subfusil(String nombre, int precio, Equipo equipo, double daño, double precision,
                    int municionCargador, int municionReserva, double tiempoRecarga, int cadenciaDisparo) {
        super(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga);
        this.cadenciaDisparo = cadenciaDisparo;
    }

    /**
     * Simula el disparo de una ráfaga continua de varias balas aprovechando la alta cadencia.
     *
     * @param balasADisparar Cantidad de proyectiles que se intentarán disparar en la ráfaga.
     */
    public void dispararRafaga(int balasADisparar) {
        if (municionCargador <= 0) {
            System.out.println("[" + nombre + "] *¡Click!* Imposible iniciar ráfaga, cargador vacío.");
            return;
        }

        int balasReales = Math.min(balasADisparar, municionCargador);
        municionCargador -= balasReales;
        double danioTotal = balasReales * daño;

        System.out.println("[" + nombre + "] ¡BRRRRRRRT! Ráfaga a " + cadenciaDisparo + " RPM disparó " 
                + balasReales + " bala(s). Daño acumulado: " + danioTotal 
                + " | Restantes en cargador: " + municionCargador + "/" + capacidadCargador);

        if (balasReales < balasADisparar) {
            System.out.println("[" + nombre + "] La ráfaga se interrumpió porque el cargador se vació.");
        }
    }

    @Override
    public void disparar() {
        if (municionCargador > 0) {
            municionCargador--;
            System.out.println("[" + nombre + "] ¡Trrrat! Disparo rápido a " + cadenciaDisparo + " RPM. Daño: " + daño 
                    + " | Balas restantes: " + municionCargador + "/" + capacidadCargador);
        } else {
            super.disparar();
        }
    }

    // --- Getters y Setters ---

    public int getCadenciaDisparo() {
        return cadenciaDisparo;
    }

    public void setCadenciaDisparo(int cadenciaDisparo) {
        this.cadenciaDisparo = cadenciaDisparo;
    }

    @Override
    public void mostrarInformacion() {
        super.mostrarInformacion();
        System.out.println("Categoría: Subfusil (SMG)");
        System.out.println("Cadencia de Disparo: " + cadenciaDisparo + " RPM");
    }
}
