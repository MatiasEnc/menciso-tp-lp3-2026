/**
 * Clase base abstracta que modela cualquier tipo de arma en Counter-Strike 2.
 */
public abstract class Arma {
    protected String nombre;
    protected int precio;
    protected Equipo equipo;

    /**
     * Constructor para inicializar los atributos comunes de un arma.
     *
     * @param nombre Nombre del arma (ej. "AK-47", "USP-S", "Granada HE").
     * @param precio Costo en la tienda del juego (en dólares).
     * @param equipo Bando que tiene acceso a esta arma (TERRORISTA, COUNTER_TERRORISTA o AMBOS).
     */
    public Arma(String nombre, int precio, Equipo equipo) {
        this.nombre = nombre;
        this.precio = precio;
        this.equipo = equipo;
    }

    // --- Getters y Setters ---

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    /**
     * Imprime en consola un resumen de la información general del arma.
     */
    public void mostrarInformacion() {
        System.out.println("----------------------------------------");
        System.out.println("Arma: " + nombre);
        System.out.println("Precio: $" + precio);
        System.out.println("Equipo: " + equipo);
    }

    @Override
    public String toString() {
        return String.format("%s [Precio: $%d | Equipo: %s]", nombre, precio, equipo);
    }
}
