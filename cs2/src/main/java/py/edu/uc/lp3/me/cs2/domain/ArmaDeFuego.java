package py.edu.uc.lp3.me.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * Clase abstracta que representa todas las armas de fuego en CS2.
 * Guarda únicamente el estado encapsulado de la entidad.
 * Las validaciones de invariantes (como municionCargador <= capacidadCargador)
 * y las reglas de disparo/recarga viven en la capa service.
 */
@MappedSuperclass
public abstract class ArmaDeFuego extends Arma {

    @Column(name = "dano")
    private double daño;

    @Column(name = "precision_arma")
    private double precision;

    @Column(name = "municion_cargador")
    private int municionCargador;

    @Column(name = "municion_reserva")
    private int municionReserva;

    @Column(name = "tiempo_recarga")
    private double tiempoRecarga;

    @Column(name = "capacidad_cargador")
    private int capacidadCargador;

    /**
     * Constructor protegido requerido por JPA/Jackson con valores por defecto.
     */
    protected ArmaDeFuego() {
        super();
        this.daño = 28.0;
        this.precision = 75.0;
        this.municionCargador = 15;
        this.municionReserva = 45;
        this.tiempoRecarga = 2.2;
        this.capacidadCargador = 20;
    }

    /**
     * Constructor completo con capacidad explícita y multimedia.
     */
    public ArmaDeFuego(String nombre, int precio, Equipo equipo, double daño, double precision,
                       int municionCargador, int capacidadCargador, int municionReserva, double tiempoRecarga,
                       String imagen, String sonido) {
        super(nombre, precio, equipo, imagen, sonido);
        this.daño = daño;
        this.precision = precision;
        this.municionCargador = municionCargador;
        this.capacidadCargador = capacidadCargador;
        this.municionReserva = municionReserva;
        this.tiempoRecarga = tiempoRecarga;
    }

    /**
     * Constructor completo con multimedia (asigna capacidadCargador igual a la carga inicial o 20 por defecto).
     */
    public ArmaDeFuego(String nombre, int precio, Equipo equipo, double daño, double precision,
                       int municionCargador, int municionReserva, double tiempoRecarga,
                       String imagen, String sonido) {
        this(nombre, precio, equipo, daño, precision, municionCargador,
             municionCargador > 0 ? municionCargador : 20, municionReserva, tiempoRecarga, imagen, sonido);
    }

    /**
     * Constructor completo con recursos multimedia por defecto.
     */
    public ArmaDeFuego(String nombre, int precio, Equipo equipo, double daño, double precision,
                       int municionCargador, int municionReserva, double tiempoRecarga) {
        this(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga,
             "firearm.png", "gunshot.wav");
    }

    /**
     * Constructor simple sobrecargado con valores por defecto.
     */
    public ArmaDeFuego(String nombre, int precio, double daño, int municionCargador) {
        this(nombre, precio, Equipo.AMBOS, daño, 75.0, municionCargador, municionCargador * 3, 2.2,
             "firearm.png", "gunshot.wav");
    }

    @Override
    public String ejecutarAccion() {
        return ejecutarAccion(1);
    }

    @Override
    public String ejecutarAccion(int repeticiones) {
        if (municionCargador > 0) {
            int efectuados = Math.min(Math.max(repeticiones, 1), municionCargador);
            return String.format("[%s] Disparo efectuado (%d bala(s)). Daño base: %.1f | Balas en cargador: %d/%d | Sonido: %s",
                    getNombre(), efectuados, daño, municionCargador, capacidadCargador, getSonido());
        } else {
            return String.format("[%s] *Click* Sin munición en el cargador (0/%d). ¡Necesita recargar! | Sonido: empty_click.wav",
                    getNombre(), capacidadCargador);
        }
    }

    public double getDaño() {
        return daño;
    }

    public void setDaño(double daño) {
        this.daño = daño;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public int getMunicionCargador() {
        return municionCargador;
    }

    public void setMunicionCargador(int municionCargador) {
        this.municionCargador = municionCargador;
    }

    public int getMunicionReserva() {
        return municionReserva;
    }

    public void setMunicionReserva(int municionReserva) {
        this.municionReserva = municionReserva;
    }

    public double getTiempoRecarga() {
        return tiempoRecarga;
    }

    public void setTiempoRecarga(double tiempoRecarga) {
        this.tiempoRecarga = tiempoRecarga;
    }

    public int getCapacidadCargador() {
        return capacidadCargador;
    }

    public void setCapacidadCargador(int capacidadCargador) {
        this.capacidadCargador = capacidadCargador;
    }
}
