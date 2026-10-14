package py.edu.uc.lp3.me.cs2.domain;

public class Escopeta extends ArmaDeFuego {
    private int perdigonesPorDisparo;

    public Escopeta(String nombre, int precio, Equipo equipo, double daño, double precision,
                    int municionCargador, int municionReserva, double tiempoRecarga, int perdigonesPorDisparo,
                    String imagen, String sonido) {
        super(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga, imagen, sonido);
        this.perdigonesPorDisparo = perdigonesPorDisparo;
    }

    public Escopeta(String nombre, int precio, Equipo equipo, double daño, double precision,
                    int municionCargador, int municionReserva, double tiempoRecarga, int perdigonesPorDisparo) {
        this(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga, perdigonesPorDisparo,
             "xm1014.png", "shotgun_blast.wav");
    }

    public int getPerdigonesPorDisparo() { return perdigonesPorDisparo; }
}
