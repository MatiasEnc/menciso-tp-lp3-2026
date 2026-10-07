package py.edu.uc.lp3.me.cs2.domain;

import py.edu.uc.lp3.me.cs2.exceptions.ArmaException;

public class Granada extends Arma {
    private double daño;
    private double radioExplosion;
    private double tiempoActivacion;
    private boolean esLetal;
    private String efecto;
    private boolean lanzada;

    /**
     * Constructor completo.
     */
    public Granada(String nombre, int precio, Equipo equipo, double daño, double radioExplosion,
                   double tiempoActivacion, boolean esLetal, String efecto) {
        super(nombre, precio, equipo);
        if (radioExplosion < 0 || tiempoActivacion < 0) {
            throw new ArmaException("Los valores de radio y tiempo deben ser positivos");
        }
        this.daño = daño;
        this.radioExplosion = radioExplosion;
        this.tiempoActivacion = tiempoActivacion;
        this.esLetal = esLetal;
        this.efecto = efecto;
        this.lanzada = false;
    }

    /**
     * Constructor simple sobrecargado: granada táctica por defecto.
     */
    public Granada(String nombre, int precio, String efecto) {
        this(nombre, precio, Equipo.AMBOS, 0.0, 5.0, 1.5, false, efecto);
    }

    public void lanzar() {
        this.lanzada = true;
    }

    /**
     * Sobrecarga de método de dominio: lanzar con distancia.
     */
    public void lanzar(double distanciaMetros) {
        if (distanciaMetros < 0) {
            throw new ArmaException("La distancia de lanzamiento no puede ser negativa");
        }
        this.lanzada = true;
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
        lanzar();
        double tiempoFinal = Math.max(0.2, tiempoActivacion - retrasoSegundos);
        return String.format("[%s] ¡Granada arrojada y detonada tras %.1fs! Efecto: %s (Radio: %.1fm, Letal: %s)",
                nombre, tiempoFinal, efecto, radioExplosion, esLetal ? "Sí" : "No");
    }

    public double getDaño() { return daño; }
    public double getRadioExplosion() { return radioExplosion; }
    public String getEfecto() { return efecto; }
    public boolean isEsLetal() { return esLetal; }
}
