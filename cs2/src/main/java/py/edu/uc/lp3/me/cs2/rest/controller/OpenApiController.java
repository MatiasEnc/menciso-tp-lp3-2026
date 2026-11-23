package py.edu.uc.lp3.me.cs2.rest.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador que publica el documento OpenAPI 3.0.3 del servicio Counter-Strike 2 en /v3/api-docs
 * y un visor Swagger UI interactivo en /swagger-ui.html.
 * Permite que los compañeros del grupo (A2 y A3) importen el contrato en Insomnia o lo consulten
 * en el navegador sin necesidad de abrir el código fuente Java.
 */
@RestController
public class OpenApiController {

    @GetMapping(value = "/v3/api-docs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> obtenerOpenApi() {
        Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("openapi", "3.0.3");

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("title", "Counter-Strike 2 (CS2) — API de Armas, Reglas de Juego y Simulación (POO-09)");
        info.put("version", "1.0.0");
        info.put("description",
                "API REST con persistencia en H2 sobre el dominio Counter-Strike 2 (Autor: Matias Enciso). "
                + "Incluye CRUD polimórfico de la familia Arma (Pistola, Granada, RifleAsalto, Subfusil, Francotirador, Escopeta), "
                + "catálogo de la interfaz UICapable (Arma y Jugador) y dos reglas de negocio que alimentan la simulación persistida: "
                + "Regla 1 (consumo de munición/carga única de granada, atenuación por distancia e impacto de fuego amigo al 35%) y "
                + "Regla 2 (mitigación porcentual de daño con chaleco Kevlar, desgaste de durabilidad y descuento de vida del Jugador).");
        info.put("contact", Map.of(
                "name", "Matias Enciso",
                "url", "https://github.com/MatiasEnc/menciso-tp-lp3-2026"
        ));
        doc.put("info", info);

        doc.put("servers", List.of(
                Map.of("url", "http://localhost:8080", "description", "Servidor local por defecto (A1)")
        ));

        doc.put("tags", List.of(
                Map.of("name", "1. Reglas de Juego y Simulación", "description", "Endpoints que ejecutan las dos reglas de negocio de CS2 y la simulación persistida en H2."),
                Map.of("name", "2. CRUD de Armas", "description", "Operaciones CRUD sobre la familia Arma con validación de invariantes en ArmaService."),
                Map.of("name", "3. Interfaz UICapable y Polimorfismo", "description", "Endpoints que exponen la interfaz de dominio UICapable y sobreescritura/sobrecarga.")
        ));

        Map<String, Object> paths = new LinkedHashMap<>();

        // 1. POST /api/simulacion/explosion
        paths.put("/api/simulacion/explosion", Map.of(
                "post", Map.of(
                        "tags", List.of("1. Reglas de Juego y Simulación"),
                        "summary", "Simulación persistida de impacto/explosión en H2 (combina Regla 1 y Regla 2)",
                        "operationId", "simularImpactoPersistido",
                        "description", "Ejecuta las 2 reglas de negocio sobre tres clases del modelo (Granada/Pistola, Jugador y Blindaje) y guarda el estado resultante en H2. "
                                + "Regla 1: Verifica y consume la carga de la granada o las balas del cargador, atenúa el daño según la distancia al radio de explosión y reduce el daño al 35% si hay fuego amigo. "
                                + "Regla 2: El Blindaje Kevlar absorbe su porcentaje de daño desgastando su durabilidad en H2, y el daño residual se descuenta de la vida del Jugador.",
                        "requestBody", Map.of(
                                "required", false,
                                "content", Map.of("application/json", Map.of(
                                        "schema", Map.of("$ref", "#/components/schemas/SimulacionExplosionRequest"),
                                        "example", Map.of("armaId", 3, "jugadorId", 1, "distanciaMetros", 2.0, "disparos", 1)
                                ))
                        ),
                        "responses", Map.of(
                                "200", Map.of(
                                        "description", "Simulación ejecutada y persistida exitosamente en H2.",
                                        "content", Map.of("application/json", Map.of(
                                                "schema", Map.of("$ref", "#/components/schemas/ResultadoSimulacion")
                                        ))
                                ),
                                "400", Map.of(
                                        "description", "ParametroInvalidoException: distancia negativa o invariante de arma/jugador/blindaje inválido.",
                                        "content", Map.of("application/json", Map.of("schema", Map.of("$ref", "#/components/schemas/ErrorResponse")))
                                ),
                                "404", Map.of(
                                        "description", "ResourceNotFoundException: armaId o jugadorId no existe en H2.",
                                        "content", Map.of("application/json", Map.of("schema", Map.of("$ref", "#/components/schemas/ErrorResponse")))
                                ),
                                "409", Map.of(
                                        "description", "MunicionInsuficienteException: la granada ya fue detonada previamente o el arma no tiene balas en el cargador.",
                                        "content", Map.of("application/json", Map.of("schema", Map.of("$ref", "#/components/schemas/ErrorResponse")))
                                )
                        )
                ),
                "get", Map.of(
                        "tags", List.of("1. Reglas de Juego y Simulación"),
                        "summary", "Simulación de explosión por parámetros de consulta (solo lectura, ideal para validación externa)",
                        "operationId", "simularPorParametros",
                        "description", "Evalúa las dos reglas de negocio (atenuación por distancia/fuego amigo y mitigación por blindaje Kevlar) recibiendo parámetros simples por URL sin modificar H2. Rechaza distancias negativas con HTTP 400 (ParametroInvalidoException).",
                        "parameters", List.of(
                                Map.of("name", "distancia", "in", "query", "required", false, "description", "Distancia en metros al epicentro (>= 0). Si es negativa devuelve 400.", "schema", Map.of("type", "number", "default", 2.5)),
                                Map.of("name", "conBlindaje", "in", "query", "required", false, "description", "Indica si el jugador objetivo lleva Chaleco Kevlar (100% durabilidad, 50% absorción).", "schema", Map.of("type", "boolean", "default", true)),
                                Map.of("name", "fuegoAmigo", "in", "query", "required", false, "description", "Indica si el atacante y el objetivo son del mismo equipo (reduce el daño al 35%).", "schema", Map.of("type", "boolean", "default", false))
                        ),
                        "responses", Map.of(
                                "200", Map.of(
                                        "description", "Cálculo de las reglas de negocio completado.",
                                        "content", Map.of("application/json", Map.of("schema", Map.of("$ref", "#/components/schemas/ResultadoSimulacion")))
                                ),
                                "400", Map.of(
                                        "description", "ParametroInvalidoException: distancia negativa.",
                                        "content", Map.of("application/json", Map.of("schema", Map.of("$ref", "#/components/schemas/ErrorResponse")))
                                )
                        )
                )
        ));

        // 2. POST /api/armas/{id}/disparar
        paths.put("/api/armas/{id}/disparar", Map.of(
                "post", Map.of(
                        "tags", List.of("1. Reglas de Juego y Simulación"),
                        "summary", "Regla 1: Disparar o activar un arma persistida en H2 (consume munición o carga)",
                        "operationId", "dispararArma",
                        "description", "Aplica la Regla 1 de negocio sobre el arma indicada por ID: valida que la cantidad de balas sea > 0 (si es <= 0 lanza ParametroInvalidoException -> 400) y que el arma tenga munición en el cargador o no haya sido detonada (si está vacía lanza MunicionInsuficienteException -> 409). Descuenta las balas y persiste en H2.",
                        "parameters", List.of(
                                Map.of("name", "id", "in", "path", "required", true, "description", "ID del arma en H2 (ej. 1 para Glock-18, 2 para USP-S, 3 para Granada HE).", "schema", Map.of("type", "integer")),
                                Map.of("name", "balas", "in", "query", "required", false, "description", "Cantidad de balas a disparar (debe ser > 0).", "schema", Map.of("type", "integer", "default", 1))
                        ),
                        "responses", Map.of(
                                "200", Map.of("description", "Disparo efectuado y munición descontada en H2.", "content", Map.of("application/json", Map.of("schema", Map.of("$ref", "#/components/schemas/Arma")))),
                                "400", Map.of("description", "ParametroInvalidoException: cantidad de balas <= 0 o ID inválido.", "content", Map.of("application/json", Map.of("schema", Map.of("$ref", "#/components/schemas/ErrorResponse")))),
                                "404", Map.of("description", "ResourceNotFoundException: no existe arma con ese ID.", "content", Map.of("application/json", Map.of("schema", Map.of("$ref", "#/components/schemas/ErrorResponse")))),
                                "409", Map.of("description", "MunicionInsuficienteException: cargador sin balas (0) o granada ya detonada.", "content", Map.of("application/json", Map.of("schema", Map.of("$ref", "#/components/schemas/ErrorResponse"))))
                        )
                )
        ));

        // 3. POST /api/armas/{id}/recargar
        paths.put("/api/armas/{id}/recargar", Map.of(
                "post", Map.of(
                        "tags", List.of("1. Reglas de Juego y Simulación"),
                        "summary", "Regla de Recarga: Transfiere munición desde la reserva al cargador hasta su capacidadCargador",
                        "operationId", "recargarArma",
                        "description", "Recarga un arma de fuego persistida en H2 respetando el tope de capacidadCargador. Si es una Granada o si municionReserva es 0, lanza MunicionInsuficienteException (HTTP 409).",
                        "parameters", List.of(
                                Map.of("name", "id", "in", "path", "required", true, "description", "ID del arma en H2.", "schema", Map.of("type", "integer"))
                        ),
                        "responses", Map.of(
                                "200", Map.of("description", "Arma recargada y guardada en H2.", "content", Map.of("application/json", Map.of("schema", Map.of("$ref", "#/components/schemas/Arma")))),
                                "404", Map.of("description", "ResourceNotFoundException: ID no encontrado.", "content", Map.of("application/json", Map.of("schema", Map.of("$ref", "#/components/schemas/ErrorResponse")))),
                                "409", Map.of("description", "MunicionInsuficienteException: el arma no tiene reserva disponible o es una Granada.", "content", Map.of("application/json", Map.of("schema", Map.of("$ref", "#/components/schemas/ErrorResponse"))))
                        )
                )
        ));

        // 4. GET /api/simulacion/explosion/jugadores y /jugadores/{id}
        paths.put("/api/simulacion/explosion/jugadores", Map.of(
                "get", Map.of(
                        "tags", List.of("1. Reglas de Juego y Simulación"),
                        "summary", "Lista todos los jugadores persistidos en H2 con su vida y durabilidad de blindaje",
                        "operationId", "listarJugadores",
                        "responses", Map.of("200", Map.of("description", "Lista de jugadores en H2."))
                )
        ));

        paths.put("/api/simulacion/explosion/jugadores/{id}", Map.of(
                "get", Map.of(
                        "tags", List.of("1. Reglas de Juego y Simulación"),
                        "summary", "Obtiene el estado persistido de un jugador por ID (vida y durabilidad de blindaje tras simular)",
                        "operationId", "obtenerJugadorPorId",
                        "parameters", List.of(
                                Map.of("name", "id", "in", "path", "required", true, "description", "ID del jugador en H2 (1 = s1mple CT, 2 = Phoenix T).", "schema", Map.of("type", "integer"))
                        ),
                        "responses", Map.of(
                                "200", Map.of("description", "Estado actual del jugador en H2."),
                                "404", Map.of("description", "ResourceNotFoundException: jugador no encontrado.")
                        )
                )
        ));

        // 5. CRUD /api/armas
        paths.put("/api/armas", Map.of(
                "get", Map.of(
                        "tags", List.of("2. CRUD de Armas"),
                        "summary", "Lista todas las armas persistidas en H2",
                        "operationId", "listarArmas",
                        "responses", Map.of("200", Map.of("description", "Listado polimórfico de armas."))
                ),
                "post", Map.of(
                        "tags", List.of("2. CRUD de Armas"),
                        "summary", "Crea una nueva arma en H2 validando todos los invariantes de CS2 en ArmaService",
                        "operationId", "crearArma",
                        "description", "Crea una subclase de Arma discriminada por la propiedad 'tipo' (Pistola, Granada, RifleAsalto, Subfusil, Francotirador, Escopeta). "
                                + "Invariantes validados en ArmaService: nombre obligatorio (>= 2 caracteres y único), precio >= 0, daño >= 0, 0 <= precision <= 100, capacidadCargador > 0, municionCargador >= 0 y municionCargador <= capacidadCargador.",
                        "requestBody", Map.of(
                                "required", true,
                                "content", Map.of("application/json", Map.of(
                                        "schema", Map.of("$ref", "#/components/schemas/Arma"),
                                        "example", Map.ofEntries(
                                                Map.entry("tipo", "Pistola"),
                                                Map.entry("nombre", "Five-SeveN"),
                                                Map.entry("descripcion", "Pistola semiautomática CT"),
                                                Map.entry("precio", 500),
                                                Map.entry("equipo", "COUNTER_TERRORISTA"),
                                                Map.entry("daño", 32.0),
                                                Map.entry("precision", 82.0),
                                                Map.entry("municionCargador", 20),
                                                Map.entry("capacidadCargador", 20),
                                                Map.entry("municionReserva", 100),
                                                Map.entry("esSilenciada", false),
                                                Map.entry("activo", true)
                                        )
                                ))
                        ),
                        "responses", Map.of(
                                "201", Map.of("description", "Arma creada y persistida en H2."),
                                "400", Map.of("description", "ParametroInvalidoException: precio negativo, municionCargador > capacidadCargador o nombre duplicado.")
                        )
                )
        ));

        paths.put("/api/armas/{id}", Map.of(
                "get", Map.of(
                        "tags", List.of("2. CRUD de Armas"),
                        "summary", "Obtiene un arma por su ID",
                        "operationId", "obtenerArmaPorId",
                        "parameters", List.of(Map.of("name", "id", "in", "path", "required", true, "schema", Map.of("type", "integer"))),
                        "responses", Map.of(
                                "200", Map.of("description", "Arma encontrada."),
                                "404", Map.of("description", "ResourceNotFoundException: ID inexistente.")
                        )
                ),
                "put", Map.of(
                        "tags", List.of("2. CRUD de Armas"),
                        "summary", "Actualiza un arma existente validando sus invariantes en ArmaService",
                        "operationId", "actualizarArma",
                        "parameters", List.of(Map.of("name", "id", "in", "path", "required", true, "schema", Map.of("type", "integer"))),
                        "requestBody", Map.of("required", true, "content", Map.of("application/json", Map.of("schema", Map.of("$ref", "#/components/schemas/Arma")))),
                        "responses", Map.of(
                                "200", Map.of("description", "Arma actualizada en H2."),
                                "400", Map.of("description", "ParametroInvalidoException: invariante violado."),
                                "404", Map.of("description", "ResourceNotFoundException: ID inexistente.")
                        )
                ),
                "delete", Map.of(
                        "tags", List.of("2. CRUD de Armas"),
                        "summary", "Elimina un arma de H2 por su ID",
                        "operationId", "eliminarArma",
                        "parameters", List.of(Map.of("name", "id", "in", "path", "required", true, "schema", Map.of("type", "integer"))),
                        "responses", Map.of(
                                "204", Map.of("description", "Arma eliminada exitosamente."),
                                "404", Map.of("description", "ResourceNotFoundException: ID inexistente.")
                        )
                )
        ));

        // 6. Interfaz UICapable
        paths.put("/armas/catalogo-ui", Map.of(
                "get", Map.of(
                        "tags", List.of("3. Interfaz UICapable y Polimorfismo"),
                        "summary", "Lista elementos persistidos de dos familias distintas (Arma y Jugador) mediante la interfaz UICapable",
                        "operationId", "obtenerCatalogoUI",
                        "responses", Map.of("200", Map.of("description", "Catálogo multimedia polimórfico."))
                )
        ));

        doc.put("paths", paths);

        // Schemas
        Map<String, Object> schemas = new LinkedHashMap<>();
        schemas.put("Arma", Map.of(
                "type", "object",
                "required", List.of("tipo", "nombre", "precio"),
                "properties", Map.of(
                        "tipo", Map.of("type", "string", "enum", List.of("Pistola", "Granada", "RifleAsalto", "Subfusil", "Francotirador", "Escopeta")),
                        "nombre", Map.of("type", "string", "example", "Glock-18"),
                        "precio", Map.of("type", "integer", "minimum", 0, "example", 200),
                        "equipo", Map.of("type", "string", "enum", List.of("TERRORISTA", "COUNTER_TERRORISTA", "AMBOS")),
                        "daño", Map.of("type", "number", "minimum", 0, "example", 28.0),
                        "precision", Map.of("type", "number", "minimum", 0, "maximum", 100, "example", 75.0),
                        "municionCargador", Map.of("type", "integer", "minimum", 0, "example", 20, "description", "No puede superar a capacidadCargador."),
                        "capacidadCargador", Map.of("type", "integer", "minimum", 1, "example", 20),
                        "municionReserva", Map.of("type", "integer", "minimum", 0, "example", 120),
                        "radioExplosion", Map.of("type", "number", "minimum", 0.1, "example", 8.0)
                )
        ));

        schemas.put("SimulacionExplosionRequest", Map.of(
                "type", "object",
                "properties", Map.of(
                        "armaId", Map.of("type", "integer", "example", 3, "description", "ID del arma persistida en H2 (ej. 3 = Granada HE, 1 = Glock-18)."),
                        "jugadorId", Map.of("type", "integer", "example", 1, "description", "ID del jugador objetivo en H2 (ej. 1 = s1mple CT)."),
                        "distanciaMetros", Map.of("type", "number", "minimum", 0, "example", 2.0, "description", "Distancia en metros desde el impacto (>= 0)."),
                        "disparos", Map.of("type", "integer", "minimum", 1, "example", 1, "description", "Cantidad de disparos si el arma es de fuego.")
                )
        ));

        schemas.put("ResultadoSimulacion", Map.of(
                "type", "object",
                "properties", Map.ofEntries(
                        Map.entry("armaId", Map.of("type", "integer")),
                        Map.entry("armaUtilizada", Map.of("type", "string")),
                        Map.entry("jugadorId", Map.of("type", "integer")),
                        Map.entry("jugadorObjetivo", Map.of("type", "string")),
                        Map.entry("persistidoEnH2", Map.of("type", "boolean")),
                        Map.entry("enRadioAlcance", Map.of("type", "boolean")),
                        Map.entry("danioFinalVida", Map.of("type", "number")),
                        Map.entry("vidaRestante", Map.of("type", "number")),
                        Map.entry("durabilidadBlindajeRestante", Map.of("type", "number")),
                        Map.entry("estadoJugador", Map.of("type", "string")),
                        Map.entry("detalleRegla", Map.of("type", "string"))
                )
        ));

        schemas.put("ErrorResponse", Map.of(
                "type", "object",
                "properties", Map.of(
                        "timestamp", Map.of("type", "string"),
                        "status", Map.of("type", "integer", "example", 400),
                        "tipoExcepcion", Map.of("type", "string", "enum", List.of("ParametroInvalidoException", "ValidationException", "MunicionInsuficienteException", "ResourceNotFoundException")),
                        "excepcionPadre", Map.of("type", "string", "example", "ArmaException"),
                        "error", Map.of("type", "string"),
                        "mensaje", Map.of("type", "string")
                )
        ));

        doc.put("components", Map.of("schemas", schemas));
        return ResponseEntity.ok(doc);
    }

    @GetMapping(value = "/swagger-ui.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> swaggerUi() {
        String html = """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>CS2 API — OpenAPI / Swagger UI</title>
                    <link rel="stylesheet" href="https://unpkg.com/swagger-ui-dist@5/swagger-ui.css" />
                </head>
                <body>
                    <div id="swagger-ui"></div>
                    <script src="https://unpkg.com/swagger-ui-dist@5/swagger-ui-bundle.js"></script>
                    <script>
                        window.onload = () => {
                            window.ui = SwaggerUIBundle({
                                url: '/v3/api-docs',
                                dom_id: '#swagger-ui'
                            });
                        };
                    </script>
                </body>
                </html>
                """;
        return ResponseEntity.ok(html);
    }
}
