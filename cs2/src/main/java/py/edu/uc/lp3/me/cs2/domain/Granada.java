package py.edu.uc.lp3.me.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("GRANADA")
public class Granada extends Arma {

    @Column(name = "dano_explosivo")
    private double daño;

    @Column(name = "radio_explosion")
    private double radioExplosion;

    @Column(name = "tiempo_activacion")
    private double tiempoActivacion;

    @Column(name = "es_letal")
    private boolean esLetal;

    @Column(name = "efecto")
    private String efecto;

    @Column(name = "lanzada")
    private boolean lanzada;

    /**
     * Constructor protegido requerido por JPA/Jackson.
     */
    protected Granada() {
        super();
        this.daño = 98.0;
        this.radioExplosion = 8.0;
        this.tiempoActivacion = 2.0;
        this.esLetal = true;
        this.efecto = "Explosión fragmentaria";
        this.lanzada = false;
    }

    /**
     * Constructor completo con multimedia.
     */
    public Granada(String nombre, int precio, Equipo equipo, double daño, double radioExplosion,
                   double tiempoActivacion, boolean esLetal, String efecto,
                   String imagen, String sonido) {
        super(nombre, precio, equipo, imagen, sonido);
        this.daño = daño;
        this.radioExplosion = radioExplosion;
        this.tiempoActivacion = tiempoActivacion;
        this.esLetal = esLetal;
        this.efecto = (efecto != null && !efecto.isBlank()) ? efecto : "Explosión fragmentaria";
        this.lanzada = false;
    }

    /**
     * Constructor completo con imagen y sonido por defecto.
     */
    public Granada(String nombre, int precio, Equipo equipo, double daño, double radioExplosion,
                   double tiempoActivacion, boolean esLetal, String efecto) {
        this(nombre, precio, equipo, daño, radioExplosion, tiempoActivacion, esLetal, efecto,
             "granada.png", "grenade_explosion.wav");
    }

    /**
     * Constructor simple sobrecargado: granada táctica por defecto.
     */
    public Granada(String nombre, int precio, String efecto) {
        this(nombre, precio, Equipo.AMBOS, 0.0, 5.0, 1.5, false, efecto,
             "granada_tactica.png", "flashbang_bang.wav");
    }

    /**
     * Constructor simple sobrecargado con radio de explosión.
     */
    public Granada(String nombre, int precio, double radioExplosion) {
        this(nombre, precio, Equipo.AMBOS, 98.0, radioExplosion, 2.0, true, "Explosión fragmentaria",
             "granada.png", "grenade_explosion.wav");
    }

    /**
     * Sobreescritura del método abstracto base ejecutarAccion().
     */
    @Override
    public String ejecutarAccion() {
        return ejecutarAccion(0);
    }

    /**
     * Sobrecarga de ejecutarAccion con retraso de activación (cocinado).
     */
    @Override
    public String ejecutarAccion(int retrasoSegundos) {
        double tiempoFinal = Math.max(0.2, tiempoActivacion - Math.max(retrasoSegundos, 0));
        return String.format("[%s] ¡Granada arrojada y detonada tras %.1fs! Efecto: %s (Radio: %.1fm, Letal: %s) | Sonido: %s",
                getNombre(), tiempoFinal, efecto, radioExplosion, esLetal ? "Sí" : "No", getSonido());
    }

    public double getDaño() {
        return daño;
    }

    public void setDaño(double daño) {
        this.daño = daño;
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

    public void setLanzada(boolean lanzada) {
        this.lanzada = lanzada;
    }
}
