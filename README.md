# menciso-tp-lp3-2026

**Asignatura:** Lenguaje de Programación 3 (CYT646) — Edición 2026  
**Ejercicio:** POO-09 (Challenge de APIs cruzadas, OpenAPI y cierre de observaciones de POO-08)  
**Dominio:** Counter-Strike 2 (CS2)  
**Autor:** Matias Enciso  
**Licencia:** [Apache License 2.0](LICENSE)  

---

## 0. Cierre de Observaciones de POO-08 (3 puntos) y OpenAPI Propio

En cumplimiento de la tabla de los 3 puntos de POO-09, a continuación se detalla cada observación recibida en la corrección de POO-08 y cómo quedó cerrada en el código:

| Criterio observado en POO-08 | Observación de la cátedra | Cómo se cerró en esta entrega |
| :--- | :--- | :--- |
| **Criterio 2: Organización en paquetes (Separación de 3 capas)** | `ArmaController` inyectaba `ArmaRepository` y `JugadorRepository` directamente, y el `GET` de `SimulacionExplosionController` instanciaba `Granada`, `Blindaje` y `Jugador`. | Se eliminaron los repositorios de `ArmaController` (ahora delega todo en `ArmaService`, incluyendo `obtenerCatalogoUI()` y `obtenerComportamientoPolimorfico()`), y `SimulacionExplosionController` delega la creación y simulación de lectura en `SimulacionExplosionService`. |
| **Criterio 2: Lógica y excepciones en `service` vs `domain`** | Las entidades de `domain` validaban, aplicaban las reglas y lanzaban excepciones en lugar de guardar solo el estado encapsulado. | Se limpiaron `Arma`, `ArmaDeFuego`, `Pistola`, `Granada`, `Jugador` y `Blindaje` en `domain` para que solo guarden su estado encapsulado (`private`), y se movieron todas las validaciones, las dos reglas de negocio y los `throw` a `ArmaService` y `SimulacionExplosionService`. Además, `GlobalExceptionHandler` se movió a `rest.controller`, y se eliminó la interfaz repetida `UI.java`. |
| **Criterio 3: Ocultamiento e invariantes (`municionCargador <= capacidadCargador`)** | Un `POST` con `municionCargador: 500` y `capacidadCargador: 20` se guardaba, y la simulación con granada en el JSON no pasaba por `validarArma`. | Se quitó el auto-incremento de `capacidadCargador` del setter y se agregó en `ArmaService.validarArma()` el control estricto `municionCargador <= capacidadCargador` (además de validar daño, precisión, radio y tiempo). En `SimulacionExplosionService` ahora se validan siempre el arma, el jugador y el blindaje tanto por ID como por JSON. |
| **Criterio 6 y 8: `GET /demo`, propiedades de H2 en README y enlace en Especificaciones** | `GET /demo` modificaba H2, el README listaba `spring.jpa.database-platform` que no estaba en `application.properties`, y el enlace en `ESPECIFICACIONES.md` tenía un hash anterior. | `GET /api/simulacion/explosion/demo` pasó a ser de solo lectura (`@Transactional(readOnly = true)`), la lista de propiedades del README ahora coincide línea por línea con `application.properties`, se unificó la explicación de `PUT` y se sincronizó el hash del commit en `ESPECIFICACIONES.md`. |

### Documento OpenAPI 3.0.3 Propio
* **JSON OpenAPI (para importar directo en Insomnia o consultar desde clientes HTTP):**  
  `GET http://localhost:8080/v3/api-docs`
* **Visor interactivo Swagger UI en navegador:**  
  `GET http://localhost:8080/swagger-ui.html`
