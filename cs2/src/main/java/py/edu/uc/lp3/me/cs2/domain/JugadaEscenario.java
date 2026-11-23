package py.edu.uc.lp3.me.cs2.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que persiste en H2 una jugada exitosa del escenario cruzado de POO-09.
 * Solo se guarda cuando tanto la API de A2 (Diana Aquino) como la API de A3 (Fabricio Gonzalez)
 * aprueban sus respectivas reglas de negocio.
 * Guarda únicamente el estado encapsulado (atributos privados).
 */
@Entity
@Table(name = "jugadas_escenario")
public class JugadaEscenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombreEscenario;

    @Column(name = "arma_id")
    private Long armaId;

    @Column(name = "arma_nombre", length = 100)
    private String armaNombre;

    @Column(name = "jugador_id")
    private Long jugadorObjetivoId;

    @Column(name = "jugador_nombre", length = 100)
    private String jugadorObjetivoNombre;

    @Column(name = "equipo_atacante", length = 50)
    private String equipoAtacante;

    @Column(name = "equipo_objetivo", length = 50)
    private String equipoObjetivo;

    @Column(name = "distancia_explosion")
    private double distanciaExplosion;

    @Column(name = "cargador_antes")
    private int cargadorAntes;

    @Column(name = "disparos_rafaga")
    private int disparosRafaga;

    @Column(name = "cargador_despues")
    private int cargadorDespues;

    @Column(name = "vida_antes")
    private double vidaAntes;

    @Column(name = "vida_despues")
    private double vidaDespues;

    @Column(name = "blindaje_despues")
    private double durabilidadBlindajeDespues;

    @Column(name = "validacion_a2_diana", length = 1000)
    private String validacionA2Diana;

    @Column(name = "validacion_a3_fabricio", length = 1000)
    private String validacionA3Fabricio;

    @Column(name = "estado_jugada", length = 80)
    private String estadoJugada;

    @Column(name = "fecha_ejecucion", updatable = false)
    private LocalDateTime fechaEjecucion;

    @PrePersist
    protected void onCreate() {
        this.fechaEjecucion = LocalDateTime.now();
    }

    protected JugadaEscenario() {
    }

    public JugadaEscenario(String nombreEscenario, Long armaId, String armaNombre,
                           Long jugadorObjetivoId, String jugadorObjetivoNombre,
                           String equipoAtacante, String equipoObjetivo, double distanciaExplosion,
                           int cargadorAntes, int disparosRafaga, int cargadorDespues,
                           double vidaAntes, double vidaDespues, double durabilidadBlindajeDespues,
                           String validacionA2Diana, String validacionA3Fabricio, String estadoJugada) {
        this.nombreEscenario = nombreEscenario;
        this.armaId = armaId;
        this.armaNombre = armaNombre;
        this.jugadorObjetivoId = jugadorObjetivoId;
        this.jugadorObjetivoNombre = jugadorObjetivoNombre;
        this.equipoAtacante = equipoAtacante;
        this.equipoObjetivo = equipoObjetivo;
        this.distanciaExplosion = distanciaExplosion;
        this.cargadorAntes = cargadorAntes;
        this.disparosRafaga = disparosRafaga;
        this.cargadorDespues = cargadorDespues;
        this.vidaAntes = vidaAntes;
        this.vidaDespues = vidaDespues;
        this.durabilidadBlindajeDespues = durabilidadBlindajeDespues;
        this.validacionA2Diana = validacionA2Diana;
        this.validacionA3Fabricio = validacionA3Fabricio;
        this.estadoJugada = estadoJugada;
        this.fechaEjecucion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEscenario() {
        return nombreEscenario;
    }

    public void setNombreEscenario(String nombreEscenario) {
        this.nombreEscenario = nombreEscenario;
    }

    public Long getArmaId() {
        return armaId;
    }

    public void setArmaId(Long armaId) {
        this.armaId = armaId;
    }

    public String getArmaNombre() {
        return armaNombre;
    }

    public void setArmaNombre(String armaNombre) {
        this.armaNombre = armaNombre;
    }

    public Long getJugadorObjetivoId() {
        return jugadorObjetivoId;
    }

    public void setJugadorObjetivoId(Long jugadorObjetivoId) {
        this.jugadorObjetivoId = jugadorObjetivoId;
    }

    public String getJugadorObjetivoNombre() {
        return jugadorObjetivoNombre;
    }

    public void setJugadorObjetivoNombre(String jugadorObjetivoNombre) {
        this.jugadorObjetivoNombre = jugadorObjetivoNombre;
    }

    public String getEquipoAtacante() {
        return equipoAtacante;
    }

    public void setEquipoAtacante(String equipoAtacante) {
        this.equipoAtacante = equipoAtacante;
    }

    public String getEquipoObjetivo() {
        return equipoObjetivo;
    }

    public void setEquipoObjetivo(String equipoObjetivo) {
        this.equipoObjetivo = equipoObjetivo;
    }

    public double getDistanciaExplosion() {
        return distanciaExplosion;
    }

    public void setDistanciaExplosion(double distanciaExplosion) {
        this.distanciaExplosion = distanciaExplosion;
    }

    public int getCargadorAntes() {
        return cargadorAntes;
    }

    public void setCargadorAntes(int cargadorAntes) {
        this.cargadorAntes = cargadorAntes;
    }

    public int getDisparosRafaga() {
        return disparosRafaga;
    }

    public void setDisparosRafaga(int disparosRafaga) {
        this.disparosRafaga = disparosRafaga;
    }

    public int getCargadorDespues() {
        return cargadorDespues;
    }

    public void setCargadorDespues(int cargadorDespues) {
        this.cargadorDespues = cargadorDespues;
    }

    public double getVidaAntes() {
        return vidaAntes;
    }

    public void setVidaAntes(double vidaAntes) {
        this.vidaAntes = vidaAntes;
    }

    public double getVidaDespues() {
        return vidaDespues;
    }

    public void setVidaDespues(double vidaDespues) {
        this.vidaDespues = vidaDespues;
    }

    public double getDurabilidadBlindajeDespues() {
        return durabilidadBlindajeDespues;
    }

    public void setDurabilidadBlindajeDespues(double durabilidadBlindajeDespues) {
        this.durabilidadBlindajeDespues = durabilidadBlindajeDespues;
    }

    public String getValidacionA2Diana() {
        return validacionA2Diana;
    }

    public void setValidacionA2Diana(String validacionA2Diana) {
        this.validacionA2Diana = validacionA2Diana;
    }

    public String getValidacionA3Fabricio() {
        return validacionA3Fabricio;
    }

    public void setValidacionA3Fabricio(String validacionA3Fabricio) {
        this.validacionA3Fabricio = validacionA3Fabricio;
    }

    public String getEstadoJugada() {
        return estadoJugada;
    }

    public void setEstadoJugada(String estadoJugada) {
        this.estadoJugada = estadoJugada;
    }

    public LocalDateTime getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(LocalDateTime fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }
}
