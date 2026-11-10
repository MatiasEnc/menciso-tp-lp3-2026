# Especificaciones de Entrega — Taller de POO (POO-08)

**Materia:** Lenguaje de Programación 3 (CYT646) — Edición 2026  
**Ejercicio:** POO-08 (CRUD REST, H2, reglas de juego, simulación persistida, excepciones e Insomnia)  
**Rúbrica:** `RUBRICA-ejercicios-lp3-2026`  
**Estudiante:** Matias Enciso  
**Dominio:** Counter-Strike 2 (CS2)  
**Repositorio:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026](https://github.com/MatiasEnc/menciso-tp-lp3-2026)  
**Enlace exacto al commit de la solución:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/cecf2ac9b1c7c197fe160e626e71e0a1dd52ccf0](https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/cecf2ac9b1c7c197fe160e626e71e0a1dd52ccf0)  

---

## 1. Objetivo del Proyecto

Publicar un servicio HTTP con Spring Boot y Java 21 sobre el dominio **Counter-Strike 2 (CS2)** que persiste su estado en una base de datos relacional **H2**, cumpliendo con:
1. **Persistencia JPA en H2** de la familia principal de armas (`Arma` como clase padre abstracta, y sus hijas `Pistola`, `Granada`, `RifleAsalto`, `Subfusil`, `Francotirador`, `Escopeta`) y de la familia `Jugador` junto con su `Blindaje` embebido.
2. **CRUD REST completo** sobre `/api/armas` con discriminación polimórfica en JSON (`"tipo": "Pistola"`, `"tipo": "Granada"`, etc.).
3. **Protocolo del dominio (`UICapable`)** implementado por dos familias distintas (`Arma` y `Jugador`) y expuesto en un endpoint de lectura polimórfico.
4. **Dos reglas de negocio en métodos del dominio** que alimentan una **simulación persistida en H2** donde participan tres clases del modelo (`Granada`/`Pistola`, `Jugador` y `Blindaje`).
5. **Jerarquía de excepciones del dominio** (`ArmaException` como base y dos hijas: `ParametroInvalidoException` y `MunicionInsuficienteException`, más `ResourceNotFoundException`) atendidas por un `@ExceptionHandler` específico por tipo en `GlobalExceptionHandler`.
6. **Colección de Insomnia versionada** en `insomnia/insomnia-cs2-poo08.json` lista para importar y ejecutar todas las pruebas.

---

## 2. Reglas de Negocio e Invariantes del Dominio

### A. Invariantes Protegidos (Ocultamiento frente a JSON y JPA)
* **Constructores vacíos protegidos (`protected`):** Las clases `@Entity` declaran su constructor sin argumentos como `protected` para que JPA e Hibernate puedan instanciarlas al leer filas, sin permitir que capas externas creen objetos vacíos o inválidos.
* **Validaciones de estado en el dominio:**
  * El nombre del arma o jugador no puede ser nulo ni vacío.
  * El precio del arma no puede ser negativo (`precio >= 0`).
  * El daño, radio de explosión, precisión (`0.0` a `100.0`), munición y vida (`0.0` a `100.0`) no admiten valores negativos.
  * Si un JSON en `POST` o `PUT` rompe cualquiera de estas reglas, el dominio lanza `ParametroInvalidoException` y la base H2 no guarda datos corruptos.

### B. Las Dos Reglas de Negocio en la Simulación Persistida
Participan tres clases del modelo: **`Granada` / `Pistola` (familia `Arma`)**, **`Jugador`** y **`Blindaje`**:
1. **Regla 1 — Consumo de munición / detonación y cálculo de daño (`ArmaDeFuego.disparar()` y `Granada.lanzar()` + `calcularDanioExplosion()`):**  
   Al usar un arma en combate se descuenta munición del cargador (o se activa la carga única de la granada calculando la caída lineal de daño según la distancia al epicentro y reduciendo el daño al 35% si el arma y el jugador pertenecen al mismo equipo —fuego amigo—). Si el cargador está vacío o la granada ya fue detonada, el dominio lanza `MunicionInsuficienteException`.
2. **Regla 2 — Mitigación de daño por Kevlar y desgaste de durabilidad (`Blindaje.mitigarDanio()` y `Jugador.recibirImpacto()`):**  
   Cuando el jugador recibe el impacto, su `Blindaje` absorbe su porcentaje configurado de daño (por ejemplo 50%), desgastando su propia durabilidad en H2, y únicamente el daño restante se descuenta de la salud (`vida`) del `Jugador`.

---

## 3. URLs del Servicio REST

