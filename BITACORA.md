# Bitácora de uso de IA

**Asistente usado:** Antigravity (Google DeepMind)  
**Modelo exacto:** Gemini 3.8 / Argon 4  
**Fecha:** 23 de Noviembre de 2026  

---

## Resumen de cómo usé la IA (Prompts)

Para el desarrollo del trabajo práctico me apoyé en el asistente de IA para ir armando y ajustando cada etapa del proyecto sobre el dominio de Counter-Strike 2. Estos fueron los pasos y pedidos principales que fui haciendo:

### Etapa POO-06 y POO-07 (Dominio, Constructores, Polimorfismo e Interfaz)
1. Al principio le pedí ayuda para armar la estructura en Mermaid con el diagrama de clases de Counter-Strike 2 y pasarlo al `README.md`.
2. Le pasé la estructura de paquetes del template de la cátedra (`py.edu.uc.lp3.me.cs2`) para organizar las clases separando dominio, excepciones y controladores REST.
3. Le pedí armar la clase base abstracta `Arma` con atributos privados encapsulados, métodos abstractos (`ejecutarAccion`) y constructores sobrecargados, junto con las clases hijas (`Pistola`, `Granada`, `ArmaDeFuego`, `RifleAsalto`, `Subfusil`, `Francotirador`, `Escopeta`).
4. Luego incorporamos la interfaz del dominio `UICapable` (`getImagen` y `getSonido`) para que las clases del modelo expongan sus recursos multimedia en un catálogo REST.

### Etapa POO-08 (CRUD REST con H2, Reglas de Juego, Simulación Persistida, Excepciones e Insomnia)
5. Le pedí agregar las dependencias de `spring-boot-starter-data-jpa` y `H2` en el `pom.xml`, configurar el archivo `application.properties` con persistencia en archivo (`./data/armas`) y habilitar la consola H2 en `/h2-console`.
6. Le pedí mapear la jerarquía de `Arma` y sus subclases (`Pistola`, `Granada`, etc.) como entidades JPA usando `@Inheritance(strategy = InheritanceType.SINGLE_TABLE)` con constructores vacíos `protected`, además de configurar `@JsonTypeInfo` y `@JsonSubTypes` para que Jackson discrimine el subtipo en los JSON.
7. También le pedí persistir la clase `Jugador` con su `Blindaje` (`@Embeddable`) y hacer que `Jugador` implemente `UICapable` para que el endpoint `/armas/catalogo-ui` use la interfaz sobre dos familias distintas.
8. Le pedí implementar el CRUD REST completo en `/api/armas` conectado a `ArmaService` y `ArmaRepository`, la simulación persistida en `/api/simulacion/explosion`, la jerarquía de excepciones (`ArmaException`, `ParametroInvalidoException`, `MunicionInsuficienteException`, `ResourceNotFoundException`) y la colección de Insomnia.

### Etapa POO-09 (Cierre de Observaciones de POO-08, OpenAPI y Challenge de APIs Cruzadas A1 -> A2 y A3)
9. Le pasé el documento de corrección de POO-08 del profesor y le pedí cerrar todas las observaciones: quitar los repositorios de `ArmaController`, dejar las entidades de `domain` únicamente con su estado encapsulado (`private`), mover todas las validaciones, las dos reglas de negocio y el lanzamiento de excepciones a la capa `service` (`ArmaService` y `SimulacionExplosionService`), validar estrictamente que `municionCargador <= capacidadCargador`, mover `GlobalExceptionHandler` a `rest.controller`, eliminar la interfaz repetida `UI.java` y alinear las propiedades de H2 en el `README.md`.
10. Le pedí publicar el documento OpenAPI 3.0.3 de mi servicio en `GET /v3/api-docs` y el visor Swagger UI en `/swagger-ui.html` y `/swagger-ui/index.html` para que mis compañeros de grupo puedan importar y consultar mis reglas.
11. Le pasé los datos de mis dos compañeros de Counter-Strike 2 —**A2: Diana Aquino** (`POST /api/armas/granadas/simular`) y **A3: Fabricio Gonzalez** (`POST /api/reglas/municion`)— y le pedí implementar `CompaneroApiClient` (con `HttpClient` y URLs configurables en `application.properties`), la entidad `JugadaEscenario` y el endpoint de escenario cruzado `POST /api/escenario/jugadas` que consulta a A2 y A3, persiste en H2 solo si ambos aceptan, y si alguno rechaza deja la base H2 sin cambios indicando de quién fue el rechazo (`RechazoCompaneroException`).
12. Por último, le pedí crear las pruebas automatizadas con servidores HTTP embebidos (`EscenarioCruzadoTests`), actualizar la colección de Insomnia (`insomnia/insomnia-cs2-poo09.json`) con el camino feliz y un rechazo por cada compañero, y documentar el mapa A1/A2/A3 y los diagramas de secuencia Mermaid en `README.md` y `ESPECIFICACIONES.md`.

---

**Repositorio:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026](https://github.com/MatiasEnc/menciso-tp-lp3-2026)  
**Commit de la solución:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/a62d842c676353fdb03da87b74ee82fe5b0d3e8e](https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/a62d842c676353fdb03da87b74ee82fe5b0d3e8e)
