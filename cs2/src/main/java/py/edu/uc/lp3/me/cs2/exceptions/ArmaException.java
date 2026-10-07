package py.edu.uc.lp3.me.cs2.exceptions;

/**
 * Excepción para violaciones de invariantes y reglas de negocio del dominio CS2.
 */
public class ArmaException extends RuntimeException {

    public ArmaException(String message) {
        super(message);
    }

    public ArmaException(String message, Throwable cause) {
        super(message, cause);
    }
}