* **Operaciones documentadas para el grupo:**
  1. `POST /api/simulacion/explosion` y `GET /api/simulacion/explosion?distancia=2.5&conBlindaje=true&fuegoAmigo=false` (Simulación que combina Regla 1 y Regla 2; devuelve `200`, `400 ParametroInvalidoException`, `404 ResourceNotFoundException` o `409 MunicionInsuficienteException`).
  2. `POST /api/armas/{id}/disparar?balas=1` (Regla 1: consumo de munición/carga en H2; devuelve `200`, `400` o `409`).
  3. `POST /api/armas/{id}/recargar` (Regla de recarga desde reserva hasta `capacidadCargador`; devuelve `200` o `409`).
  4. `GET /api/armas`, `POST /api/armas`, `GET /api/armas/{id}`, `PUT /api/armas/{id}`, `DELETE /api/armas/{id}` (CRUD completo con validación de `municionCargador <= capacidadCargador`).

---

## 1. Cómo Arrancar el Servicio y Propiedades de H2

### Arranque del proyecto
Desde la carpeta `cs2` del repositorio:

```bash
cd cs2
./mvnw spring-boot:run
```

El servicio levanta en `http://localhost:8080` y precarga automáticamente un arsenal inicial de armas (`Pistola`, `Granada`, `RifleAsalto`) y dos jugadores (`s1mple (CT)` y `Phoenix (T)`) con su blindaje en H2 si la base está vacía.

### Propiedades de H2 explicadas (`src/main/resources/application.properties`)
* `spring.application.name=menciso-tp-lp3-2026`: Define el nombre identificador de la aplicación dentro de Spring Boot.
* `spring.datasource.url=jdbc:h2:file:./data/armas;AUTO_SERVER=TRUE`: Configura H2 para guardar los datos en disco dentro de `./data/armas` (para que persistan al reiniciar) y permite conexiones concurrentes.
* `spring.datasource.driverClassName=org.h2.Driver`: Especifica el driver JDBC oficial del motor H2.
* `spring.datasource.username=sa`: Define el usuario administrador por defecto de la base de datos local.
* `spring.datasource.password=`: Deja la contraseña vacía para el entorno de desarrollo local.
* `spring.jpa.hibernate.ddl-auto=update`: Hace que Hibernate cree o actualice automáticamente las tablas (`armas` y `jugadores`) a partir de las clases `@Entity` sin borrar los datos existentes.
* `spring.jpa.open-in-view=false`: Desactiva el patrón Open Session in View para cerrar las sesiones de base de datos apenas termina la transacción de la capa `service`.
* `spring.jpa.show-sql=true`: Muestra en la consola las consultas SQL que Hibernate ejecuta contra H2.
* `spring.jpa.properties.hibernate.format_sql=true`: Formatea e indenta las sentencias SQL en la consola para que sean legibles.
* `spring.h2.console.enabled=true`: Habilita la consola visual web de H2 para inspeccionar las tablas desde el navegador.
* `spring.h2.console.path=/h2-console`: Define la ruta URL (`http://localhost:8080/h2-console`) donde se accede a la consola H2.

---

## 2. Diagrama de Clases Actualizado (Mermaid)

El diagrama muestra la arquitectura estricta de 3 capas (`rest.controller` -> `service` -> `repository` / `domain`):
* **Herencia (`<|--`):** La familia principal `Arma` -> `ArmaDeFuego` / `Granada` -> `Pistola`, y la jerarquía de excepciones `ArmaException` -> `ParametroInvalidoException` / `MunicionInsuficienteException` / `ResourceNotFoundException`.
* **Implementación de Interfaz (`<|..`):** `UICapable` implementada por dos familias distintas (`Arma` y `Jugador`).
* **Separación de capas (`-->`):** Los controladores hablan únicamente con la capa `service` (`ArmaService` y `SimulacionExplosionService`), la capa `service` concentra las validaciones, las reglas de negocio y el uso de `ArmaRepository` y `JugadorRepository`, y el paquete `domain` guarda únicamente el estado encapsulado.
* **Jerarquía de Excepciones (`..>`):** La capa `service` lanza las excepciones cuando se incumple un invariante o regla de juego, y `GlobalExceptionHandler` (en `rest.controller`) las traduce a respuestas HTTP por tipo.

