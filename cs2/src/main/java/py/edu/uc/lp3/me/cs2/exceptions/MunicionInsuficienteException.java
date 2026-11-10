package py.edu.uc.lp3.me.cs2.exceptions;

/**
 * Excepción hija que se lanza cuando un arma intenta ejecutar una acción de combate
 * o recarga sin contar con munición o carga disponible.
 */
public class MunicionInsuficienteException extends ArmaException {

    public MunicionInsuficienteException(String message) {
        super(message);
    }
}
