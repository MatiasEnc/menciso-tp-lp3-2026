package py.edu.uc.lp3.me.cs2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import py.edu.uc.lp3.me.cs2.exceptions.RechazoCompaneroException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Cliente HTTP de la capa Service que consume las APIs de los dos compañeros del grupo CS2 (POO-09):
 * - A2 (Diana Aquino): POST /api/armas/granadas/simular?equipoAtacante=...&equipoObjetivo=...&distancia=...
 * - A3 (Fabricio Gonzalez): POST /api/reglas/municion con body JSON {"cargador": N, "disparos": M}
 */
@Component
public class CompaneroApiClient {

    private static final Logger log = LoggerFactory.getLogger(CompaneroApiClient.class);

    private final HttpClient httpClient;

    @Value("${cs2.a2.nombre:Diana Aquino}")
    private String a2Nombre;

    @Value("${cs2.a2.base-url:http://localhost:8081}")
    private String a2BaseUrl;

    @Value("${cs2.a2.endpoint:/api/armas/granadas/simular}")
    private String a2Endpoint;

    @Value("${cs2.a3.nombre:Fabricio Gonzalez}")
    private String a3Nombre;

    @Value("${cs2.a3.base-url:http://localhost:8082}")
    private String a3BaseUrl;

    @Value("${cs2.a3.endpoint:/api/reglas/municion}")
    private String a3Endpoint;

    public CompaneroApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Cliente 1 -> Consulta la Regla 2 del OpenAPI de A2 (Diana Aquino):
     * POST {a2BaseUrl}/api/armas/granadas/simular?equipoAtacante={...}&equipoObjetivo={...}&distancia={...}
     * Responde 200 OK si la explosión es legal y sin fuego amigo,
     * 400 Bad Request si distancia < 0 o > 35m, o 422 Unprocessable Entity (FuegoAmigoException) si ambos equipos son iguales.
     */
    public String consultarReglaA2Diana(String equipoAtacante, String equipoObjetivo, double distancia) {
        String eqAtacanteEnc = URLEncoder.encode(equipoAtacante != null ? equipoAtacante : "Counter-Terrorista", StandardCharsets.UTF_8);
        String eqObjetivoEnc = URLEncoder.encode(equipoObjetivo != null ? equipoObjetivo : "Terrorista", StandardCharsets.UTF_8);
        String url = String.format("%s%s?equipoAtacante=%s&equipoObjetivo=%s&distancia=%s",
                normalizarBaseUrl(a2BaseUrl), a2Endpoint, eqAtacanteEnc, eqObjetivoEnc, distancia);

        log.info("[POO-09 -> A2 ({})] Consultando regla de granada y fuego amigo: POST {}", a2Nombre, url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            String body = response.body();

            if (status >= 200 && status < 300) {
                log.info("[POO-09 <- A2 ({})] Validación APROBADA (HTTP {}): {}", a2Nombre, status, body);
                return body;
            }

            log.warn("[POO-09 <- A2 ({})] Validación RECHAZADA (HTTP {}): {}", a2Nombre, status, body);
            throw new RechazoCompaneroException(
                    "A2",
                    "A2 (" + a2Nombre + ")",
                    url,
                    status,
                    body,
                    "Rechazo de A2 (" + a2Nombre + "): la regla de explosión de granada y fuego amigo rechazó la jugada (HTTP " + status + ")."
            );
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RechazoCompaneroException(
                    "A2",
                    "A2 (" + a2Nombre + ")",
                    url,
                    503,
                    e.getMessage(),
                    "No se pudo conectar con el servicio de A2 (" + a2Nombre + ") en " + url + ". Verifique que esté levantado."
            );
        }
    }

    /**
     * Cliente 2 -> Consulta la Regla 1 del OpenAPI de A3 (Fabricio Gonzalez):
     * POST {a3BaseUrl}/api/reglas/municion con JSON {"cargador": N, "disparos": M}
     * Responde 200 OK si cargador >= disparos, o 409 Conflict (SIN_MUNICION) si no alcanza.
     */
    public String consultarReglaA3Fabricio(int cargador, int disparos) {
        String url = normalizarBaseUrl(a3BaseUrl) + a3Endpoint;
        String jsonPayload = String.format("{\"cargador\":%d,\"disparos\":%d}", cargador, disparos);

        log.info("[POO-09 -> A3 ({})] Consultando regla de munición: POST {} body={}", a3Nombre, url, jsonPayload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            String body = response.body();

            if (status >= 200 && status < 300) {
                log.info("[POO-09 <- A3 ({})] Validación APROBADA (HTTP {}): {}", a3Nombre, status, body);
                return body;
            }

            log.warn("[POO-09 <- A3 ({})] Validación RECHAZADA (HTTP {}): {}", a3Nombre, status, body);
            throw new RechazoCompaneroException(
                    "A3",
                    "A3 (" + a3Nombre + ")",
                    url,
                    status,
                    body,
                    "Rechazo de A3 (" + a3Nombre + "): la regla de munición rechazó la ráfaga de disparos (HTTP " + status + ")."
            );
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RechazoCompaneroException(
                    "A3",
                    "A3 (" + a3Nombre + ")",
                    url,
                    503,
                    e.getMessage(),
                    "No se pudo conectar con el servicio de A3 (" + a3Nombre + ") en " + url + ". Verifique que esté levantado."
            );
        }
    }

    private String normalizarBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "http://localhost:8080";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public String getA2BaseUrl() {
        return a2BaseUrl;
    }

    public void setA2BaseUrl(String a2BaseUrl) {
        this.a2BaseUrl = a2BaseUrl;
    }

    public String getA3BaseUrl() {
        return a3BaseUrl;
    }

    public void setA3BaseUrl(String a3BaseUrl) {
        this.a3BaseUrl = a3BaseUrl;
    }
}
