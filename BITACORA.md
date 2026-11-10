# Bitácora de uso de IA

**Asistente usado:** Antigravity (Google DeepMind)  
**Modelo exacto:** Gemini 3.8 / Argon 4  
**Fecha:** 28 de Octubre de 2026  

---

## Resumen de cómo usé la IA (Prompts)

Para el desarrollo del trabajo práctico me apoyé en el asistente de IA para ir armando y ajustando cada etapa del proyecto sobre el dominio de Counter-Strike 2. Estos fueron los pasos y pedidos principales que fui haciendo:

### Etapa POO-06 y POO-07 (Dominio, Constructores, Polimorfismo e Interfaz)
1. Al principio le pedí ayuda para armar la estructura en Mermaid con el diagrama de clases de Counter-Strike 2 y pasarlo al `README.md`.
2. Le pasé la estructura de paquetes del template de la cátedra (`py.edu.uc.lp3.me.cs2`) para organizar las clases separando dominio, excepciones y controladores REST.
3. Le pedí armar la clase base abstracta `Arma` con atributos encapsulados, métodos abstractos (`ejecutarAccion`) y constructores sobrecargados, junto con las clases hijas (`Pistola`, `Granada`, `ArmaDeFuego`, `RifleAsalto`, `Subfusil`, `Francotirador`, `Escopeta`).
4. Luego incorporamos la interfaz del dominio `UICapable` (`getImagen` y `getSonido`) para que las clases del modelo expongan sus recursos multimedia en un catálogo REST.

### Etapa POO-08 (CRUD REST con H2, Reglas de Juego, Simulación Persistida, Excepciones e Insomnia)
5. Le pedí agregar las dependencias de `spring-boot-starter-data-jpa` y `H2` en el `pom.xml`, configurar el archivo `application.properties` con persistencia en archivo (`./data/armas`) y habilitar la consola H2 en `/h2-console`.
6. Le pedí mapear la jerarquía de `Arma` y sus subclases (`Pistola`, `Granada`, etc.) como entidades JPA usando `@Inheritance(strategy = InheritanceType.SINGLE_TABLE)` con constructores vacíos `protected` para cuidar el ocultamiento, además de configurar `@JsonTypeInfo` y `@JsonSubTypes` para que Jackson discrimine el subtipo en los JSON de entrada y salida.
7. También le pedí persistir la clase `Jugador` con su `Blindaje` (`@Embeddable`) y hacer que `Jugador` implemente `UICapable` para que el endpoint `/armas/catalogo-ui` use la interfaz sobre dos familias distintas.
8. Le pedí implementar el CRUD REST completo en `/api/armas` (`POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`) conectado a `ArmaRepository` y `ArmaService`, validando que no se puedan guardar precios negativos ni nombres vacíos.
9. Para la simulación del juego, le pedí conectar tres clases del modelo (`Granada`/`Pistola`, `Jugador` y `Blindaje`) mediante dos reglas de negocio en métodos del dominio (consumo de munición/detonación con atenuación por distancia y fuego amigo, y mitigación de daño por el chaleco Kevlar) y persistir el estado resultante en H2 a través de `POST /api/simulacion/explosion`.
10. Le pedí armar la jerarquía de excepciones del dominio con `ArmaException` como clase padre y las clases hijas `ParametroInvalidoException` y `MunicionInsuficienteException` (además de `ResourceNotFoundException`), agregando un `@ExceptionHandler` separado por cada tipo en `GlobalExceptionHandler` para devolver códigos HTTP y respuestas JSON distinguibles.
11. Por último, le pedí completar la colección de Insomnia en `insomnia/insomnia-cs2-poo08.json` con la variable `base_url`, los 5 pedidos del CRUD, el `POST` ilegal, el `id` inexistente, el endpoint de la interfaz, la simulación persistida y los pedidos para cada excepción hija, además de actualizar el `README.md` y `ESPECIFICACIONES.md`.

---

**Repositorio:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026](https://github.com/MatiasEnc/menciso-tp-lp3-2026)  
**Commit de la solución:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/cecf2ac9b1c7c197fe160e626e71e0a1dd52ccf0](https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/cecf2ac9b1c7c197fe160e626e71e0a1dd52ccf0)
