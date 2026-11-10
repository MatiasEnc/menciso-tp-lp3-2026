package py.edu.uc.lp3.me.cs2.domain;

import jakarta.persistence.*;
import py.edu.uc.lp3.me.cs2.exceptions.ParametroInvalidoException;

/**
 * Modela un jugador o combatiente en Counter-Strike 2.
 * Pertenece a una familia independiente de Arma e implementa también la interfaz UICapable (POO-07).
 * Mapeado como Entidad JPA en H2 para persistir su estado (vida, blindaje, estado vital) tras simulaciones.
 */
@Entity
@Table(name = "jugadores")
public class Jugador implements UI.Capable, UICapable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private double vida;           // 0.0 a 100.0 (HP)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Equipo equipo;         // TERRORISTA o COUNTER_TERRORISTA

    @Embedded
    private Blindaje blindaje;     // Chaleco/Blindaje defensivo

    @Column(nullable = false)
    private boolean vivo;

    @Column(length = 255)
    private String imagen;

    @Column(length = 255)
    private String sonido;

    /**
     * Constructor protegido requerido por JPA/Jackson.
     */
    protected Jugador() {
        this("Bot-Alpha", 100.0, Equipo.COUNTER_TERRORISTA, new Blindaje());
    }

    public Jugador(String nombre, double vida, Equipo equipo) {
        this(nombre, vida, equipo, null);
    }

    public Jugador(String nombre, double vida, Equipo equipo, Blindaje blindaje) {
        if (nombre == null || nombre.isBlank()) {
            throw new ParametroInvalidoException("El nombre del jugador no puede estar vacío");
        }
        if (vida < 0.0 || vida > 100.0) {
            throw new ParametroInvalidoException("La vida del jugador debe estar entre 0.0 y 100.0");
        }
        this.nombre = nombre.trim();
        this.vida = vida;
        this.equipo = (equipo != null) ? equipo : Equipo.COUNTER_TERRORISTA;
        this.blindaje = blindaje;
        this.vivo = vida > 0.0;
        this.imagen = (this.equipo == Equipo.TERRORISTA) ? "agent_phoenix.png" : "agent_sas.png";
        this.sonido = "player_radio_affirmative.wav";
    }

    /**
     * Regla de negocio 2 (Mitigación por blindaje y descuento de salud):
     * Si el jugador cuenta con blindaje activo, este absorbe parte del impacto y se desgasta;
     * el daño residual se descuenta de la vida del jugador.
     *
     * @param danioEntrante Daño bruto recibido antes de armadura.
     * @return Daño final descontado de la vida.
     */
    public double recibirImpacto(double danioEntrante) {
        if (danioEntrante < 0) {
            throw new ParametroInvalidoException("El daño entrante no puede ser negativo");
        }
        double danioFinal = danioEntrante;
        if (this.blindaje != null && this.blindaje.getDurabilidad() > 0.0) {
            danioFinal = this.blindaje.mitigarDanio(danioEntrante);
        }
        recibirDanio(danioFinal);
        return danioFinal;
    }

    /**
     * Aplica el daño final a la salud del jugador tras atravesar el blindaje.
     */
    public void recibirDanio(double danioFinal) {
        this.vida = Math.max(0.0, this.vida - danioFinal);
        if (this.vida <= 0.0) {
            this.vivo = false;
            this.sonido = "player_death.wav";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ParametroInvalidoException("El nombre del jugador no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }

    public double getVida() {
        return vida;
    }

    public void setVida(double vida) {
        if (vida < 0.0 || vida > 100.0) {
            throw new ParametroInvalidoException("La vida del jugador debe estar entre 0.0 y 100.0");
        }
        this.vida = vida;
        this.vivo = vida > 0.0;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = (equipo != null) ? equipo : Equipo.COUNTER_TERRORISTA;
    }

    public Blindaje getBlindaje() {
        return blindaje;
    }

    public void setBlindaje(Blindaje blindaje) {
        this.blindaje = blindaje;
    }

    public boolean isVivo() {
        return vivo;
    }

    public void setVivo(boolean vivo) {
        this.vivo = vivo;
    }

    public boolean tieneBlindaje() {
        return this.blindaje != null && this.blindaje.getDurabilidad() > 0.0;
    }

    @Override
    public String getImagen() {
        return imagen;
    }

    @Override
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String getSonido() {
        return sonido;
    }

    @Override
    public void setSonido(String sonido) {
        this.sonido = sonido;
    }

    @Override
    public String toString() {
        return String.format("%s [ID: %d | Vida: %.1f HP | Equipo: %s | Blindaje: %s | Estado: %s]",
                nombre, id, vida, equipo, tieneBlindaje() ? blindaje.toString() : "Ninguno",
                vivo ? "VIVO" : "ELIMINADO");
    }
}
