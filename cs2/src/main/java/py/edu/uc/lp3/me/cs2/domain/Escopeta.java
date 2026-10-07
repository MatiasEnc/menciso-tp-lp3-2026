package py.edu.uc.lp3.me.cs2.domain;

public class Escopeta extends ArmaDeFuego {
    private int perdigonesPorDisparo;

    public Escopeta(String nombre, int precio, Equipo equipo, double daño, double precision,
                    int municionCargador, int municionReserva, double tiempoRecarga, int perdigonesPorDisparo) {
        super(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga);
        this.perdigonesPorDisparo = perdigonesPorDisparo;
    }

    public int getPerdigonesPorDisparo() { return perdigonesPorDisparo; }
}
