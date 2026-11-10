package py.edu.uc.lp3.me.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FRANCOTIRADOR")
public class Francotirador extends ArmaDeFuego {

    @Column(name = "mira_desplegada")
    private boolean miraDesplegada;

    protected Francotirador() {
        super();
        this.miraDesplegada = false;
    }

    public Francotirador(String nombre, int precio, Equipo equipo, double daño, double precision,
                         int municionCargador, int municionReserva, double tiempoRecarga,
                         String imagen, String sonido) {
        super(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga, imagen, sonido);
        this.miraDesplegada = false;
    }

    public Francotirador(String nombre, int precio, Equipo equipo, double daño, double precision,
                         int municionCargador, int municionReserva, double tiempoRecarga) {
        this(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga,
             "awp.png", "sniper_shot.wav");
    }

    public void alternarMira() {
        this.miraDesplegada = !this.miraDesplegada;
    }

    public boolean isMiraDesplegada() { return miraDesplegada; }
    public void setMiraDesplegada(boolean miraDesplegada) { this.miraDesplegada = miraDesplegada; }
}
