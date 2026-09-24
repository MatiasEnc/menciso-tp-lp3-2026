package py.edu.uc.lp3.me.cs2;

/**
 * Representa rifles de francotirador de precisión en CS2 (ej. AWP, SSG 08, SCAR-20, G3SG1).
 * Hereda de ArmaDeFuego e incorpora la gestión de niveles de zoom en mira óptica.
 */
public class Francotirador extends ArmaDeFuego {
    private int nivelesZoom;
    private int zoomActual; // 0 = Sin zoom (apuntado libre/noscope), 1 = Primer aumento, 2 = Segundo aumento, etc.

    /**
     * Constructor para Francotirador.
     *
     * @param nombre           Nombre del rifle de francotirador.
     * @param precio           Costo en la tienda.
     * @param equipo           Bando al que pertenece.
     * @param daño             Daño masivo infligido por disparo.
     * @param precision        Precisión base del arma.
     * @param municionCargador Capacidad del cargador.
     * @param municionReserva  Balas de reserva.
     * @param tiempoRecarga    Tiempo en segundos para recargar.
     * @param nivelesZoom      Cantidad de niveles de aumento óptico disponibles (ej. 2 para AWP o SSG 08).
     */
    public Francotirador(String nombre, int precio, Equipo equipo, double daño, double precision,
                         int municionCargador, int municionReserva, double tiempoRecarga, int nivelesZoom) {
        super(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga);
        this.nivelesZoom = nivelesZoom;
        this.zoomActual = 0;
    }

    /**
     * Alterna cíclicamente entre los niveles de aumento óptico disponibles y la vista libre.
     */
    public void cambiarZoom() {
        if (nivelesZoom <= 0) {
            System.out.println("[" + nombre + "] Esta arma no posee niveles de zoom configurables.");
            return;
        }

        zoomActual = (zoomActual + 1) % (nivelesZoom + 1);

        if (zoomActual == 0) {
            System.out.println("[" + nombre + "] Mira óptica cerrada (Sin zoom / modo No-Scope).");
        } else {
            System.out.println("[" + nombre + "] Mira telescópica ajustada a: Nivel de zoom " + zoomActual + "x/" + nivelesZoom + "x.");
        }
    }

    @Override
    public void disparar() {
        if (municionCargador > 0) {
            municionCargador--;
            String estiloDisparo = (zoomActual > 0) 
                    ? " [Disparo de precisión con Zoom " + zoomActual + "x]" 
                    : " [¡NOSCOPE! Disparo sin mira]";
            System.out.println("[" + nombre + "]" + estiloDisparo + " ¡¡BOOOM!! Gran impacto. Daño devastador: " + daño 
                    + " | Balas restantes: " + municionCargador + "/" + capacidadCargador);
        } else {
            super.disparar();
        }
    }

    // --- Getters y Setters ---

    public int getNivelesZoom() {
        return nivelesZoom;
    }

    public void setNivelesZoom(int nivelesZoom) {
        this.nivelesZoom = nivelesZoom;
        if (this.zoomActual > nivelesZoom) {
            this.zoomActual = 0;
        }
    }

    public int getZoomActual() {
        return zoomActual;
    }

    public void setZoomActual(int zoomActual) {
        if (zoomActual >= 0 && zoomActual <= nivelesZoom) {
            this.zoomActual = zoomActual;
        } else {
            System.out.println("Nivel de zoom inválido. Debe estar entre 0 y " + nivelesZoom);
        }
    }

    @Override
    public void mostrarInformacion() {
        super.mostrarInformacion();
        System.out.println("Categoría: Francotirador");
        System.out.println("Niveles de Zoom óptico: " + nivelesZoom);
        System.out.println("Zoom actual: " + (zoomActual == 0 ? "Sin mira" : zoomActual + "x"));
    }
}
