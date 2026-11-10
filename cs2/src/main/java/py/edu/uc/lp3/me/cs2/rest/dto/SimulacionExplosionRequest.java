package py.edu.uc.lp3.me.cs2.rest.dto;

import py.edu.uc.lp3.me.cs2.domain.Blindaje;
import py.edu.uc.lp3.me.cs2.domain.Granada;
import py.edu.uc.lp3.me.cs2.domain.Jugador;

public class SimulacionExplosionRequest {

    private Long armaId;
    private Long jugadorId;
    private Granada granada;
    private Jugador jugador;
    private Blindaje blindaje;
    private double distanciaMetros = 2.0;
    private int disparos = 1;

    public SimulacionExplosionRequest() {
    }

    public SimulacionExplosionRequest(Granada granada, Jugador jugador, Blindaje blindaje, double distanciaMetros) {
        this.granada = granada;
        this.jugador = jugador;
        this.blindaje = blindaje;
        this.distanciaMetros = distanciaMetros;
    }

    public Long getArmaId() {
        return armaId;
    }

    public void setArmaId(Long armaId) {
        this.armaId = armaId;
    }

    public Long getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(Long jugadorId) {
        this.jugadorId = jugadorId;
    }

    public Granada getGranada() {
        return granada;
    }

    public void setGranada(Granada granada) {
        this.granada = granada;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    public Blindaje getBlindaje() {
        return blindaje;
    }

    public void setBlindaje(Blindaje blindaje) {
        this.blindaje = blindaje;
    }

    public double getDistanciaMetros() {
        return distanciaMetros;
    }

    public void setDistanciaMetros(double distanciaMetros) {
        this.distanciaMetros = distanciaMetros;
    }

    public int getDisparos() {
        return disparos;
    }

    public void setDisparos(int disparos) {
        this.disparos = disparos;
    }
}
