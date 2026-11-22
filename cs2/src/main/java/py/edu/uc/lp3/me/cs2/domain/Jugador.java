package py.edu.uc.lp3.me.cs2.domain;

import jakarta.persistence.*;

/**
 * Modela un jugador o combatiente en Counter-Strike 2.
 * Pertenece a una familia independiente de Arma e implementa la interfaz UICapable (POO-07).
 * Guarda únicamente su estado encapsulado; las validaciones y reglas de impacto viven en la capa service.
 */
@Entity
@Table(name = "jugadores")
public class Jugador implements UICapable {

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
        this.nombre = (nombre != null) ? nombre.trim() : null;
        this.vida = vida;
        this.equipo = (equipo != null) ? equipo : Equipo.COUNTER_TERRORISTA;
        this.blindaje = blindaje;
        this.vivo = vida > 0.0;
        this.imagen = (this.equipo == Equipo.TERRORISTA) ? "agent_phoenix.png" : "agent_sas.png";
        this.sonido = "player_radio_affirmative.wav";
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
        this.nombre = (nombre != null) ? nombre.trim() : null;
    }

    public double getVida() {
        return vida;
    }

    public void setVida(double vida) {
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
