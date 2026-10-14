package py.edu.uc.lp3.me.cs2.domain;

/**
 * Interfaz que define la capacidad de un elemento para representarse en la UI
 * con recursos multimedia de imagen y sonido.
 */
public interface UICapable {
    String getImagen();
    void setImagen(String imagen);
    String getSonido();
    void setSonido(String sonido);
}
