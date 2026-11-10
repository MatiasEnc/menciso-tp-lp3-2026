package py.edu.uc.lp3.me.cs2.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

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

    @JsonCreator
    public static Equipo fromString(String value) {
        if (value == null || value.isBlank()) {
            return AMBOS;
        }
        String clean = value.trim();
        for (Equipo e : Equipo.values()) {
            if (e.name().equalsIgnoreCase(clean)
                    || e.descripcion.equalsIgnoreCase(clean)
                    || (clean.equalsIgnoreCase("T") && e == TERRORISTA)
                    || (clean.equalsIgnoreCase("CT") && e == COUNTER_TERRORISTA)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Equipo desconocido: " + value + ". Valores válidos: TERRORISTA, COUNTER_TERRORISTA, AMBOS");
    }
}
