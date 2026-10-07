# Bitácora de Desarrollo y Entrega — Taller de Git y Modelado OO

**Materia:** Lenguaje de Programación 3 (CYT646) — Edición 2026

**Proyecto:** `menciso-tp-lp3-2026`

**Estudiante:** Matias Enciso

**Dominio:** Counter-Strike 2 (CS2)

**Repositorio GitHub:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026](https://github.com/MatiasEnc/menciso-tp-lp3-2026?utm_source=gemini)

## 1. Historial Integrado de Sesiones y Commits

### Sesión 1 — 2026-09-16 · Inicio e Infraestructura del Proyecto

* **`b6a4665` (Initial commit):** Creación del repositorio público en GitHub con licencia Apache 2.0 (`LICENSE`) y `README.md` inicial con el encabezado de la materia y la definición del dominio Counter-Strike 2.

* **`b998d54` (clase 16/09):** Inicialización del proyecto Spring Boot `cs2` con Maven (`pom.xml`), wrapper ejecutable (`./mvnw`), Java 21, clase principal `Cs2Application` y tests de contexto.

### Sesión 2 — 2026-09-20 · Modelado Inicial de Entidades

* **`a0ff1f4` (Agregue entidades):** Incorporación de las primeras clases del dominio bajo el paquete `py.edu.uc.lp3.me.cs2`:

  * Clase base abstracta `Arma` y enum `Equipo` (`TERRORISTA`, `COUNTER_TERRORISTA`, `AMBOS`).

  * Subclase abstracta `ArmaDeFuego` y subclases concretas: `Pistola`, `Granada`, `RifleAsalto`, `Subfusil`, `Francotirador`, `Escopeta`.

  * Esqueleto inicial de `IndexController` para el endpoint raíz.

### Sesión 3 — 2026-09-24 · Refactor, Ocultamiento, Polimorfismo y REST

* **`37aa1c7`:** Corrección de empaquetado; se añadió la declaración formal `package py.edu.uc.lp3.me.cs2;` a todas las clases pendientes.

* **`8fa7aae`:** Actualización de `IndexController` (`GET /`), retornando un objeto JSON estructurado con los atributos `autor`, `dominio` y `estado: "API funcionando"`.

* **`57dc725` (Refactor integral del dominio y controllers):**

  * **Ocultamiento e Encapsulamiento (Parte D):** Atributos definidos como `private`. Se implementaron validaciones de estado (invariantes) en constructores y setters (`precio >= 0`, `balas >= 0`). Si se pasan valores negativos, se arroja `IllegalArgumentException`.

  * **Método Abstracto (Parte F):** Se declaró el método abstracto `public abstract String ejecutarAccion();` en la clase base `Arma`.

  * **Jerarquía y Polimorfismo:**

    * `ArmaDeFuego` hereda de `Arma` e implementa `ejecutarAccion()` con la lógica de disparo balístico y gestión de munición.

    * `Pistola` hereda de `ArmaDeFuego` y sobrescribe `ejecutarAccion()` agregando la lógica de uso de silenciador.

    * `Granada` pasa a heredar **directamente de `Arma`** e implementa `ejecutarAccion()` definiendo el lanzamiento y detonación táctica.

  * **`ArmaController` (Parte E y F):**

    * `GET /armas/crear`: Instanciación mediante parámetros de URL (`@RequestParam`). Retorna la representación JSON de la entidad instanciada o HTTP `400 Bad Request` si los datos violan las validaciones.

    * `GET /armas/comportamiento`: Colección de tipo heterogéneo `List<Arma>` que contiene instancias de `Pistola` y `Granada`. Invoca el método polimórfico `ejecutarAccion()` sin emplear `if`, `switch` ni evaluación explícita de tipos concretos.

### Sesión 4 — 2026-09-24 · Documentación del Modelo

* **`352ad76` (Parte F-bis):** Actualización del `README.md` del repositorio con el diagrama de clases en formato Mermaid (`classDiagram`), representando visualmente la jerarquía de `Arma`, `ArmaDeFuego`, `Pistola`, `Granada` y demás subclases de CS2.

## 2. Respuestas a los Criterios de Diseño OO

1. **¿El controller usa el tipo padre o pregunta el tipo concreto?**

   El controlador utiliza **exclusivamente el tipo padre `Arma`**. En el endpoint `/armas/comportamiento`, la lista se declara como `List<Arma>`. Al recorrerla, se invoca `arma.ejecutarAccion()`, confiando en el **despacho dinámico (polimorfismo en tiempo de ejecución)**. No se utiliza `instanceof`, casteo de tipos ni condicionales `if/else` para determinar la clase concreta.

2. **¿Se puede romper el invariante desde el controller?**

   **No.** Las clases del dominio tienen encapsulamiento estricto (`private`) y validan los parámetros de entrada en sus constructores y *setters*. Si desde el controller o mediante parámetros HTTP se envía una petición con datos fuera de rango (ej. `precio = -100`), la entidad arroja una excepción `IllegalArgumentException`, la cual es capturada para retornar un código de estado HTTP `400 Bad Request`.

3. **¿Por qué la clase base no puede implementar el método abstracto?**

   Porque `Arma` representa la abstracción conceptual del inventario (definida por costo, nombre y equipo asignado). Las mecánicas operativas de sus clases derivadas son completamente incompatibles: un arma de fuego dispara proyectiles mediante un cargador, mientras que una granada es un artefacto consumible de un solo uso que actúa por ignición y detonación. No existe un comportamiento por defecto común a nivel de implementación concreta para la base.

## 3. Verificación de Funcionamiento

* **Compilación del proyecto:**

  `./mvnw clean compile` -> **BUILD SUCCESS** (Java 21).

* **Ejecución del servicio:**

  `./mvnw spring-boot:run` -> Levanta correctamente en el puerto 8080.

* **Endpoints evaluados:**

  * `GET http://localhost:8080/` -> `{"autor": "Matias Enciso", "dominio": "CS2", "estado": "API funcionando"}`

  * `GET http://localhost:8080/armas/comportamiento`  -> Devuelve el arreglo JSON con el comportamiento polimórfico de `Pistola` y `Granada`.

  * `GET http://localhost:8080/armas/crear?nombre=Glock-18&precio=200&dano=30&balas=20` ->  Retorna el objeto `Pistola` construido correctamente.

  * `GET http://localhost:8080/armas/crear?nombre=Glock-18&precio=-500` -> Retorna respuesta HTTP `400 Bad Request` protegiendo la integridad del dominio.