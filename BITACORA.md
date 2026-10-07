# Bitácora de Desarrollo y Asistencia de Inteligencia Artificial (IA)

**Materia:** Lenguaje de Programación 3 (CYT646) — Edición 2026  
**Ejercicio:** POO-06 (Revisión, paquetes, constructores y sobrecarga)  
**Rúbrica:** `rubrica-ejercicios-lp3-2026`  
**Proyecto:** `menciso-tp-lp3-2026`  
**Estudiante:** Matias Enciso  
**Dominio:** Counter-Strike 2 (CS2)  
**Repositorio GitHub:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026](https://github.com/MatiasEnc/menciso-tp-lp3-2026)  
**Enlace exacto al commit de la solución:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/80c9dd5](https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/80c9dd5)  

---

## 1. Declaración de Asistencia de Inteligencia Artificial (IA)

En cumplimiento con el criterio de entrega de la cátedra:

* **Marca del Asistente:** Google Antigravity
* **Modelo Exacto del LLM:** `Gemini 3.8 Flash` (Google DeepMind)
* **Resumen de los Prompts (en mis propias palabras):**
  Utilicé el asistente como apoyo interactivo tipo pair-programming durante el desarrollo en mi máquina virtual Ubuntu. Le consulté principalmente cómo resolver problemas de red entre macOS y VirtualBox (reenvío de puertos y SSH), cómo estructurar los paquetes siguiendo el template oficial de la cátedra (`domain`, `exceptions`, `rest.controller`), cómo modelar el polimorfismo dinámico con un método abstracto en `Arma` sin usar `if` en el controller, la forma correcta en Java de encadenar constructores sobrecargados con `this(...)` y `super(...)`, cómo sobrecargar métodos de dominio, y cómo solucionar fallos de resolución DNS al hacer `git push`.

---

## 2. Historial de Sesiones, Dudas y Preguntas a la IA

A lo largo del taller fui trabajando en mi máquina virtual Ubuntu conectada por SSH a la terminal de mi Mac. Este es el relato cronológico de cómo fui avanzando y las preguntas que le fui haciendo al asistente en cada etapa.

---

### Sesión 1 — 2026-09-16 · Inicio e Infraestructura

* **Qué hice:** Creé el repositorio público en GitHub con licencia Apache 2.0 (`LICENSE`) y el `README.md` inicial. Luego generé el esqueleto de Spring Boot desde `start.spring.io` con Maven, Java 21 y la dependencia Spring Web dentro de la carpeta `cs2/`.
* **Commits:**
  * `b6a4665` — *Initial commit:* repositorio con `LICENSE` y `README.md`.
  * `b998d54` — *clase 16/09:* proyecto Spring Boot `cs2` con `pom.xml`, `mvnw`, clase `Cs2Application` y tests de contexto.

---

### Sesión 2 — 2026-09-20 · Modelado Inicial y Conectividad por SSH

* **Qué hice:** Migré las clases del modelado que habíamos hecho en clase del 2 y 3 de septiembre al proyecto Spring Boot (`Arma`, `ArmaDeFuego`, `Equipo`, `Pistola`, `Granada`, `RifleAsalto`, `Subfusil`, `Francotirador`, `Escopeta`) y creé el primer `IndexController`.
* **Preguntas y problemas que tuve:**
  1. *¿Cómo me conecto a mi máquina virtual por SSH desde la terminal de mi Mac?*  
     Al hacer `ip a` en Ubuntu vi la IP `10.0.2.15`. Intenté hacer `ssh Matias@10.0.2.15` desde mi Mac y se quedaba colgado sin conectar. Le pregunté al asistente qué pasaba y me explicó que `10.0.2.15` es una IP interna del modo NAT de VirtualBox que la Mac no puede alcanzar directamente. Me guió para configurar una regla de reenvío de puertos (*Port Forwarding*) en la configuración de red de VirtualBox, mapeando el puerto `2222` de la máquina anfitriona (Mac) al puerto `22` de la máquina invitada (Ubuntu).
  2. *¿Por qué me salía "Connection reset by peer" al intentar conectar?*  
     Al ejecutar `ssh -p 2222 matias@127.0.0.1` la conexión se cerraba de inmediato. El asistente revisó el estado y detectó que en Ubuntu no estaba instalado ni corriendo el servidor SSH. Me indicó instalar `openssh-server` con `sudo apt install openssh-server` y habilitarlo con `sudo systemctl enable --now ssh`. Una vez activo el servicio, pude ingresar sin problemas desde la terminal de Mac.
