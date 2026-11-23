# Especificaciones de Entrega — Challenge de APIs Cruzadas (POO-09 y Cierre POO-08)

**Materia:** Lenguaje de Programación 3 (CYT646) — Edición 2026  
**Ejercicio:** POO-09 (Challenge de APIs cruzadas, OpenAPI y cierre de observaciones de POO-08)  
**Estudiante (A1):** Matias Enciso  
**Compañeros consultados:** Diana Aquino (`A2`) y Fabricio Gonzalez (`A3`)  
**Dominio del Grupo:** Counter-Strike 2 (CS2)  
**Repositorio:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026](https://github.com/MatiasEnc/menciso-tp-lp3-2026)  
**Enlace exacto al commit de la solución:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/a62d842c676353fdb03da87b74ee82fe5b0d3e8e](https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/a62d842c676353fdb03da87b74ee82fe5b0d3e8e)  

---

## 1. Quiénes son A1, A2 y A3 (Mapa del Grupo Round Robin en CS2)

| Rol | Integrante | Repositorio | URL Base Local | URL OpenAPI 3 | Regla publicada y consultada en el escenario |
| :---: | :--- | :--- | :--- | :--- | :--- |
| **A1 (Propio)** | **Matias Enciso** | [MatiasEnc/menciso-tp-lp3-2026](https://github.com/MatiasEnc/menciso-tp-lp3-2026) | `http://localhost:8080` | `http://localhost:8080/v3/api-docs` | Orquesta el escenario **`POST /api/escenario/jugadas`** consultando a A2 y A3 y persiste la `JugadaEscenario` en H2 solo si ambos aceptan. Además publica `GET /api/simulacion/explosion` y `POST /api/armas/{id}/disparar` para que lo consulten sus compañeros. |
| **A2 (Compañera 1)** | **Diana Aquino** | [dianamariapaz2104/daquino-tp-lp3-2026](https://github.com/dianamariapaz2104/daquino-tp-lp3-2026) | `http://localhost:8081` | `http://localhost:8081/v3/api-docs` | **Regla consultada (`POST /api/armas/granadas/simular?equipoAtacante=...&equipoObjetivo=...&distancia=...`):** Valida la explosión de granada de apertura con radio de 5m y absorción Kevlar 35% (`200 OK`), rechazando distancias fuera de rango (`400`) o fuego amigo entre el mismo equipo (`422 FuegoAmigoException`). |
| **A3 (Compañero 2)** | **Fabricio Gonzalez** | [FabricioG29/fg-taller-git-2026](https://github.com/FabricioG29/fg-taller-git-2026) | `http://localhost:8082` | `http://localhost:8082/v3/api-docs` | **Regla consultada (`POST /api/reglas/municion` con `{"cargador":20,"disparos":3}`):** Valida que la munición del cargador alcance para la ráfaga de disparos (`200 OK`), o rechaza con `409 Conflict` (`SIN_MUNICION`) si las balas no alcanzan. |

---

## 2. El Escenario Propio en Una Frase

**«Asalto Táctico Coordinado CS2 (Granada de Apertura + Ráfaga de Remate)» (`POST /api/escenario/jugadas`):** Para ejecutar y guardar en el H2 de **A1 (Matias Enciso)** un asalto sobre un objetivo, primero la API de **A2 (Diana Aquino)** debe validar que la granada de apertura esté a una distancia legal y no sea fuego amigo (`200 OK`), y luego la API de **A3 (Fabricio Gonzalez)** debe validar que el cargador del arma alcance para la ráfaga de disparos de remate (`200 OK`); si cualquiera de las dos rechaza (`422`, `400` o `409`), la base H2 de A1 queda intacta (`"estadoH2Local": "SIN_CAMBIOS"`) y se informa exactamente de cuál compañero vino el rechazo.

---

## 3. Cierre de Observaciones de POO-08 (3 puntos)

Se cerraron **todas** las observaciones del documento de corrección de POO-08:
1. **Separación de 3 capas (Criterio 2):** `ArmaController` y `SimulacionExplosionController` ya no inyectan repositorios ni instancian objetos del dominio; delegan todo en `ArmaService` y `SimulacionExplosionService`.
2. **Dominio limpio y lógica/excepciones en `service` (Criterio 2):** Las entidades de `domain` (`Arma`, `ArmaDeFuego`, `Pistola`, `Granada`, `Jugador`, `Blindaje`, `JugadaEscenario`) solo guardan su estado encapsulado (`private`), mientras que todas las validaciones, reglas y `throw` viven en `service`. `GlobalExceptionHandler` fue movido a `rest.controller` y se eliminó `UI.java`.
3. **Invariante `municionCargador <= capacidadCargador` (Criterio 3):** `ArmaService.validarArma()` rechaza cualquier arma con `municionCargador > capacidadCargador` (por ejemplo `500` balas en un cargador de `20`) con `400 ParametroInvalidoException`, y `SimulacionExplosionService` valida siempre los objetos recibidos por ID o por JSON.
4. **Documentación y `GET /demo` (Criterios 6 y 8):** Las propiedades de H2 en `README.md` coinciden línea por línea con `application.properties`, `GET /api/simulacion/explosion/demo` es de solo lectura, y el enlace al commit está sincronizado en los tres documentos.

---

## 4. Cómo Probarlo Paso a Paso

### A. Arrancar los 3 servicios del grupo (en 3 terminales)
```bash
# Terminal 1 — A2 (Diana Aquino) en puerto 8081
git clone https://github.com/dianamariapaz2104/daquino-tp-lp3-2026.git
cd daquino-tp-lp3-2026
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081

# Terminal 2 — A3 (Fabricio Gonzalez) en puerto 8082
git clone https://github.com/FabricioG29/fg-taller-git-2026.git
cd fg-taller-git-2026
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8082

# Terminal 3 — A1 Propio (Matias Enciso) en puerto 8080
git clone https://github.com/MatiasEnc/menciso-tp-lp3-2026.git
cd menciso-tp-lp3-2026/cs2
./mvnw spring-boot:run
```

### B. Probar desde Insomnia (`insomnia/insomnia-cs2-poo09.json`)
1. Importar `insomnia/insomnia-cs2-poo09.json` en Insomnia.
2. En la carpeta **`1. POO-09 — OpenAPI y Escenario Cruzado`**:
   * **Pedido 01 (`GET /v3/api-docs`):** Verifica el documento OpenAPI 3.0.3 propio.
   * **Pedido 02 (`Camino Feliz - POST /api/escenario/jugadas`):** Envía `Counter-Terrorista` vs `Terrorista`, `distanciaExplosion: 2.0`, `cargadorDisponible: 20`, `disparosRafaga: 3`. Tanto A2 como A3 responden `200 OK`, A1 guarda la jugada en H2 y devuelve **`201 Created`**.
   * **Pedido 03 (`GET /api/escenario/jugadas`):** Confirma que la jugada quedó persistida en la tabla `jugadas_escenario` de H2.
   * **Pedido 04 (`Rechazo de A2 Diana Aquino - POST /api/escenario/jugadas`):** Envía `Terrorista` vs `Terrorista`. A2 rechaza por fuego amigo (`422`), A1 no guarda nada en H2 y responde `422` con `"rechazadoPor": "A2 (Diana Aquino)"` y `"estadoH2Local": "SIN_CAMBIOS"`.
   * **Pedido 05 (`Rechazo de A3 Fabricio Gonzalez - POST /api/escenario/jugadas`):** Envía `cargadorDisponible: 5` y `disparosRafaga: 25`. A2 acepta, pero A3 rechaza con `409 SIN_MUNICION`. A1 deja su H2 sin cambios y responde `409` con `"rechazadoPor": "A3 (Fabricio Gonzalez)"` y `"estadoH2Local": "SIN_CAMBIOS"`.

### C. Ejecutar las 22 pruebas automatizadas (incluyen servidores HTTP embebidos para A2 y A3)
```bash
cd cs2
./mvnw test
```
