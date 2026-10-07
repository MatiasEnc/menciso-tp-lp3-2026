package py.edu.uc.lp3.me.cs2.domain;

public class RifleAsalto extends ArmaDeFuego {
    public RifleAsalto(String nombre, int precio, Equipo equipo, double daño, double precision,
                       int municionCargador, int municionReserva, double tiempoRecarga) {
        super(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga);
    }
}
