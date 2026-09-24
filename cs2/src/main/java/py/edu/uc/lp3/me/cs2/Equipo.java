package py.edu.uc.lp3.me.cs2;

/**
 * Representa la afiliación o bando al que pertenece un arma en CS2.
 */
public enum Equipo {
    TERRORISTA("Terrorista (T)"),
    COUNTER_TERRORISTA("Counter-Terrorista (CT)"),
    AMBOS("Ambos Equipos (T y CT)");

    private final String nombreLegible;

    Equipo(String nombreLegible) {
        this.nombreLegible = nombreLegible;
    }

    public String getNombreLegible() {
        return nombreLegible;
    }

    @Override
    public String toString() {
        return nombreLegible;
    }
}
