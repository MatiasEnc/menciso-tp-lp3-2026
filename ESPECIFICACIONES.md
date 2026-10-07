# Especificaciones de Entrega — Taller de POO (POO-06)

**Materia:** Lenguaje de Programación 3 (CYT646) — Edición 2026  
**Ejercicio:** POO-06 (Revisión, paquetes, constructores y sobrecarga)  
**Rúbrica:** `rubrica-ejercicios-lp3-2026`  
**Estudiante:** Matias Enciso  
**Dominio:** Counter-Strike 2 (CS2)  
**Repositorio:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026](https://github.com/MatiasEnc/menciso-tp-lp3-2026)  
**Enlace exacto al commit de la solución:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/7b6c8a22a90efac6cb93e524da66977cd942cd85](https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/7b6c8a22a90efac6cb93e524da66977cd942cd85)  

---

## 1. Objetivo del Proyecto

Diseñar e implementar en Java 21 y Spring Boot un modelo orientado a objetos del dominio **Counter-Strike 2**, aplicando:
1. Organización estricta de paquetes según el template oficial de la cátedra (`py.edu.uc.lp3.me.cs2.*`).
2. Encapsulamiento, protección de invariantes y manejo de errores mediante excepciones de dominio (`ArmaException`).
3. Herencia y polimorfismo dinámico mediante un método abstracto en la clase base (`Arma`) sobreescrito por dos clases hijas independientes (`Pistola` y `Granada`).
4. Constructores simples y sobrecargados con delegación (`this(...)` y `super(...)`), además de la sobrecarga de un método de acción en el dominio (`ejecutarAccion(int)`).
5. Exposición de servicios REST mediante controladores Spring Boot para verificar el comportamiento observable por HTTP.

---

## 2. Consignas Aplicadas al Dominio de Counter-Strike 2

### A. Organización en Paquetes
* `py.edu.uc.lp3.me.cs2.domain`: Clases del modelo de negocio (`Arma`, `ArmaDeFuego`, `Pistola`, `Granada`, `RifleAsalto`, `Subfusil`, `Francotirador`, `Escopeta`, `Equipo`).
* `py.edu.uc.lp3.me.cs2.exceptions`: Excepción de dominio (`ArmaException`).
* `py.edu.uc.lp3.me.cs2.rest.controller`: Controladores HTTP (`IndexController`, `ArmaController`).
* `py.edu.uc.lp3.me.cs2`: Clase principal de inicio (`Cs2Application`).

### B. Ocultamiento e Invariantes
* Atributos protegidos y privados (`protected`/`private`).
* Los constructores y setters validan reglas de negocio: precios no negativos (`precio >= 0`), nombres válidos y munición no negativa.
* Si se vulnera una regla, se lanza `ArmaException`, la cual es capturada por el controlador para responder con código HTTP `400 Bad Request`.

### C. Herencia y Sobreescritura (Polimorfismo Dinámico)
* **Clase base:** `Arma` declara `public abstract String ejecutarAccion();`.
* **Clase hija 1 (`Pistola`):** Sobreescribe la acción simulando el disparo balístico (consumo de cargador y silenciador *Pffft* vs *¡PUM!*).
* **Clase hija 2 (`Granada`):** Sobreescribe la acción simulando el lanzamiento y detonación con temporizador, radio expansivo y efecto táctico (humo, ceguera).
* **Consumo polimórfico:** `ArmaController` maneja una lista `List<Arma>` invocando `arma.ejecutarAccion()` sin emplear condicionales `if` ni `instanceof`.

### D. Constructores y Sobrecarga
* **Constructores sobrecargados:**
  * `Arma(nombre, precio)` delega a `this(nombre, precio, Equipo.AMBOS)`.
  * `Pistola(nombre, precio, daño)` delega con `this(...)` asignando valores por defecto seguros.
  * `Granada(nombre, precio, efecto)` delega con `this(...)`.
* **Sobrecarga de método del dominio:**
  * `Arma` declara `ejecutarAccion(int repeticiones)`.
  * En `Pistola`, permite ráfagas de N disparos evaluando munición restante.
  * En `Granada`, permite temporizar/cocinar la granada antes de detonar.

---

## 3. Forma de Probarlo

### A. Compilación y Ejecución
Desde la raíz del subproyecto `cs2`:

```bash
# 1. Compilación
./mvnw clean compile

# 2. Arranque del servicio Spring Boot (puerto 8080)
./mvnw spring-boot:run
```

### B. Pruebas de Endpoints HTTP (en navegador o curl)

1. **Estado general del servicio:**
   * `GET http://localhost:8080/`
   * **Resultado esperado:** JSON con autor, dominio y estado activo.

2. **Demostración de Polimorfismo Dinámico (Sobreescritura):**
   * `GET http://localhost:8080/armas/comportamiento`
   * **Resultado esperado:** Array JSON con `Pistola` (disparo con silenciador) y `Granada` (detonación táctica), procesadas polimórficamente como `Arma`.

3. **Demostración de Sobrecarga del Dominio:**
   * `GET http://localhost:8080/armas/comportamiento-sobrecargado?repeticiones=3`
   * **Resultado esperado:** Ráfaga de 3 disparos en la pistola y retardo en la granada.

4. **Instanciación con Constructor Completo vía URL:**
   * `GET http://localhost:8080/armas/crear?nombre=Glock-18&precio=200&dano=28&balas=20`
   * **Resultado esperado:** Retorno de la instancia de `Pistola` en formato JSON.

5. **Instanciación con Constructor Sobrecargado Simple:**
   * `GET http://localhost:8080/armas/crear-simple?nombre=Desert-Eagle&precio=700&dano=53.0`
   * **Resultado esperado:** Instancia de `Pistola` con valores por defecto legales.

6. **Protección del Invariante (Error Controlado HTTP 400):**
   * `GET http://localhost:8080/armas/crear?nombre=Glock-18&precio=-50&balas=20`
   * **Resultado esperado:** Código HTTP `400 Bad Request` con mensaje `{"error": "El precio no puede ser negativo"}`.
