package py.edu.uc.lp3.me.cs2.exceptions;

/**
 * Excepción lanzada por la capa service cuando una de las APIs de los compañeros
 * (A2 Diana Aquino o A3 Fabricio Gonzalez) rechaza la validación del escenario cruzado.
 * Permite atribuir claramente de quién vino el rechazo y abortar la transacción en H2.
 */
public class RechazoCompaneroException extends ArmaException {

    private final String rolCompanero;
    private final String rechazadoPor;
    private final String urlConsultada;
    private final int codigoHttpCompanero;
    private final String detalleCompanero;

    public RechazoCompaneroException(String rolCompanero, String rechazadoPor, String urlConsultada,
                                     int codigoHttpCompanero, String detalleCompanero, String mensaje) {
        super(mensaje);
        this.rolCompanero = rolCompanero;
        this.rechazadoPor = rechazadoPor;
        this.urlConsultada = urlConsultada;
        this.codigoHttpCompanero = codigoHttpCompanero;
        this.detalleCompanero = detalleCompanero;
    }

    public String getRolCompanero() {
        return rolCompanero;
    }

    public String getRechazadoPor() {
        return rechazadoPor;
    }

    public String getUrlConsultada() {
        return urlConsultada;
    }

    public int getCodigoHttpCompanero() {
        return codigoHttpCompanero;
    }

    public String getDetalleCompanero() {
        return detalleCompanero;
    }
}
