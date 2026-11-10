package py.edu.uc.lp3.me.cs2.rest.dto;

public class ResultadoSimulacion {

    private Long armaId;
    private String armaUtilizada;
    private Long jugadorId;
    private String jugadorObjetivo;
    private boolean persistidoEnH2;
    private boolean enRadioAlcance;
    private double distanciaMetros;
    private double radioExplosion;
    private double danioNominal;
    private double danioAtenuadoDistancia;
    private boolean fuegoAmigo;
    private double danioDespuesFuegoAmigo;
    private double danioAbsorbidoBlindaje;
    private double durabilidadBlindajeRestante;
    private double danioFinalVida;
    private double vidaRestante;
    private String estadoJugador;
    private String detalleRegla;

    public ResultadoSimulacion() {
    }

    public Long getArmaId() {
        return armaId;
    }

    public void setArmaId(Long armaId) {
        this.armaId = armaId;
    }

    public String getArmaUtilizada() {
        return armaUtilizada;
    }

    public void setArmaUtilizada(String armaUtilizada) {
        this.armaUtilizada = armaUtilizada;
    }

    public Long getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(Long jugadorId) {
        this.jugadorId = jugadorId;
    }

    public String getJugadorObjetivo() {
        return jugadorObjetivo;
    }

    public void setJugadorObjetivo(String jugadorObjetivo) {
        this.jugadorObjetivo = jugadorObjetivo;
    }

    public boolean isPersistidoEnH2() {
        return persistidoEnH2;
    }

    public void setPersistidoEnH2(boolean persistidoEnH2) {
        this.persistidoEnH2 = persistidoEnH2;
    }

    public boolean isEnRadioAlcance() {
        return enRadioAlcance;
    }

    public void setEnRadioAlcance(boolean enRadioAlcance) {
        this.enRadioAlcance = enRadioAlcance;
    }

    public double getDistanciaMetros() {
        return distanciaMetros;
    }

    public void setDistanciaMetros(double distanciaMetros) {
        this.distanciaMetros = distanciaMetros;
    }

    public double getRadioExplosion() {
        return radioExplosion;
    }

    public void setRadioExplosion(double radioExplosion) {
        this.radioExplosion = radioExplosion;
    }

    public double getDanioNominal() {
        return danioNominal;
    }

    public void setDanioNominal(double danioNominal) {
        this.danioNominal = danioNominal;
    }

    public double getDanioAtenuadoDistancia() {
        return danioAtenuadoDistancia;
    }

    public void setDanioAtenuadoDistancia(double danioAtenuadoDistancia) {
        this.danioAtenuadoDistancia = danioAtenuadoDistancia;
    }

    public boolean isFuegoAmigo() {
        return fuegoAmigo;
    }

    public void setFuegoAmigo(boolean fuegoAmigo) {
        this.fuegoAmigo = fuegoAmigo;
    }

    public double getDanioDespuesFuegoAmigo() {
        return danioDespuesFuegoAmigo;
    }

    public void setDanioDespuesFuegoAmigo(double danioDespuesFuegoAmigo) {
        this.danioDespuesFuegoAmigo = danioDespuesFuegoAmigo;
    }

    public double getDanioAbsorbidoBlindaje() {
        return danioAbsorbidoBlindaje;
    }

    public void setDanioAbsorbidoBlindaje(double danioAbsorbidoBlindaje) {
        this.danioAbsorbidoBlindaje = danioAbsorbidoBlindaje;
    }

    public double getDurabilidadBlindajeRestante() {
        return durabilidadBlindajeRestante;
    }

    public void setDurabilidadBlindajeRestante(double durabilidadBlindajeRestante) {
        this.durabilidadBlindajeRestante = durabilidadBlindajeRestante;
    }

    public double getDanioFinalVida() {
        return danioFinalVida;
    }

    public void setDanioFinalVida(double danioFinalVida) {
        this.danioFinalVida = danioFinalVida;
    }

    public double getVidaRestante() {
        return vidaRestante;
    }

    public void setVidaRestante(double vidaRestante) {
        this.vidaRestante = vidaRestante;
    }

    public String getEstadoJugador() {
        return estadoJugador;
    }

    public void setEstadoJugador(String estadoJugador) {
        this.estadoJugador = estadoJugador;
    }

    public String getDetalleRegla() {
        return detalleRegla;
    }

    public void setDetalleRegla(String detalleRegla) {
        this.detalleRegla = detalleRegla;
    }
}
