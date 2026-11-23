package py.edu.uc.lp3.me.cs2.rest.dto;

public class EscenarioJugadaRequest {

    private Long armaId = 1L;
    private Long jugadorObjetivoId = 1L;
    private String equipoAtacante = "Counter-Terrorista";
    private String equipoObjetivo = "Terrorista";
    private double distanciaExplosion = 2.0;
    private Integer cargadorDisponible;
    private int disparosRafaga = 3;

    public EscenarioJugadaRequest() {
    }

    public Long getArmaId() {
        return armaId;
    }

    public void setArmaId(Long armaId) {
        this.armaId = armaId;
    }

    public Long getJugadorObjetivoId() {
        return jugadorObjetivoId;
    }

    public void setJugadorObjetivoId(Long jugadorObjetivoId) {
        this.jugadorObjetivoId = jugadorObjetivoId;
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

    public Integer getCargadorDisponible() {
        return cargadorDisponible;
    }

    public void setCargadorDisponible(Integer cargadorDisponible) {
        this.cargadorDisponible = cargadorDisponible;
    }

    public int getDisparosRafaga() {
        return disparosRafaga;
    }

    public void setDisparosRafaga(int disparosRafaga) {
        this.disparosRafaga = disparosRafaga;
    }
}
