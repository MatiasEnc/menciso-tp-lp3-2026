package py.edu.uc.lp3.me.cs2.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUBFUSIL")
public class Subfusil extends ArmaDeFuego {

    protected Subfusil() {
        super();
    }

    public Subfusil(String nombre, int precio, Equipo equipo, double daño, double precision,
                    int municionCargador, int municionReserva, double tiempoRecarga,
                    String imagen, String sonido) {
        super(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga, imagen, sonido);
    }

    public Subfusil(String nombre, int precio, Equipo equipo, double daño, double precision,
                    int municionCargador, int municionReserva, double tiempoRecarga) {
        this(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga,
             "mp9.png", "subfusil_shot.wav");
    }
}
