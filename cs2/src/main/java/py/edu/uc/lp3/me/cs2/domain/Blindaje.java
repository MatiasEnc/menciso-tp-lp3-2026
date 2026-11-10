package py.edu.uc.lp3.me.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import py.edu.uc.lp3.me.cs2.exceptions.ParametroInvalidoException;

/**
 * Modela el blindaje (Kevlar / Casco) en Counter-Strike 2.
 * Participa en la simulación absorbiendo un porcentaje del impacto
 * y reduciendo su durabilidad según la fuerza recibida.
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

    public Blindaje(String tipo, double durabilidad, double porcentajeAbsorcion) {
        if (durabilidad < 0.0 || durabilidad > 100.0) {
            throw new ParametroInvalidoException("La durabilidad del blindaje debe estar entre 0.0 y 100.0");
        }
        if (porcentajeAbsorcion < 0.0 || porcentajeAbsorcion > 100.0) {
            throw new ParametroInvalidoException("El porcentaje de absorción debe estar entre 0.0 y 100.0");
        }
        this.tipo = (tipo != null && !tipo.isBlank()) ? tipo : "Chaleco Kevlar";
        this.durabilidad = durabilidad;
        this.porcentajeAbsorcion = porcentajeAbsorcion;
    }

    /**
     * Regla de negocio 2 (Mitigación de daño por Blindaje):
     * El blindaje absorbe hasta su capacidad porcentual, desgastando sus puntos de durabilidad.
     * Retorna el daño residual que impacta directamente en la salud del jugador.
     */
    public double mitigarDanio(double danioEntrante) {
        if (this.durabilidad <= 0.0) {
            return danioEntrante; // Sin armadura, todo el daño pasa directo
        }

        double danioObjetivoAbsorcion = danioEntrante * (this.porcentajeAbsorcion / 100.0);

        if (this.durabilidad >= danioObjetivoAbsorcion) {
            this.durabilidad -= danioObjetivoAbsorcion;
            return Math.max(0.0, danioEntrante - danioObjetivoAbsorcion);
        } else {
            // El blindaje se rompe absorbiendo solo lo que le quedaba de durabilidad
            double danioResidual = danioEntrante - this.durabilidad;
            this.durabilidad = 0.0;
            return Math.max(0.0, danioResidual);
        }
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
        if (durabilidad < 0.0 || durabilidad > 100.0) {
            throw new ParametroInvalidoException("La durabilidad del blindaje debe estar entre 0.0 y 100.0");
        }
        this.durabilidad = durabilidad;
    }

    public double getPorcentajeAbsorcion() {
        return porcentajeAbsorcion;
    }

    public void setPorcentajeAbsorcion(double porcentajeAbsorcion) {
        if (porcentajeAbsorcion < 0.0 || porcentajeAbsorcion > 100.0) {
            throw new ParametroInvalidoException("El porcentaje de absorción debe estar entre 0.0 y 100.0");
        }
        this.porcentajeAbsorcion = porcentajeAbsorcion;
    }

    @Override
    public String toString() {
        return String.format("%s [Durabilidad: %.1f%% | Absorción: %.1f%%]",
                tipo, durabilidad, porcentajeAbsorcion);
    }
}
