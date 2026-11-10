package py.edu.uc.lp3.me.cs2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handler para la primera excepción hija: ParametroInvalidoException.
     * Se dispara cuando se violan invariantes de construcción o atributos (ej. precios negativos).
     */
    @ExceptionHandler(ParametroInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleParametroInvalidoException(ParametroInvalidoException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("tipoExcepcion", ex.getClass().getSimpleName());
        body.put("excepcionPadre", "ArmaException");
        body.put("error", "Violación de Invariante de Dominio");
        body.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handler para la segunda excepción hija: MunicionInsuficienteException.
     * Se dispara cuando se intenta disparar, recargar o detonar un arma sin carga disponible.
     */
    @ExceptionHandler(MunicionInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleMunicionInsuficienteException(MunicionInsuficienteException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("tipoExcepcion", ex.getClass().getSimpleName());
        body.put("excepcionPadre", "ArmaException");
        body.put("error", "Regla de Juego Incumplida: Sin Munición o Carga");
        body.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Handler para recurso inexistente (ID no encontrado en H2).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("tipoExcepcion", ex.getClass().getSimpleName());
        body.put("excepcionPadre", "ArmaException");
        body.put("error", "Recurso No Encontrado");
        body.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handler general para la clase base ArmaException o argumentos ilegales.
     */
    @ExceptionHandler({ArmaException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> handleArmaException(RuntimeException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("tipoExcepcion", ex.getClass().getSimpleName());
        body.put("error", "Error de Dominio CS2");
        body.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Desempaqueta excepciones del dominio lanzadas desde setters/constructores durante la deserialización JSON de Jackson.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof ParametroInvalidoException pEx) {
            return handleParametroInvalidoException(pEx);
        }
        if (cause instanceof MunicionInsuficienteException mEx) {
            return handleMunicionInsuficienteException(mEx);
        }
        if (cause instanceof ArmaException aEx) {
            return handleArmaException(aEx);
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("tipoExcepcion", "ParametroInvalidoException");
        body.put("excepcionPadre", "ArmaException");
        body.put("error", "JSON o Parámetro Inválido");
        body.put("mensaje", cause != null ? cause.getMessage() : ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