```mermaid
classDiagram
    class UICapable {
        <<interface>>
        +getImagen()* String
        +setImagen(imagen)* void
        +getSonido()* String
        +setSonido(sonido)* void
    }

    class Arma {
        <<abstract>>
        -Long id
        -String nombre
        -int precio
        -Equipo equipo
        -String imagen
        -String sonido
        #Arma()
        +Arma(nombre, precio, equipo, imagen, sonido)
        +ejecutarAccion()* String
        +ejecutarAccion(repeticiones)* String
    }

    class ArmaDeFuego {
        <<abstract>>
        -double daño
        -double precision
        -int municionCargador
        -int municionReserva
        -int capacidadCargador
        #ArmaDeFuego()
        +ejecutarAccion() String
        +ejecutarAccion(repeticiones) String
    }

    class Pistola {
        -boolean esSilenciada
        #Pistola()
        +Pistola(nombre, precio, daño)
        +alternarSilenciador() void
        +ejecutarAccion() String
    }

    class Granada {
        -double daño
        -double radioExplosion
        -double tiempoActivacion
        -boolean esLetal
        -String efecto
        -boolean lanzada
        #Granada()
        +Granada(nombre, precio, radioExplosion)
        +ejecutarAccion() String
    }

    class Jugador {
        -Long id
        -String nombre
        -double vida
        -Equipo equipo
        -Blindaje blindaje
        -boolean vivo
        #Jugador()
    }

    class Blindaje {
        <<embeddable>>
        -String tipo
        -double durabilidad
        -double porcentajeAbsorcion
    }

    class ArmaRepository {
        <<interface>>
        +existsByNombreIgnoreCase(nombre)* boolean
        +findByEquipo(equipo)* List~Arma~
    }

    class JugadorRepository {
        <<interface>>
    }

    class ArmaService {
        +listarTodas() List~Arma~
        +obtenerPorId(id) Arma
        +crear(arma) Arma
        +actualizar(id, arma) Arma
        +eliminar(id) void
        +dispararArma(id, cantidad) Arma
        +recargarArma(id) Arma
        +obtenerCatalogoUI() List
        +validarArma(arma, esCreacion) void
    }

    class SimulacionExplosionService {
        +simularImpactoPersistido(request) ResultadoSimulacion
        +simularExplosion(granada, jugador, blindaje, distancia) ResultadoSimulacion
        +calcularDanioExplosion(granada, distancia, equipo) double
        +aplicarImpactoAJugador(jugador, danio) double
        +validarJugador(jugador) void
        +validarBlindaje(blindaje) void
    }

    class ArmaRestController {
        +listarTodas() ResponseEntity
        +obtenerPorId(id) ResponseEntity
        +crear(arma) ResponseEntity
        +actualizar(id, arma) ResponseEntity
        +eliminar(id) ResponseEntity
        +disparar(id, balas) ResponseEntity
        +recargar(id) ResponseEntity
    }

    class ArmaController {
        +crearArma(...) ResponseEntity
        +crearArmaSimple(...) ResponseEntity
        +obtenerComportamiento() ResponseEntity
        +obtenerComportamientoSobrecargado(repeticiones) ResponseEntity
        +catalogoUI() ResponseEntity
    }

    class SimulacionExplosionController {
        +simular(request) ResponseEntity
        +listarJugadores() ResponseEntity
        +obtenerJugador(id) ResponseEntity
        +simularParametros(...) ResponseEntity
        +demo() ResponseEntity
    }

    class ArmaException {
        +ArmaException(message)
    }

    class ParametroInvalidoException {
        +ParametroInvalidoException(message)
    }

    class MunicionInsuficienteException {
        +MunicionInsuficienteException(message)
    }

    class ResourceNotFoundException {
        +ResourceNotFoundException(message)
    }

    class GlobalExceptionHandler {
        +handleParametroInvalidoException(ex) ResponseEntity
        +handleMunicionInsuficienteException(ex) ResponseEntity
        +handleResourceNotFoundException(ex) ResponseEntity
        +handleArmaException(ex) ResponseEntity
    }

    UICapable <|.. Arma : implements
    UICapable <|.. Jugador : implements
    Arma <|-- ArmaDeFuego : extends
    Arma <|-- Granada : extends
    ArmaDeFuego <|-- Pistola : extends
    Jugador *-- Blindaje : contiene
    ArmaRestController --> ArmaService : usa
    ArmaController --> ArmaService : usa
    SimulacionExplosionController --> SimulacionExplosionService : usa
    SimulacionExplosionService --> ArmaService : usa
    ArmaService --> ArmaRepository : usa
    ArmaService --> JugadorRepository : usa
    SimulacionExplosionService --> ArmaRepository : usa
    SimulacionExplosionService --> JugadorRepository : usa
    ArmaException <|-- ParametroInvalidoException : extends
    ArmaException <|-- MunicionInsuficienteException : extends
    ArmaException <|-- ResourceNotFoundException : extends
    ArmaService ..> ParametroInvalidoException : lanza
    ArmaService ..> MunicionInsuficienteException : lanza
    SimulacionExplosionService ..> ParametroInvalidoException : lanza
    GlobalExceptionHandler ..> ArmaException : maneja por tipo
```

