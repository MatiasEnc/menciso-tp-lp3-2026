package py.edu.uc.lp3.me.cs2;

/**
 * Representa una escopeta de combate en CS2 (ej. Nova, XM1014, MAG-7, Sawed-Off).
 * Hereda de ArmaDeFuego e incorpora la dispersión múltiple mediante perdigones por disparo.
 */
public class Escopeta extends ArmaDeFuego {
    private int perdigonesPorDisparo;

    /**
     * Constructor para Escopeta.
     *
     * @param nombre               Nombre de la escopeta.
     * @param precio               Costo en la tienda.
     * @param equipo               Bando al que pertenece.
     * @param daño                 Daño individual por cada perdigón impactado.
     * @param precision            Precisión o dispersión de los perdigones.
     * @param municionCargador     Cartuchos en la recámara/tubo cargador.
     * @param municionReserva      Cartuchos en reserva.
     * @param tiempoRecarga        Tiempo necesario para recargar.
     * @param perdigonesPorDisparo Número de proyectiles o postas expulsados en cada cartucho.
     */
    public Escopeta(String nombre, int precio, Equipo equipo, double daño, double precision,
                    int municionCargador, int municionReserva, double tiempoRecarga, int perdigonesPorDisparo) {
        super(nombre, precio, equipo, daño, precision, municionCargador, municionReserva, tiempoRecarga);
        this.perdigonesPorDisparo = perdigonesPorDisparo;
    }

    /**
     * Calcula el daño potencial máximo si todos los perdigones impactan en el objetivo.
     *
     * @return Daño total acumulado.
     */
    public double calcularDañoMaximo() {
        return daño * perdigonesPorDisparo;
    }

    public double calcularDanioMaximo() {
        return calcularDañoMaximo();
    }

    @Override
    public void disparar() {
        if (municionCargador > 0) {
            municionCargador--;
            System.out.println("[" + nombre + "] ¡¡KABOOM!! Descarga de cartucho con " + perdigonesPorDisparo 
                    + " perdigones (" + daño + " c/u). Daño máximo a quemarropa: " + calcularDañoMaximo() 
                    + " | Cartuchos restantes: " + municionCargador + "/" + capacidadCargador);
        } else {
            super.disparar();
        }
    }

    // --- Getters y Setters ---

    public int getPerdigonesPorDisparo() {
        return perdigonesPorDisparo;
    }

    public void setPerdigonesPorDisparo(int perdigonesPorDisparo) {
        this.perdigonesPorDisparo = perdigonesPorDisparo;
    }

    @Override
    public void mostrarInformacion() {
        super.mostrarInformacion();
        System.out.println("Categoría: Escopeta");
        System.out.println("Perdigones por cartucho: " + perdigonesPorDisparo);
        System.out.println("Daño potencial por disparo: " + calcularDañoMaximo() + " (" + daño + " x " + perdigonesPorDisparo + ")");
    }
}
