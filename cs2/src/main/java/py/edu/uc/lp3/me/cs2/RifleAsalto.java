package py.edu.uc.lp3.me.cs2;

/**
 * Representa un rifle de asalto en CS2 (ej. AK-47, M4A4, M4A1-S, AUG, SG 553).
 * Hereda de ArmaDeFuego e incorpora la posibilidad de poseer mira telescópica/óptica.
 */
public class RifleAsalto extends ArmaDeFuego {
    private boolean tieneMira;
    private boolean miraActiva;

    /**
     * Constructor para RifleAsalto.
     *
     * @param nombre           Nombre del rifle de asalto.
     * @param precio           Costo en la tienda.
     * @param equipo           Bando al que pertenece.
     * @param daño             Daño base infligido.
     * @param precision        Precisión base.
     * @param municionCargador Balas en el cargador.
     * @param municionReserva  Balas en la reserva.
     * @param tiempoRecarga    Tiempo en segundos para recargar.
     * @param tieneMira        Indica si el rifle cuenta con mira óptica/telescópica (ej. AUG o SG 553).
     */
    public RifleAsalto(String nombre, int precio, Equipo equipo, double daño, double precision,
                       int municionCargador, int municionReserva, double tiempoRecarga, boolean tieneMira) {
        super(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga);
        this.tieneMira = tieneMira;
        this.miraActiva = false;
    }

    /**
     * Alterna el uso de la mira óptica si el rifle dispone de una.
     */
    public void alternarMira() {
        if (!tieneMira) {
            System.out.println("[" + nombre + "] Este rifle de asalto no cuenta con mira óptica (usa miras de hierro estándar).");
            return;
        }

        miraActiva = !miraActiva;
        if (miraActiva) {
            System.out.println("[" + nombre + "] Mira telescópica activada. Precisión incrementada a larga distancia.");
        } else {
            System.out.println("[" + nombre + "] Mira telescópica desactivada. Apuntado estándar desde la cadera.");
        }
    }

    @Override
    public void disparar() {
        if (municionCargador > 0) {
            municionCargador--;
            String modo = (tieneMira && miraActiva) ? " [Con mira óptica]" : " [Fuego estándar]";
            System.out.println("[" + nombre + "]" + modo + " ¡RATATAT! Ráfaga de asalto. Daño: " + daño 
                    + " | Balas restantes: " + municionCargador + "/" + capacidadCargador);
        } else {
            super.disparar();
        }
    }

    // --- Getters y Setters ---

    public boolean isTieneMira() {
        return tieneMira;
    }

    public void setTieneMira(boolean tieneMira) {
        this.tieneMira = tieneMira;
        if (!tieneMira) {
            this.miraActiva = false;
        }
    }

    public boolean isMiraActiva() {
        return miraActiva;
    }

    public void setMiraActiva(boolean miraActiva) {
        this.miraActiva = miraActiva;
    }

    @Override
    public void mostrarInformacion() {
        super.mostrarInformacion();
        System.out.println("Categoría: Rifle de Asalto");
        System.out.println("¿Posee mira telescópica?: " + (tieneMira ? "Sí" : "No"));
        if (tieneMira) {
            System.out.println("Estado actual de la mira: " + (miraActiva ? "Apuntando" : "Inactiva"));
        }
    }
}
