package py.edu.uc.lp3.me.cs2;

/**
 * Clase base abstracta que modela cualquier tipo de arma en Counter-Strike 2.
 */
public abstract class Arma {
    protected String nombre;
    protected int precio;
    protected Equipo equipo;

    public Arma(String nombre, int precio, Equipo equipo) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del arma no puede estar vacío");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        this.nombre = nombre;
        this.precio = precio;
        this.equipo = (equipo != null) ? equipo : Equipo.AMBOS;
    }

    /**
     * Método abstracto de comportamiento (Parte F):
     * Cada arma concreta define cómo actúa en combate, manteniendo su estado encapsulado.
     */
    public abstract String ejecutarAccion();

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        this.precio = precio;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public void mostrarInformacion() {
        System.out.println("Arma: " + nombre + " | Precio: $" + precio + " | Equipo: " + equipo);
    }

    @Override
    public String toString() {
        return String.format("%s [Precio: $%d | Equipo: %s]", nombre, precio, equipo);
    }
}