* **Commit:**
  * `a0ff1f4` — *Agregue entidades:* primeras clases del dominio y `IndexController` básico.

---

### Sesión 3 — 2026-09-24 · Refactor, Ocultamiento, Polimorfismo y REST

* **Qué hice:** Corregí las declaraciones de paquetes de las clases, actualicé el `IndexController` para que devuelva un JSON con autor y estado, y apliqué el refactor de herencia y método abstracto requerido por la consigna.
* **Preguntas que le hice al asistente:**
  1. *¿Cómo cumplo el requisito del método abstracto y las dos hijas sin usar `if` por tipo?*  
     Le pregunté cómo estructurar la jerarquía de CS2 para que dos clases hijas tuvieran comportamientos bien diferenciados. Me recomendó declarar en la clase base `Arma` el método abstracto `public abstract String ejecutarAccion();`. Elegimos dos ramas diferentes del dominio:
     * `Pistola` (hereda de `ArmaDeFuego` -> `Arma`): implementa el disparo balístico descontando munición del cargador y reportando si tiene silenciador puesto o no.
     * `Granada` (hereda directamente de `Arma`): implementa la acción de arrojarse y detonar con radio de explosión y efecto táctico (humo, ceguera).
  2. *¿Cómo debe construirse el endpoint en el controller?*  
     Le pregunté cómo hacer para que el controller no tenga que preguntar qué tipo de arma es cada una. Me explicó que debía crear una lista del tipo padre `List<Arma>` conteniendo una pistola y una granada, y dentro de un `for (Arma arma : arsenal)` llamar directamente a `arma.ejecutarAccion()`. Gracias al polimorfismo dinámico en tiempo de ejecución, cada objeto responde con su comportamiento correspondiente sin requerir `if`, `switch` ni `instanceof`.
  3. *¿Cómo valido los datos para proteger el invariante?*  
     Consulté qué hacer si alguien manda un precio negativo o valores inválidos desde los parámetros de la URL. Agregamos validaciones en los constructores y setters de `Arma` (`precio < 0` lanza excepción), y en `ArmaController` (`GET /armas/crear`) capturamos el error para devolver un código HTTP `400 Bad Request`.
* **Commits:**
  * `37aa1c7` — *Se agrego package a cada entidad:* empaquetado formal de las clases.
  * `8fa7aae` — *Altualizar IndexController con respuesta de estado de API:* endpoint `GET /` devolviendo JSON con autor, dominio y estado.
  * `57dc725` — *feat: metodo abstracto en Arma, hijas Pistola y Granada, y ArmaController REST:* método abstracto en `Arma`, sobreescritura en `Pistola` y `Granada`, endpoints `/armas/crear` y `/armas/comportamiento`.
  * `352ad76` — *docs: diagrama Mermaid del modelado (2 y 3 sep):* inclusión del diagrama de clases en el `README.md`.

---

### Sesión 4 — 2026-10-07 · Revisión POO-06: Paquetes, Constructores y Sobrecarga

