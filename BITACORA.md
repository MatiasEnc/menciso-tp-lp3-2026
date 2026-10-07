# Bitácora de Asistencia de Inteligencia Artificial (IA)

**Proyecto:** `menciso-tp-lp3-2026`  
**Asignatura:** Lenguaje de Programación 3 (CYT646) — Edición 2026  
**Estudiante:** Matias Enciso  
**Fecha de vigencia:** 30 de septiembre - 7 de octubre de 2026  

---

## 1. Asistente y Modelo de Lenguaje Utilizado

* **Marca del Asistente:** Google Antigravity
* **Modelo Exacto del LLM:** `Gemini 3.8 Flash` (Google DeepMind)

---

## 2. Resumen de Prompts y Sesiones de Trabajo

A continuación se resumen las interacciones y solicitudes realizadas al asistente a lo largo del desarrollo:

1. **Diagnóstico y Configuración de Conectividad SSH:**
   * *Prompt inicial:* Solicitud de asistencia para conectar la terminal local de macOS con la máquina virtual de VirtualBox que se encontraba aislada bajo red NAT (`10.0.2.15`).
   * *Acción:* Configuración del reenvío de puertos (puerto local `2222` hacia puerto invitado `22`), resolución de DNS interna en Ubuntu y verificación del servicio `openssh-server`.

2. **Revisión de Rúbrica y Estructura de Paquetes (POO-06):**
   * *Prompt:* Adecuar la estructura del proyecto al template oficial de la cátedra separando el dominio de la capa HTTP.
   * *Acción:* Modularización en paquetes `py.edu.uc.lp3.me.cs2.domain`, `py.edu.uc.lp3.me.cs2.exceptions` y `py.edu.uc.lp3.me.cs2.rest.controller`.

3. **Ocultamiento, Invariantes y Excepciones del Dominio:**
   * *Prompt:* Implementar reglas estrictas de validación de negocio para evitar que cualquier capa externa deje a las entidades en un estado inválido.
   * *Acción:* Creación de `ArmaException` y validaciones en constructores y setters de `Arma` y `ArmaDeFuego` (precios no negativos, munición no negativa, porcentajes de precisión válidos).

4. **Herencia y Sobreescritura (Polimorfismo Dinámico):**
   * *Prompt:* Declarar un método abstracto en la clase base `Arma` e implementarlo de forma independiente en dos clases hijas concretas.
   * *Acción:* Declaración de `ejecutarAccion()` en `Arma` y su sobreescritura diferencial en `Pistola` (consumo de cargador y silenciador) y `Granada` (lanzamiento y detonación con radio táctico).

5. **Constructores Sobrecargados y Sobrecarga de Mensajes del Dominio:**
   * *Prompt:* Cumplir el criterio de constructores simples y sobrecargados, además de sobrecargar un mensaje de acción en las clases del dominio.
   * *Acción:* Incorporación de constructores simples que delegan con `this(...)` y constructores completos con llamada a `super(...)`. Sobrecarga del mensaje `ejecutarAccion(int)` y `disparar(int balas)`.

6. **Servicios REST y Comportamiento Observable:**
   * *Prompt:* Crear endpoints en Spring Boot para verificar el servicio, permitir construcción por URL y responder con el JSON polimórfico y sobrecargado.
   * *Acción:* Desarrollo de `IndexController` (`GET /`) y `ArmaController` (`GET /armas/crear`, `GET /armas/crear-simple`, `GET /armas/comportamiento`, `GET /armas/comportamiento-sobrecargado`).

7. **Documentación Técnica y Diagrama Mermaid:**
   * *Prompt:* Actualizar el `README.md` explicando conceptualmente la diferencia entre sobrecarga y sobreescritura, junto con el diagrama Mermaid de clases.
   * *Acción:* Redacción de las secciones técnicas en el README y elaboración del diagrama de clases alineado a `src/`.