---

## 3. Las Dos Reglas de Negocio y la Simulación Persistida

En la simulación participan **tres clases del modelo** (`Granada`/`Pistola` de la familia `Arma`, `Jugador` y `Blindaje`), cuyas reglas y validaciones se ejecutan en la capa `service` (`ArmaService` y `SimulacionExplosionService`):

1. **Regla 1 (Consumo de carga/munición y cálculo de daño según distancia y fuego amigo):** Al disparar un arma de fuego o detonar una granada, `ArmaService` y `SimulacionExplosionService` descuentan munición del cargador (o consumen la carga única de la granada calculando la atenuación por distancia al epicentro y reduciendo el daño al 35% si hay fuego amigo), lanzando `MunicionInsuficienteException` si el arma ya no tiene balas o si la granada ya fue detonada.
2. **Regla 2 (Mitigación e impacto por Kevlar sobre `Blindaje` y `Jugador`):** Antes de descontar puntos de salud al `Jugador`, `SimulacionExplosionService.aplicarImpactoAJugador()` hace que su `Blindaje` absorba un porcentaje del daño entrante desgastando su propia durabilidad, y solo el daño residual impacta en la vida del jugador (cambiando su estado a eliminado si llega a `0 HP`).

El nuevo estado de `Arma` y `Jugador` **se guarda en H2** tras validar todos los invariantes.

---

## 4. Respuestas a «Qué hay que pensar»