* **Qué hice:** Tomé la rúbrica vigente de la cátedra (`rubrica-ejercicios-lp3-2026`) correspondiente al ejercicio `POO-06` para asegurar que el proyecto cumpliera con los 8 criterios de calificación (24/24 puntos).
* **Preguntas y ajustes realizados:**
  1. *¿Cómo organizo los paquetes según el template de la cátedra?*  
     Le pasé el enlace del template oficial (`lp3-template-tp`) y le pregunté cómo organizar mis carpetas. Reorganizamos el código separando claramente el dominio de la entrada HTTP:
     * `py.edu.uc.lp3.me.cs2.domain` para las entidades (`Arma`, `Pistola`, `Granada`, `Equipo`, etc.).
     * `py.edu.uc.lp3.me.cs2.exceptions` para la excepción del dominio (`ArmaException`).
     * `py.edu.uc.lp3.me.cs2.rest.controller` para los controladores REST (`IndexController`, `ArmaController`).
     * `py.edu.uc.lp3.me.cs2` para la clase de inicio de Spring Boot (`Cs2Application`).
  2. *¿Cómo cumplo el Criterio 5 de constructores simples y sobrecargados?*  
     Le pregunté cómo implementar constructores sobrecargados de forma limpia sin duplicar lógica de inicialización. Me explicó que la buena práctica en Java es tener un constructor principal completo y constructores simples sobrecargados que llamen a `this(...)` suministrando valores por defecto legales. Por ejemplo, en `Pistola` agregué un constructor simple `Pistola(nombre, precio, daño)` que delega en el completo asignando valores por defecto seguros (cargador de 12 balas, equipo AMBOS, sin silenciador), y en `Granada` un constructor simple `Granada(nombre, precio, efecto)`.
  3. *¿Cómo sobrecargo un mensaje del dominio?*  
     La rúbrica pedía además la sobrecarga de un mensaje del dominio (la misma acción con distinta lista de argumentos). Le pregunté qué mensaje tenía sentido sobrecargar en CS2. Sobrecargamos `ejecutarAccion(int repeticiones)` en `Arma`:
     * En `Pistola`: `ejecutarAccion()` realiza 1 disparo individual, mientras que `ejecutarAccion(int disparos)` realiza una ráfaga de N disparos consecutivos evaluando la munición disponible.
     * En `Granada`: `ejecutarAccion()` realiza una detonación estándar inmediata, mientras que `ejecutarAccion(int retrasoSegundos)` permite temporizar o cocinar la granada (*cooking grenade*) antes del estallido.
  4. *¿Cómo muestro esto en los controllers?*  
     Agregué los endpoints `GET /armas/crear-simple` (para instanciar usando el constructor sobrecargado) y `GET /armas/comportamiento-sobrecargado?repeticiones=3` (para observar la sobrecarga en funcionamiento).
  5. *¿Por qué volvió a fallar `git push` con "Could not resolve host: github.com"?*  
     Al intentar pushear desde la máquina virtual volvió a saltar el error de resolución DNS. Le pregunté qué pasaba y me recordó que VirtualBox a veces resetea los servidores DNS en `/etc/resolv.conf`. Lo arreglé ejecutando `echo "nameserver 8.8.8.8" | sudo tee /etc/resolv.conf` y configurando `git config --global credential.helper store` para almacenar las credenciales de Git.
* **Commits:**
  * `c718b44` — *feat: paquetes domain/rest, constructores y metodos sobrecargados e invariantes con ArmaException:* reestructuración de paquetes, constructores sobrecargados, sobrecarga de métodos y excepciones de dominio.
  * `637dddb` — *docs: README con sobrecarga vs sobreescritura, diagrama Mermaid y BITACORA de IA:* actualización completa de la documentación y creación de `BITACORA.md`.
  * `80c9dd5` — *docs: actualizar enlace al commit y bitacora de especificaciones para Classroom:* enlace definitivo al commit calificado.

---

## 3. Respuestas a los Criterios de Diseño OO (Defensa en Sala)

### 1. ¿El controller usa el tipo padre o pregunta el tipo concreto?
El controlador utiliza **estrictamente el tipo padre `Arma`**. En los endpoints `/armas/comportamiento` y `/armas/comportamiento-sobrecargado`, las armas se almacenan en una colección `List<Arma>`. Al iterar, se invoca `arma.ejecutarAccion()`. La llamada se resuelve en tiempo de ejecución por **despacho dinámico (polimorfismo dinámico)**. En ningún lugar del controller se utiliza `instanceof`, casting explícito de tipos ni sentencias `if/switch` para averiguar si es una Pistola o una Granada.

### 2. ¿Se puede romper el invariante desde el controller u otra capa?
**No.** Los atributos de las entidades están encapsulados (`protected`/`private`) y su estado solo puede modificarse mediante constructores validados o mensajes del dominio. Si desde un parámetro de la URL se intenta enviar un valor ilegal (por ejemplo, `precio = -50` o cantidad de balas negativa), la clase arroja inmediatamente una excepción `ArmaException`. El controlador captura esta excepción y retorna una respuesta HTTP `400 Bad Request`, impidiendo que el objeto quede en un estado imposible.

