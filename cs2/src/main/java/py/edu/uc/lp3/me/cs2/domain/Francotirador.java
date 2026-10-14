package py.edu.uc.lp3.me.cs2.domain;

public class Francotirador extends ArmaDeFuego {
    private boolean miraDesplegada;

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
}
