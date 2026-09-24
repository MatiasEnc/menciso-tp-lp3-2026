package py.edu.uc.lp3.me.cs2;

public class Granada extends Arma {
    private double daño;
    private double radioExplosion;
    private double tiempoActivacion;
    private boolean esLetal;
    private String efecto;
    private boolean lanzada;

    public Granada(String nombre, int precio, Equipo equipo, double daño, double radioExplosion,
                   double tiempoActivacion, boolean esLetal, String efecto) {
        super(nombre, precio, equipo);
        if (radioExplosion < 0 || tiempoActivacion < 0) {
            throw new IllegalArgumentException("Los valores de radio y tiempo deben ser positivos");
        }
        this.daño = daño;
        this.radioExplosion = radioExplosion;
        this.tiempoActivacion = tiempoActivacion;
        this.esLetal = esLetal;
        this.efecto = efecto;
        this.lanzada = false;
    }

    public void lanzar() { this.lanzada = true; }

    @Override
    public String ejecutarAccion() {
        lanzar();
        return String.format("[%s] ¡Granada arrojada y detonada tras %.1fs! Efecto: %s (Radio: %.1fm, Letal: %s)",
                nombre, tiempoActivacion, efecto, radioExplosion, esLetal ? "Sí" : "No");
    }

    public double getDaño() { return daño; }
    public double getRadioExplosion() { return radioExplosion; }
    public String getEfecto() { return efecto; }
    public boolean isEsLetal() { return esLetal; }
}
