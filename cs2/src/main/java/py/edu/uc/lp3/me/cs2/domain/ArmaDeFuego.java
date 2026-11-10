package py.edu.uc.lp3.me.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import py.edu.uc.lp3.me.cs2.exceptions.MunicionInsuficienteException;
import py.edu.uc.lp3.me.cs2.exceptions.ParametroInvalidoException;

/**
 * Clase abstracta que representa todas las armas de fuego en CS2.
 * Mapeada como superclase persistente para compartir columnas en sus subclases.
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
     * Constructor protegido requerido por JPA/Jackson con valores por defecto legales.
     */
    protected ArmaDeFuego() {
        super();
        this.daño = 28.0;
        this.precision = 75.0;
        this.municionCargador = 15;
        this.municionReserva = 45;
        this.tiempoRecarga = 2.2;
        this.capacidadCargador = 15;
    }

    /**
     * Constructor completo con multimedia.
     */
    public ArmaDeFuego(String nombre, int precio, Equipo equipo, double daño, double precision,
                       int municionCargador, int municionReserva, double tiempoRecarga,
                       String imagen, String sonido) {
        super(nombre, precio, equipo, imagen, sonido);
        if (daño < 0) {
            throw new ParametroInvalidoException("El daño no puede ser negativo");
        }
        if (precision < 0.0 || precision > 100.0) {
            throw new ParametroInvalidoException("La precisión debe estar entre 0.0 y 100.0");
        }
        if (municionCargador < 0 || municionReserva < 0) {
            throw new ParametroInvalidoException("La munición no puede ser negativa");
        }
        this.daño = daño;
        this.precision = precision;
        this.municionCargador = municionCargador;
        this.municionReserva = municionReserva;
        this.tiempoRecarga = tiempoRecarga;
        this.capacidadCargador = Math.max(municionCargador, 1);
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

    public void disparar() {
        disparar(1);
    }

    /**
     * Regla de negocio 1 (Consumo de munición):
     * Consume balas del cargador. Si no hay munición disponible en el cargador,
     * lanza MunicionInsuficienteException.
     */
    public double disparar(int balas) {
        if (balas <= 0) {
            throw new ParametroInvalidoException("La cantidad de disparos debe ser mayor a cero");
        }
        if (this.municionCargador <= 0) {
            throw new MunicionInsuficienteException(
                    "El arma '" + getNombre() + "' no tiene munición en el cargador (0/" + capacidadCargador + "). ¡Debe recargar!");
        }
        int consumidas = Math.min(balas, municionCargador);
        this.municionCargador -= consumidas;
        return consumidas * this.daño;
    }

    /**
     * Regla de negocio de recarga de munición:
     * Transfiere balas desde la reserva hasta llenar el cargador.
     * Si no quedan balas en la reserva, lanza MunicionInsuficienteException.
     */
    public void recargar() {
        if (this.municionReserva <= 0) {
            throw new MunicionInsuficienteException(
                    "El arma '" + getNombre() + "' no tiene munición de reserva disponible para recargar (reserva: 0).");
        }
        if (this.municionCargador >= this.capacidadCargador) {
            return;
        }
        int necesarias = this.capacidadCargador - this.municionCargador;
        int recarga = Math.min(necesarias, this.municionReserva);
        this.municionCargador += recarga;
        this.municionReserva -= recarga;
    }

    @Override
    public String ejecutarAccion() {
        return ejecutarAccion(1);
    }

    @Override
    public String ejecutarAccion(int repeticiones) {
        if (repeticiones <= 0) {
            throw new ParametroInvalidoException("Las repeticiones deben ser mayores a cero");
        }
        if (municionCargador >= repeticiones) {
            disparar(repeticiones);
            return String.format("[%s] Disparo efectuado (%d bala(s)). Daño base: %.1f | Balas restantes: %d/%d | Sonido: %s",
                    getNombre(), repeticiones, daño, municionCargador, capacidadCargador, getSonido());
        } else if (municionCargador > 0) {
            int gastadas = municionCargador;
            disparar(gastadas);
            return String.format("[%s] Ráfaga parcial de %d bala(s) (cargador agotado). Balas restantes: 0/%d | Sonido: %s",
                    getNombre(), gastadas, capacidadCargador, getSonido());
        } else {
            return String.format("[%s] *Click* Sin munición en el cargador. ¡Necesita recargar! | Sonido: empty_click.wav", getNombre());
        }
    }

    public double getDaño() { return daño; }
    public void setDaño(double daño) {
        if (daño < 0) {
            throw new ParametroInvalidoException("El daño no puede ser negativo");
        }
        this.daño = daño;
    }

    public double getPrecision() { return precision; }
    public void setPrecision(double precision) {
        if (precision < 0.0 || precision > 100.0) {
            throw new ParametroInvalidoException("La precisión debe estar entre 0.0 y 100.0");
        }
        this.precision = precision;
    }

    public int getMunicionCargador() { return municionCargador; }
    public void setMunicionCargador(int municionCargador) {
        if (municionCargador < 0) {
            throw new ParametroInvalidoException("La munición del cargador no puede ser negativa");
        }
        this.municionCargador = municionCargador;
        if (this.capacidadCargador < municionCargador) {
            this.capacidadCargador = municionCargador;
        }
    }

    public int getMunicionReserva() { return municionReserva; }
    public void setMunicionReserva(int municionReserva) {
        if (municionReserva < 0) {
            throw new ParametroInvalidoException("La munición de reserva no puede ser negativa");
        }
        this.municionReserva = municionReserva;
    }

    public double getTiempoRecarga() { return tiempoRecarga; }
    public void setTiempoRecarga(double tiempoRecarga) {
        if (tiempoRecarga < 0) {
            throw new ParametroInvalidoException("El tiempo de recarga no puede ser negativo");
        }
        this.tiempoRecarga = tiempoRecarga;
    }

    public int getCapacidadCargador() { return capacidadCargador; }
    public void setCapacidadCargador(int capacidadCargador) {
        if (capacidadCargador < 0) {
            throw new ParametroInvalidoException("La capacidad del cargador no puede ser negativa");
        }
        this.capacidadCargador = capacidadCargador;
    }
}
