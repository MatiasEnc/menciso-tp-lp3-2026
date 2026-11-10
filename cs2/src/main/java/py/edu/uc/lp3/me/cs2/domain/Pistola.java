package py.edu.uc.lp3.me.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import py.edu.uc.lp3.me.cs2.exceptions.ParametroInvalidoException;

@Entity
@DiscriminatorValue("PISTOLA")
public class Pistola extends ArmaDeFuego {

    @Column(name = "es_silenciada")
    private boolean esSilenciada;

    /**
     * Constructor protegido requerido por JPA/Jackson.
     */
    protected Pistola() {
        super();
        this.esSilenciada = false;
    }

    /**
     * Constructor completo con multimedia.
     */
    public Pistola(String nombre, int precio, Equipo equipo, double daño, double precision,
                   int municionCargador, int municionReserva, double tiempoRecarga, boolean esSilenciada,
                   String imagen, String sonido) {
        super(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga, imagen, sonido);
        this.esSilenciada = esSilenciada;
    }

    /**
     * Constructor completo con imagen y sonido automáticos según silenciador.
     */
    public Pistola(String nombre, int precio, Equipo equipo, double daño, double precision,
                   int municionCargador, int municionReserva, double tiempoRecarga, boolean esSilenciada) {
        this(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga, esSilenciada,
             esSilenciada ? "usp_s.png" : "glock18.png",
             esSilenciada ? "pistol_silenced.wav" : "pistol_shot.wav");
    }

    /**
     * Constructor simple sobrecargado: crea una pistola con valores estándar y multimedia.
     */
    public Pistola(String nombre, int precio, double daño) {
        this(nombre, precio, Equipo.AMBOS, daño, 75.0, 12, 24, 2.2, false, "glock18.png", "pistol_shot.wav");
    }

    public void alternarSilenciador() {
        this.esSilenciada = !this.esSilenciada;
        setSonido(this.esSilenciada ? "pistol_silenced.wav" : "pistol_shot.wav");
        setImagen(this.esSilenciada ? "usp_s_silenced.png" : "usp_s.png");
    }

    /**
     * Sobreescritura de la acción base (1 disparo).
     */
    @Override
    public String ejecutarAccion() {
        return ejecutarAccion(1);
    }

    /**
     * Sobrecarga de ejecutarAccion permitiendo ráfagas de disparos.
     * Utiliza los métodos y getters de la clase base ArmaDeFuego para respetar el encapsulamiento.
     */
    @Override
    public String ejecutarAccion(int disparos) {
        if (disparos <= 0) {
            throw new ParametroInvalidoException("La cantidad de disparos debe ser mayor a cero");
        }
        if (getMunicionCargador() > 0) {
            int efectuados = Math.min(disparos, getMunicionCargador());
            disparar(efectuados);
            String modo = esSilenciada ? "Disparo silenciado con sigilo (*Pffft*)" : "Disparo de pistola estándar (¡PUM!)";
            return String.format("[%s] %s (%d tiro(s)). Daño: %.1f | Balas restantes: %d/%d | Sonido: %s",
                    getNombre(), modo, efectuados, getDaño(), getMunicionCargador(), getCapacidadCargador(), getSonido());
        } else {
            return String.format("[%s] *Click* Sin munición en el cargador. ¡Necesita recargar! | Sonido: empty_click.wav", getNombre());
        }
    }

    public boolean isEsSilenciada() { return esSilenciada; }
    public void setEsSilenciada(boolean esSilenciada) { this.esSilenciada = esSilenciada; }
}
