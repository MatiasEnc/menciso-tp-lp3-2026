package py.edu.uc.lp3.me.cs2.domain;

import py.edu.uc.lp3.me.cs2.exceptions.ArmaException;

public class Pistola extends ArmaDeFuego {
    private boolean esSilenciada;

    /**
     * Constructor completo.
     */
    public Pistola(String nombre, int precio, Equipo equipo, double daño, double precision,
                   int municionCargador, int municionReserva, double tiempoRecarga, boolean esSilenciada) {
        super(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga);
        this.esSilenciada = esSilenciada;
    }

    /**
     * Constructor simple sobrecargado: crea una pistola con valores estándar.
     */
    public Pistola(String nombre, int precio, double daño) {
        this(nombre, precio, Equipo.AMBOS, daño, 75.0, 12, 24, 2.2, false);
    }

    public void alternarSilenciador() {
        this.esSilenciada = !this.esSilenciada;
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
     */
    @Override
    public String ejecutarAccion(int disparos) {
        if (disparos <= 0) {
            throw new ArmaException("La cantidad de disparos debe ser mayor a cero");
        }
        if (municionCargador > 0) {
            int efectuados = Math.min(disparos, municionCargador);
            municionCargador -= efectuados;
            String modo = esSilenciada ? "Disparo silenciado con sigilo (*Pffft*)" : "Disparo de pistola estándar (¡PUM!)";
            return String.format("[%s] %s (%d tiro(s)). Daño: %.1f | Balas restantes: %d/%d",
                    nombre, modo, efectuados, daño, municionCargador, capacidadCargador);
        } else {
            return String.format("[%s] *Click* Sin munición en el cargador. ¡Necesita recargar!", nombre);
        }
    }

    public boolean isEsSilenciada() { return esSilenciada; }
}
