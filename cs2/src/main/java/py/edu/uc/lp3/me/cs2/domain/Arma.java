package py.edu.uc.lp3.me.cs2.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import py.edu.uc.lp3.me.cs2.exceptions.ParametroInvalidoException;

import java.time.LocalDateTime;

/**
 * Clase base abstracta que modela cualquier tipo de arma en Counter-Strike 2.
 * Mapeada con estrategia SINGLE_TABLE en H2 y polimorfismo JSON en Jackson.
 */
@Entity
@Table(name = "armas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_arma", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo",
        visible = true,
        defaultImpl = Pistola.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Pistola.class, names = {"Pistola", "PISTOLA", "pistola"}),
        @JsonSubTypes.Type(value = Granada.class, names = {"Granada", "GRANADA", "granada"}),
        @JsonSubTypes.Type(value = RifleAsalto.class, names = {"RifleAsalto", "RIFLE_ASALTO", "rifle_asalto"}),
        @JsonSubTypes.Type(value = Subfusil.class, names = {"Subfusil", "SUBFUSIL", "subfusil"}),
        @JsonSubTypes.Type(value = Francotirador.class, names = {"Francotirador", "FRANCOTIRADOR", "francotirador"}),
        @JsonSubTypes.Type(value = Escopeta.class, names = {"Escopeta", "ESCOPETA", "escopeta"})
})
public abstract class Arma implements UI.Capable, UICapable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private int precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Equipo equipo;

    @Column(length = 255)
    private String imagen;

    @Column(length = 255)
    private String sonido;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
        if (this.equipo == null) {
            this.equipo = Equipo.AMBOS;
        }
        if (this.imagen == null || this.imagen.isBlank()) {
            this.imagen = "default_weapon.png";
        }
        if (this.sonido == null || this.sonido.isBlank()) {
            this.sonido = "default_sound.wav";
        }
    }

    /**
     * Constructor protegido sin argumentos requerido exclusivamente por JPA/Jackson.
     * No es público para preservar el ocultamiento de invariantes en el código de negocio.
     */
    protected Arma() {
        this.equipo = Equipo.AMBOS;
        this.imagen = "default_weapon.png";
        this.sonido = "default_sound.wav";
        this.activo = true;
    }

    /**
     * Constructor completo con multimedia y descripción.
     */
    public Arma(String nombre, int precio, Equipo equipo, String imagen, String sonido, String descripcion) {
        if (nombre == null || nombre.isBlank()) {
            throw new ParametroInvalidoException("El nombre del arma no puede estar vacío");
        }
        if (precio < 0) {
            throw new ParametroInvalidoException("El precio no puede ser negativo");
        }
        this.nombre = nombre.trim();
        this.precio = precio;
        this.equipo = (equipo != null) ? equipo : Equipo.AMBOS;
        this.imagen = (imagen != null && !imagen.isBlank()) ? imagen : "default_weapon.png";
        this.sonido = (sonido != null && !sonido.isBlank()) ? sonido : "default_sound.wav";
        this.descripcion = descripcion;
        this.activo = true;
    }

    /**
     * Constructor completo con multimedia.
     */
    public Arma(String nombre, int precio, Equipo equipo, String imagen, String sonido) {
        this(nombre, precio, equipo, imagen, sonido, null);
    }

    /**
     * Constructor completo con imagen y sonido por defecto.
     */
    public Arma(String nombre, int precio, Equipo equipo) {
        this(nombre, precio, equipo, "default_weapon.png", "default_sound.wav", null);
    }

    /**
     * Constructor simple sobrecargado: asigna equipo AMBOS e imagen/sonido por defecto.
     */
    public Arma(String nombre, int precio) {
        this(nombre, precio, Equipo.AMBOS, "default_weapon.png", "default_sound.wav", null);
    }

    /**
     * Método abstracto de comportamiento (sobreescrito en clases hijas).
     */
    public abstract String ejecutarAccion();

    /**
     * Sobrecarga de método de dominio: acción con repetición o intensidad.
     */
    public abstract String ejecutarAccion(int repeticiones);

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
            throw new ParametroInvalidoException("El nombre del arma no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        if (precio < 0) {
            throw new ParametroInvalidoException("El precio no puede ser negativo");
        }
        this.precio = precio;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = (equipo != null) ? equipo : Equipo.AMBOS;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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
        return String.format("%s [ID: %d | Precio: $%d | Equipo: %s | Imagen: %s | Sonido: %s]",
                nombre, id, precio, equipo, imagen, sonido);
    }
}