* **Recurso y verbo:** El sustantivo principal de nuestra API es `/api/armas` (y `/api/simulacion/explosion` para la simulación). La URL nombra al recurso en plural y el verbo HTTP (`GET`, `POST`, `PUT`, `DELETE`) define la acción que se realiza sin poner verbos en la ruta REST del CRUD.
* **Identidad:** La clave primaria es el campo `id` (`Long`), anotado con `@Id` y `@GeneratedValue(strategy = GenerationType.IDENTITY)`. La asigna automáticamente la base de datos H2 al insertar la fila, por lo que el JSON del `POST` omite el `id` (y si alguien envía un `id` en el `POST`, `ArmaService` lo rechaza con error 400).
* **Herencia y tablas:** Elegimos `InheritanceType.SINGLE_TABLE` con la columna discriminadora `tipo_arma` en la tabla `armas` porque en CS2 todas las armas comparten la mayoría de los atributos base y consultar todo el arsenal en una sola tabla evita múltiples `JOIN` innecesarios. En el JSON el subtipo se distingue mediante `@JsonTypeInfo` y `@JsonSubTypes` con la propiedad `"tipo"` (`"Pistola"`, `"Granada"`, `"RifleAsalto"`, etc.).
* **Interfaz y repositorio:** `ArmaRepository` y `JugadorRepository` son interfaces que extienden `JpaRepository` y son consumidas exclusivamente por la capa `service` (`ArmaService` y `SimulacionExplosionService`). Ningún controller accede directamente a un repositorio, respetando la arquitectura de 3 capas.
* **Protocolo del dominio:** Nuestra interfaz de dominio es `UICapable` (`getImagen()`, `getSonido()`). La usa el endpoint `GET /armas/catalogo-ui` a través de `ArmaService.obtenerCatalogoUI()`, el cual reúne en una misma lista `List<UICapable>` instancias persistidas de dos familias distintas (`Arma` y `Jugador`) y las recorre polimórficamente.
* **Ocultamiento:** Las entidades del paquete `domain` mantienen todos sus atributos como `private` y su constructor vacío requerido por JPA como `protected`. La capa `service` (`ArmaService.validarArma`, `SimulacionExplosionService.validarJugador` y `validarBlindaje`) valida todos los invariantes numéricos de CS2 en cada entrada (crear, actualizar y simular), incluyendo que `municionCargador <= capacidadCargador`, que `capacidadCargador > 0` y que ningún precio, daño o radio sea negativo. El pedido `6. POST ilegal - Precio negativo (400)` en Insomnia prueba que un `POST` inválido lanza `ParametroInvalidoException` y deja la tabla `armas` intacta.
* **PUT y PATCH:** Implementamos `PUT /api/armas/{id}`, que reemplaza los campos del recurso validando primero todos sus invariantes en `ArmaService`; si los campos opcionales `imagen` o `sonido` se omiten o vienen vacíos en el JSON, `ArmaService.validarArma` les asigna los valores por defecto (`default_weapon.png` y `default_sound.wav`).
* **DELETE:** En nuestro modelo, `DELETE /api/armas/{id}` delega en `ArmaService.eliminar(id)`, que verifica con `obtenerPorId(id)` que el arma existe y elimina físicamente el registro de H2, respondiendo `204 No Content` (o `404 Not Found` si no existe).
* **Dos reglas:** Los dos hechos que cambian el estado de más de un objeto a la vez son: (1) el disparo/detonación del arma que gasta munición o marca la granada como detonada en H2, y (2) la absorción del impacto por el blindaje Kevlar que reduce la durabilidad del chaleco y descuenta la vida del jugador. El recurso HTTP es `POST /api/simulacion/explosion`, y al hacer un `GET /api/simulacion/explosion/jugadores/1` o `GET /api/armas/3` posterior se comprueba el resultado persistido en H2.
* **Excepciones:** La clase base es `ArmaException` (`RuntimeException`, no comprobada). Sus hijas son `ParametroInvalidoException` (cuando la capa `service` detecta la violación de un invariante como precio negativo o `municionCargador > capacidadCargador`, responde `400 Bad Request`), `MunicionInsuficienteException` (cuando se intenta disparar sin balas, recargar sin reserva o detonar dos veces una granada, responde `409 Conflict`) y `ResourceNotFoundException` (cuando un `id` no existe en H2, responde `404 Not Found`). `GlobalExceptionHandler` (ubicado en `rest.controller`) traduce cada tipo a un JSON con `timestamp`, `status`, `error`, `tipoExcepcion` y `mensaje`.
* **Preguntas de anclaje:**
  1. *¿Si mañana cambiamos H2 por otra base, qué archivos tocan y cuáles se quedan quietos?* Solo se tocan `pom.xml` (para agregar el driver de la nueva base, por ejemplo PostgreSQL) y `application.properties` (URL y credenciales). Todas las clases de `domain`, `repository`, `service` y `rest.controller` se quedan quietas.
  2. *¿Quién lanza la excepción?* La lanza la capa de negocio (`ArmaService` y `SimulacionExplosionService`) al validar invariantes o ejecutar las reglas del juego; las entidades de `domain` solo guardan su estado encapsulado y `GlobalExceptionHandler` en `rest.controller` traduce la excepción a HTTP/JSON.

