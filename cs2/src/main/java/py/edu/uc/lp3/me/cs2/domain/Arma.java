package py.edu.uc.lp3.me.cs2.domain;

import py.edu.uc.lp3.me.cs2.exceptions.ArmaException;

/**
 * Clase base abstracta que modela cualquier tipo de arma en Counter-Strike 2.
 * Implementa la característica UI.Capable para representación multimedia (imagen y sonido).
 */
public abstract class Arma implements UI.Capable, UICapable {
    protected String nombre;
    protected int precio;
    protected Equipo equipo;
    protected String imagen;
    protected String sonido;

    /**
     * Constructor completo con multimedia.
     */
    public Arma(String nombre, int precio, Equipo equipo, String imagen, String sonido) {
        if (nombre == null || nombre.isBlank()) {
            throw new ArmaException("El nombre del arma no puede estar vacío");
        }
        if (precio < 0) {
            throw new ArmaException("El precio no puede ser negativo");
        }
        this.nombre = nombre;
        this.precio = precio;
        this.equipo = (equipo != null) ? equipo : Equipo.AMBOS;
        this.imagen = (imagen != null && !imagen.isBlank()) ? imagen : "default_weapon.png";
        this.sonido = (sonido != null && !sonido.isBlank()) ? sonido : "default_sound.wav";
    }

    /**
     * Constructor completo con imagen y sonido por defecto.
     */
    public Arma(String nombre, int precio, Equipo equipo) {
        this(nombre, precio, equipo, "default_weapon.png", "default_sound.wav");
    }

    /**
     * Constructor simple sobrecargado: asigna equipo AMBOS e imagen/sonido por defecto.
     */
    public Arma(String nombre, int precio) {
        this(nombre, precio, Equipo.AMBOS, "default_weapon.png", "default_sound.wav");
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

    public void mostrarInformacion() {
        System.out.println("Arma: " + nombre + " | Precio: $" + precio + " | Equipo: " + equipo
                + " | Imagen: " + imagen + " | Sonido: " + sonido);
    }

    @Override
    public String toString() {
        return String.format("%s [Precio: $%d | Equipo: %s | Imagen: %s | Sonido: %s]",
                nombre, precio, equipo, imagen, sonido);
    }
}