| Método | URL | Descripción | Código HTTP esperado |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | Verifica que el servicio está activo | `200 OK` |
| `POST` | `/api/armas` | Crea un arma (`Pistola`, `Granada`, etc.) en H2 | `201 Created` / `400 Bad Request` |
| `GET` | `/api/armas` | Lista todas las armas persistidas en H2 | `200 OK` |
| `GET` | `/api/armas/{id}` | Obtiene un arma por su `id` en H2 | `200 OK` / `404 Not Found` |
| `PUT` | `/api/armas/{id}` | Actualiza y persiste un arma existente | `200 OK` / `400` / `404` |
| `DELETE` | `/api/armas/{id}` | Elimina un arma de H2 | `204 No Content` / `404 Not Found` |
| `GET` | `/armas/catalogo-ui` | Lista armas y jugadores persistidos usando la interfaz `UICapable` | `200 OK` |
| `POST` | `/api/simulacion/explosion` | Ejecuta las 2 reglas de negocio y persiste los cambios en H2 | `200 OK` / `400` / `409` |
| `GET` | `/api/simulacion/explosion/jugadores/{id}` | Consulta el estado persistido de un jugador en H2 | `200 OK` / `404 Not Found` |
| `POST` | `/api/armas/{id}/disparar?balas=N` | Dispara un arma persistida descontando munición en H2 | `200 OK` / `400` / `409 Conflict` |
| `POST` | `/api/armas/{id}/recargar` | Recarga el cargador desde la reserva en H2 | `200 OK` / `409 Conflict` |

---

## 4. Jerarquía de Excepciones y Handlers HTTP

* **Clase Base:** `ArmaException extends RuntimeException`
  * **Hija 1:** `ParametroInvalidoException extends ArmaException` -> Atendida por `@ExceptionHandler(ParametroInvalidoException.class)` en `GlobalExceptionHandler`, devuelve **HTTP 400 Bad Request** con `"tipoExcepcion": "ParametroInvalidoException"`.
  * **Hija 2:** `MunicionInsuficienteException extends ArmaException` -> Atendida por `@ExceptionHandler(MunicionInsuficienteException.class)` en `GlobalExceptionHandler`, devuelve **HTTP 409 Conflict** con `"tipoExcepcion": "MunicionInsuficienteException"`.
  * **Hija adicional (No encontrado):** `ResourceNotFoundException extends ArmaException` -> Atendida por `@ExceptionHandler(ResourceNotFoundException.class)`, devuelve **HTTP 404 Not Found**.

---

## 5. Cómo Probarlo Paso a Paso

### A. Arrancar el servidor
```bash
cd cs2
./mvnw spring-boot:run
```

### B. Probar desde Insomnia
1. Importar el archivo `insomnia/insomnia-cs2-poo08.json` en Insomnia.
2. Ejecutar en orden los 12 pedidos dentro de `me-taller-lp3 -> cs2`:
   * **Pedidos 1 al 5:** Prueban el CRUD completo (`POST`, `GET` lista, `GET` por id, `PUT`, `DELETE`).
   * **Pedido 6 (`POST ilegal`):** Envía `"precio": -500` y recibe `400 Bad Request` sin ensuciar la base H2.
   * **Pedido 7 (`Obtener id inexistente`):** Consulta `/api/armas/999` y recibe `404 Not Found`.
   * **Pedido 8 (`Endpoint Interfaz UICapable`):** Consulta `GET /armas/catalogo-ui` mostrando objetos `Arma` y `Jugador` a través de `UICapable`.
   * **Pedidos 9 y 10 (`Simulación Persistida` + `GET Jugador`):** Ejecuta la detonación de la `Granada HE` (`armaId: 3`) sobre `s1mple (CT)` (`jugadorId: 1`) a 2 metros y luego verifica con `GET /api/simulacion/explosion/jugadores/1` que la vida del jugador quedó en `63.25` y la durabilidad de su Kevlar disminuyó en H2.
   * **Pedido 11 (`Excepción Hija 1`):** Llama a `POST /api/armas/1/disparar?balas=-3` y recibe `400 Bad Request` (`ParametroInvalidoException`).
   * **Pedido 12 (`Excepción Hija 2`):** Llama a `POST /api/armas/3/recargar` (o intenta volver a detonar la granada `3`) y recibe `409 Conflict` (`MunicionInsuficienteException`).

### C. Verificación en Consola H2
* Abrir `http://localhost:8080/h2-console` en el navegador.
* **JDBC URL:** `jdbc:h2:file:./data/armas;AUTO_SERVER=TRUE`
* **User Name:** `sa` (contraseña vacía).
* Ejecutar `SELECT * FROM ARMAS;` y `SELECT * FROM JUGADORES;` para ver las filas persistidas.