### 3. ¿Por qué la clase base (`Arma`) no puede implementar el método abstracto?
Porque `Arma` es una abstracción general que modela atributos comunes de inventario y tienda (nombre, costo, equipo habilitado). No existe una forma uniforme de "ejecutar una acción" a nivel base: un arma de fuego opera mediante percusión mecánica, cargador de balas y recarga, mientras que una granada es un artefacto arrojadizo de un solo uso que se activa por temporizador y onda expansiva. Una implementación concreta en el padre no tendría sentido real; cada subclase debe definir su propia mecánica.

---

## 4. Sobrecarga vs. Sobreescritura en el Código

En el `README.md` del repositorio incluí una sección detallada sobre cómo se diferencian conceptualmente y en qué clases se implementaron:

| Concepto | Sobrecarga (*Overloading*) | Sobreescritura (*Overriding*) |
| :--- | :--- | :--- |
| **Definición** | Mismo nombre de método con **diferente lista de parámetros** en la misma clase o jerarquía. | Reimplementación de un método en una clase hija con la **misma firma exacta** que en el padre. |
| **Resolución** | En **tiempo de compilación** (*Enlace estático / Static Binding*). | En **tiempo de ejecución** (*Despacho dinámico / Polimorfismo*). |
| **Anotación** | No lleva anotación especial. | Lleva la anotación obligatoria `@Override`. |
| **En el proyecto** | **Constructores:** `Pistola(nombre, precio, daño)` vs `Pistola(nombre, precio, equipo, ...)`; `Granada(nombre, precio, efecto)` vs constructor completo.<br>**Métodos:** `ejecutarAccion()` vs `ejecutarAccion(int repeticiones)`; `disparar()` vs `disparar(int balas)`. | Método abstracto `ejecutarAccion()` declarado en `Arma` e implementado diferencialmente en `Pistola` (disparo con silenciador) y en `Granada` (lanzamiento y explosión táctica). |

---

## 5. Verificación de Funcionamiento y Endpoints

### Compilación y Arranque
* **Compilación:** `./mvnw clean compile` -> **`BUILD SUCCESS`** (13 clases compiladas en Java 21).
* **Ejecución del servicio:** `./mvnw spring-boot:run` (levanta en `http://localhost:8080`).

### Pruebas de Endpoints HTTP
1. **Estado general del servicio:**  
   `GET http://localhost:8080/`  
   Respuesta: `{"autor": "Matias Enciso", "dominio": "CS2", "estado": "API funcionando"}`

2. **Comportamiento Polimórfico (Sobreescritura):**  
   `GET http://localhost:8080/armas/comportamiento`  
   Respuesta: Devuelve el JSON con las dos hijas independientes (`USP-S` disparando silenciado con munición 11/12, y `Granada Flashbang` detonando con efecto de ceguera temporal), tratadas ambas como `Arma`.

3. **Comportamiento Sobrecargado:**  
   `GET http://localhost:8080/armas/comportamiento-sobrecargado?repeticiones=3`  
   Respuesta: Demuestra la invocación de `ejecutarAccion(int)` con ráfaga de 3 disparos en la pistola y retardo en la granada.

4. **Construcción con Constructor Completo por URL:**  
   `GET http://localhost:8080/armas/crear?nombre=Glock-18&precio=200&dano=28&balas=20`  
   Respuesta: Instancia exitosa y retorno del objeto en JSON.

5. **Construcción con Constructor Sobrecargado Simple:**  
   `GET http://localhost:8080/armas/crear-simple?nombre=Desert-Eagle&precio=700&dano=53.0`  
   Respuesta: Instancia la pistola usando valores por defecto para cargador y silenciador.

6. **Protección del Invariante (Error Controlado HTTP 400):**  
   `GET http://localhost:8080/armas/crear?nombre=Glock-18&precio=-50&balas=20`  
   Respuesta: `{"error": "El precio no puede ser negativo"}` con código HTTP 400 Bad Request.
