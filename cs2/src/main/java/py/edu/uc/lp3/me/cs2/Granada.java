/**
 * Representa una granada o artefacto arrojadizo en CS2.
 * Hereda de Arma e implementa mecánicas de lanzamiento y detonación con diversos efectos tácticos.
 */
public class Granada extends Arma {
    protected double daño;
    protected double radioExplosion; // En metros
    protected double tiempoActivacion; // Temporizador en segundos antes de detonar
    protected boolean esLetal;
    protected String efecto;
    private boolean lanzada;

    /**
     * Constructor para inicializar una Granada.
     *
     * @param nombre           Nombre de la granada (ej. "Granada HE", "Flashbang", "Granada de Humo", "Molotov").
     * @param precio           Precio en la tienda.
     * @param equipo           Bando al que pertenece (TERRORISTA, COUNTER_TERRORISTA o AMBOS).
     * @param daño             Daño máximo que puede provocar la explosión/efecto.
     * @param radioExplosion   Radio de alcance del efecto en metros.
     * @param tiempoActivacion Tiempo en segundos desde el lanzamiento hasta la detonación.
     * @param esLetal          Indica si está diseñada para infligir daño mortal.
     * @param efecto           Descripción del efecto táctico producido (ej. "Ceguera", "Humo denso", "Fuego").
     */
    public Granada(String nombre, int precio, Equipo equipo, double daño, double radioExplosion,
                   double tiempoActivacion, boolean esLetal, String efecto) {
        super(nombre, precio, equipo);
        this.daño = daño;
        this.radioExplosion = radioExplosion;
        this.tiempoActivacion = tiempoActivacion;
        this.esLetal = esLetal;
        this.efecto = efecto;
        this.lanzada = false;
    }

    /**
     * Simula el lanzamiento de la granada e inicia la cuenta regresiva del temporizador.
     */
    public void lanzar() {
        this.lanzada = true;
        System.out.println("[" + nombre + "] ¡Granada lanzada! Tiempo para activación: " + tiempoActivacion + " segundos.");
    }

    /**
     * Simula la detonación de la granada liberando su efecto en el área.
     */
    public void detonar() {
        if (!lanzada) {
            System.out.println("[" + nombre + "] Aviso: Se activó la detonación sin haberla lanzado previamente.");
        }
        System.out.println("[" + nombre + "] ¡¡BOOM / ACTIVACIÓN!!");
        System.out.println(" -> Efecto generado: " + efecto);
        System.out.println(" -> Radio alcanzado: " + radioExplosion + " metros.");
        if (esLetal) {
            System.out.println(" -> ¡Alerta letal! Daño infligido en epicentro: " + daño);
        } else {
            System.out.println(" -> Granada táctica no letal (Daño directo: " + daño + ").");
        }
        this.lanzada = false;
    }

    // --- Getters y Setters ---

    public double getDaño() {
        return daño;
    }

    public void setDaño(double daño) {
        this.daño = daño;
    }

    public double getDanio() {
        return daño;
    }

    public void setDanio(double danio) {
        this.daño = danio;
    }

    public double getRadioExplosion() {
        return radioExplosion;
    }

    public void setRadioExplosion(double radioExplosion) {
        this.radioExplosion = radioExplosion;
    }

    public double getTiempoActivacion() {
        return tiempoActivacion;
    }

    public void setTiempoActivacion(double tiempoActivacion) {
        this.tiempoActivacion = tiempoActivacion;
    }

    public boolean isEsLetal() {
        return esLetal;
    }

    public void setEsLetal(boolean esLetal) {
        this.esLetal = esLetal;
    }

    public String getEfecto() {
        return efecto;
    }

    public void setEfecto(String efecto) {
        this.efecto = efecto;
    }

    public boolean isLanzada() {
        return lanzada;
    }

    @Override
    public void mostrarInformacion() {
        super.mostrarInformacion();
        System.out.println("Tipo: Granada / Proyectil");
        System.out.println("Daño: " + daño);
        System.out.println("Radio de Explosión: " + radioExplosion + "m");
        System.out.println("Tiempo de Activación: " + tiempoActivacion + "s");
        System.out.println("¿Es Letal?: " + (esLetal ? "Sí" : "No"));
        System.out.println("Efecto: " + efecto);
    }
}
