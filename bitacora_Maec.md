# Bitácora Técnica de Desarrollo y Especificaciones (POO-06)

**Asignatura:** Lenguaje de Programación 3 (CYT646) — Edición 2026  
**Ejercicio:** POO-06 (Revisión, paquetes, constructores y sobrecarga)  
**Rúbrica:** `rubrica-ejercicios-lp3-2026`  
**Estudiante:** Matias Enciso  
**Dominio:** Counter-Strike 2 (CS2)  
**Repositorio GitHub:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026](https://github.com/MatiasEnc/menciso-tp-lp3-2026)  
**Enlace exacto al commit de la solución:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/637dddb](https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/637dddb)  

---

## 1. Cumplimiento de los 8 Criterios de la Rúbrica (24/24)

### Criterio 1: Entrega y arranque (3/3)
- Repositorio público disponible en GitHub con licencia abierta [Apache License 2.0](https://github.com/MatiasEnc/menciso-tp-lp3-2026/blob/main/LICENSE).
- El proyecto Spring Boot compila de forma limpia (`BUILD SUCCESS`) y arranca con `./mvnw spring-boot:run` en el puerto 8080.
- Los artefactos generados por el compilador (`target/`) quedan excluidos del control de versiones mediante `.gitignore`.
- Se entrega el enlace directo al commit de la solución: `https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/637dddb`.

### Criterio 2: Organización en paquetes (3/3)
Estructura organizada según el template oficial de la cátedra (`py.edu.uc.lp3`):
- `py.edu.uc.lp3.me.cs2` -> Clase principal de arranque de Spring Boot (`Cs2Application`).
- `py.edu.uc.lp3.me.cs2.domain` -> Clases del modelo del juego (`Arma`, `ArmaDeFuego`, `Pistola`, `Granada`, `RifleAsalto`, `Subfusil`, `Francotirador`, `Escopeta`, `Equipo`).
- `py.edu.uc.lp3.me.cs2.exceptions` -> Excepciones del dominio (`ArmaException`).
- `py.edu.uc.lp3.me.cs2.rest.controller` -> Capa de entrada HTTP / Controladores REST (`IndexController`, `ArmaController`).

### Criterio 3: Ocultamiento e invariantes (3/3)
- El estado interno no está expuesto de forma directa. Los atributos se protegen y se acceden mediante métodos con control.
- Invariantes de dominio protegidos por `ArmaException`:
  - `precio >= 0` (precios negativos son rechazados).
  - `municionCargador >= 0` y `municionReserva >= 0` (cantidades no negativas).
  - `daño >= 0`, `precision` en rango `0.0` a `100.0`.
  - `radioExplosion >= 0` y `tiempoActivacion >= 0` en granadas.
- **Respuesta a la pregunta de anclaje:** ¿Puede otra capa dejar el objeto en un estado imposible?  
  *No.* Si un cliente HTTP o controller intenta instanciar o modificar un objeto con valores ilegales (ej. `precio = -50`), el constructor o setter arroja `ArmaException` y el controlador responde HTTP `400 Bad Request`.

### Criterio 4: Herencia, clases hijas y sobreescritura (3/3)
- Jerarquía "es un":
  - `Arma` es la clase base abstracta que declara el método abstracto `public abstract String ejecutarAccion();`.
  - Dos clases hijas independientes implementan este método con la misma firma (`@Override`):
    * `Pistola` (hereda de `ArmaDeFuego` -> `Arma`): implementa el disparo balístico con gestión de balas y silenciador.
    * `Granada` (hereda directamente de `Arma`): implementa el lanzamiento y detonación táctica de área.
  - El controlador (`GET /armas/comportamiento`) interactúa exclusivamente con el tipo padre `Arma`, aprovechando el polimorfismo dinámico en tiempo de ejecución sin sentencias condicionales `if` o `instanceof`.

### Criterio 5: Constructores y sobrecarga (3/3)
- **Constructores simples y sobrecargados:**
  - `Arma`: Constructor completo `(nombre, precio, equipo)` y constructor simple sobrecargado `(nombre, precio)` delegando en `this(...)`.
  - `Pistola`: Constructor completo con balística detallada y constructor simple `(nombre, precio, daño)` delegando con `this(...)`. La clase hija llama a `super(...)` en su constructor principal.
  - `Granada`: Constructor completo y constructor simple `(nombre, precio, efecto)` delegando con `this(...)`.
- **Sobrecarga de mensaje del dominio:**
  - En `Arma`: `ejecutarAccion()` y `ejecutarAccion(int repeticiones)`.
  - En `ArmaDeFuego`: `disparar()` y `disparar(int balas)` (ráfaga).
  - En `Pistola`: `ejecutarAccion()` y `ejecutarAccion(int disparos)`.
  - En `Granada`: `lanzar()` y `lanzar(double distanciaMetros)`, más `ejecutarAccion()` y `ejecutarAccion(int retrasoSegundos)`.

### Criterio 6: Comportamiento observable (3/3)
- Servicios REST funcionando y verificables en Spring Boot:
  - `GET /` -> Estado general del servicio (`IndexController`).
  - `GET /armas/crear?nombre=Glock-18&precio=200&dano=28&balas=20` -> Construcción con constructor completo.
  - `GET /armas/crear-simple?nombre=Desert-Eagle&precio=700&dano=53.0` -> Construcción con constructor simple sobrecargado.
  - `GET /armas/crear?nombre=Glock-18&precio=-50&balas=20` -> Rechazo de precio inválido (HTTP 400).
  - `GET /armas/comportamiento` -> JSON con comportamiento polimórfico de las dos hijas (Sobreescritura).
  - `GET /armas/comportamiento-sobrecargado?repeticiones=3` -> JSON demostrando la sobrecarga de mensaje de dominio.

### Criterio 7: Git (3/3)
- Historial ordenado con commits semánticos y atómicos:
  - `b6a4665`: Initial commit
  - `b998d54`: clase 16/09 (Spring Boot starter)
  - `a0ff1f4`: Agregue entidades
  - `37aa1c7`: Se agrego package a cada entidad
  - `8fa7aae`: Actualizar IndexController con respuesta de estado de API
  - `57dc725`: feat: metodo abstracto en Arma, hijas Pistola y Granada, y ArmaController REST
  - `352ad76`: docs: diagrama Mermaid del modelado
  - `c718b44`: feat: paquetes domain/rest, constructores y metodos sobrecargados e invariantes con ArmaException
  - `637dddb`: docs: README con sobrecarga vs sobreescritura, diagrama Mermaid y BITACORA de IA

### Criterio 8: Documentación y defensa (3/3)
- `README.md` actualizado con diagrama Mermaid completo y sección dedicada que explica formalmente la sobrecarga (*overloading*) vs sobreescritura (*overriding*).
- `BITACORA.md` en la raíz del repositorio detallando el asistente utilizado (Google Antigravity), el modelo LLM (`Gemini 3.8 Flash`) y el resumen de prompts.
- Especificaciones completas listas para entrega en Classroom.

---

## 2. Guía de Ejecución y Pruebas

```bash
cd cs2
./mvnw clean compile
./mvnw spring-boot:run
```

Probar en el navegador:
1. `http://localhost:8080/`
2. `http://localhost:8080/armas/comportamiento`
3. `http://localhost:8080/armas/comportamiento-sobrecargado?repeticiones=3`
4. `http://localhost:8080/armas/crear?nombre=Glock-18&precio=200&dano=28&balas=20`
5. `http://localhost:8080/armas/crear-simple?nombre=Desert-Eagle&precio=700&dano=53.0`
6. `http://localhost:8080/armas/crear?nombre=Glock-18&precio=-50&balas=20` (HTTP 400)
