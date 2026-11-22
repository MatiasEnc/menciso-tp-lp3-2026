package py.edu.uc.lp3.me.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Modela el blindaje (Kevlar / Casco) en Counter-Strike 2.
 * Guarda únicamente el estado encapsulado. La regla de mitigación y las validaciones
 * se ejecutan en la capa service.
 */
@Embeddable
public class Blindaje {

    @Column(name = "blindaje_tipo")
    private String tipo;

    @Column(name = "blindaje_durabilidad")
    private double durabilidad;         // 0.0 a 100.0 (puntos de armadura)

    @Column(name = "blindaje_absorcion")
    private double porcentajeAbsorcion; // % del daño que mitiga (ej: 50.0%)

    public Blindaje() {
        this("Chaleco Kevlar", 100.0, 50.0);
    }

    public Blindaje(String tipo, double durabilidad) {
        this(tipo, durabilidad, 50.0);
    }

    public Blindaje(String tipo, double durabilidad, double porcentajeAbsorcion) {
        this.tipo = (tipo != null && !tipo.isBlank()) ? tipo : "Chaleco Kevlar";
        this.durabilidad = durabilidad;
        this.porcentajeAbsorcion = porcentajeAbsorcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getDurabilidad() {
        return durabilidad;
    }

    public void setDurabilidad(double durabilidad) {
        this.durabilidad = durabilidad;
    }

    public double getPorcentajeAbsorcion() {
        return porcentajeAbsorcion;
    }

    public void setPorcentajeAbsorcion(double porcentajeAbsorcion) {
        this.porcentajeAbsorcion = porcentajeAbsorcion;
    }

    @Override
    public String toString() {
        return String.format("%s [Durabilidad: %.1f%% | Absorción: %.1f%%]",
                tipo, durabilidad, porcentajeAbsorcion);
    }
}
