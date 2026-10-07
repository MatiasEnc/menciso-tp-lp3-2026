package py.edu.uc.lp3.me.cs2.domain;

public enum Equipo {
    TERRORISTA("Terrorista (T)"),
    COUNTER_TERRORISTA("Counter-Terrorista (CT)"),
    AMBOS("Ambos Equipos (T y CT)");

    private final String descripcion;

    Equipo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
