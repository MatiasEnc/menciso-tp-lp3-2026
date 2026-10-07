package py.edu.uc.lp3.me.cs2.domain;

import py.edu.uc.lp3.me.cs2.exceptions.ArmaException;

/**
 * Clase base abstracta que modela cualquier tipo de arma en Counter-Strike 2.
 */
public abstract class Arma {
    protected String nombre;
    protected int precio;
    protected Equipo equipo;

    /**
     * Constructor completo.
     */
    public Arma(String nombre, int precio, Equipo equipo) {
        if (nombre == null || nombre.isBlank()) {
            throw new ArmaException("El nombre del arma no puede estar vacío");
        }
        if (precio < 0) {
            throw new ArmaException("El precio no puede ser negativo");
        }
        this.nombre = nombre;
        this.precio = precio;
        this.equipo = (equipo != null) ? equipo : Equipo.AMBOS;
    }

    /**
     * Constructor simple sobrecargado: asigna equipo AMBOS por defecto.
     */
    public Arma(String nombre, int precio) {
        this(nombre, precio, Equipo.AMBOS);
    }

    /**
     * Método abstracto de comportamiento (sobreescrito en hijas).
     */
    public abstract String ejecutarAccion();

    /**
     * Sobrecarga de método de dominio: acción con repetición o intensidad.
     */
    public abstract String ejecutarAccion(int repeticiones);

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ArmaException("El nombre no puede estar vacío");
        }
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        if (precio < 0) {
            throw new ArmaException("El precio no puede ser negativo");
        }
        this.precio = precio;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = (equipo != null) ? equipo : Equipo.AMBOS;
    }

    public void mostrarInformacion() {
        System.out.println("Arma: " + nombre + " | Precio: $" + precio + " | Equipo: " + equipo);
    }

    @Override
    public String toString() {
        return String.format("%s [Precio: $%d | Equipo: %s]", nombre, precio, equipo);
    }
}