---

## 5. Cómo Importar y Recorrer la Colección de Insomnia

La colección exportada se encuentra versionada en:  
`insomnia/insomnia-cs2-poo08.json`

### Pasos para importar
1. Abrir **Insomnia**.
2. Ir a **Import** -> **From File** y seleccionar `insomnia/insomnia-cs2-poo08.json`.
3. Verificar que el entorno **Base Environment** esté activo (define `base_url = http://localhost:8080`).

### Orden recomendado de ejecución (Carpeta `me-taller-lp3` -> `cs2`)
1. **`1. Crear armas (POST)`** (`POST /api/armas`): Crea la pistola `Five-SeveN` en H2 y devuelve `201 Created` con su `id`.
2. **`2. Obtener todas las armas (GET)`** (`GET /api/armas`): Lista todas las armas guardadas en H2 incluyendo la recién creada.
3. **`3. Obtener arma por id (GET)`** (`GET /api/armas/1`): Devuelve el detalle de la `Glock-18` con `id = 1`.
4. **`4. Editar armas (PUT)`** (`PUT /api/armas/1`): Actualiza el nombre y precio del arma `1` a `Glock-18 Water Elemental`. Un `GET` posterior confirma que el cambio quedó persistido.
5. **`5. Eliminar armas (DELETE)`** (`DELETE /api/armas/6`): Borra el arma con `id = 6` y devuelve `204 No Content`.
6. **`6. POST ilegal - Precio negativo (400)`** (`POST /api/armas`): Envía `"precio": -500`, recibe `400 Bad Request` y demuestra que no se guardó basura en H2.
7. **`7. Obtener id inexistente (404)`** (`GET /api/armas/999`): Prueba la búsqueda de un ID inexistente y recibe `404 Not Found`.
8. **`8. Endpoint Interfaz UICapable (GET)`** (`GET /armas/catalogo-ui`): Devuelve tanto las instancias de `Arma` como las de `Jugador` persistidas en H2 usando la interfaz `UICapable`.
9. **`9. Simulación Persistida en H2 (POST)`** (`POST /api/simulacion/explosion`): Ejecuta la simulación con la `Granada HE` (`armaId: 3`) sobre el jugador `s1mple (CT)` (`jugadorId: 1`) a 2 metros, aplicando las dos reglas de negocio y guardando el resultado en H2.
10. **`10. Verificar Jugador en H2 tras Simulación (GET)`** (`GET /api/simulacion/explosion/jugadores/1`): Muestra que la vida de `s1mple (CT)` bajó de `100.0` a `63.25` y la durabilidad de su blindaje Kevlar bajó en la base de datos.
11. **`11. Excepción Hija 1 - ParametroInvalidoException (400)`** (`POST /api/armas/1/disparar?balas=-3`): Provoca la primera excepción hija enviando una cantidad inválida de balas (`400 Bad Request`).
12. **`12. Excepción Hija 2 - MunicionInsuficienteException (409)`** (`POST /api/armas/3/recargar`): Provoca la segunda excepción hija al intentar recargar una granada o usar un arma sin munición disponible (`409 Conflict`).

---

## 6. Enlace al Commit de la Solución
* **Repositorio:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026](https://github.com/MatiasEnc/menciso-tp-lp3-2026)
* **Commit de la solución:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/5862738538f692bf800b326f28d38c494b106a9c](https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/5862738538f692bf800b326f28d38c494b106a9c)
