package py.edu.uc.lp3.me.cs2;

public class Pistola extends ArmaDeFuego {
    private boolean esSilenciada;

    public Pistola(String nombre, int precio, Equipo equipo, double daño, double precision,
                   int municionCargador, int municionReserva, double tiempoRecarga, boolean esSilenciada) {
        super(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga);
        this.esSilenciada = esSilenciada;
    }

    public void alternarSilenciador() {
        this.esSilenciada = !this.esSilenciada;
    }

    @Override
    public String ejecutarAccion() {
        if (municionCargador > 0) {
            municionCargador--;
            String modo = esSilenciada ? "Disparo silenciado con sigilo (*Pffft*)" : "Disparo de pistola estándar (¡PUM!)";
            return String.format("[%s] %s. Daño: %.1f | Balas restantes: %d/%d",
                    nombre, modo, daño, municionCargador, capacidadCargador);
        } else {
            return String.format("[%s] *Click* Sin munición en el cargador. ¡Necesita recargar!", nombre);
        }
    }

    public boolean isEsSilenciada() { return esSilenciada; }
}
