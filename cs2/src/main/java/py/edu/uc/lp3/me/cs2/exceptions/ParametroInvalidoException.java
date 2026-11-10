package py.edu.uc.lp3.me.cs2.exceptions;

/**
 * Excepción hija que se lanza cuando se intenta construir o modificar un objeto
 * del dominio con valores ilegales (ej. precios negativos, nombres vacíos, daño inválido).
 */
public class ParametroInvalidoException extends ArmaException {

    public ParametroInvalidoException(String message) {
        super(message);
    }
}
