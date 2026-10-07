# Bitácora de uso de IA

**Asistente usado:** Antigravity (Google DeepMind)  
**Modelo exacto:** Gemini 3.8 Flash (High)  
**Fecha:** 7 de Octubre de 2026  

---

## Resumen de cómo usé la IA (Prompts)

Para hacer este TP me apoyé en el asistente de IA para acelerar el código rutinario y armar bien la estructura inicial. Estos son los pasos principales que le fui pidiendo:

1. Al principio le pedí ayuda para armar una estructura en Mermaid con el diagrama de clases del juego Counter-Strike 2 (basado en lo que habíamos modelado en clase el 2 y 3 de septiembre) y pasarlo a mi VS Code para incluirlo en el `README.md`.
2. Después le pedí asistencia para configurar la conexión SSH desde la terminal de mi Mac hacia mi máquina virtual de Ubuntu en VirtualBox y preparar el entorno para compilar con `./mvnw` sin problemas.
3. Le pasé la estructura de paquetes que pedía el template de la cátedra (`py.edu.uc.lp3.me.cs2`) y le pedí que me ayude a organizar las clases separando dominio, excepciones y controladores REST.
4. Para la parte del dominio, le pedí que arme la clase base abstracta `Arma` con atributos encapsulados, métodos abstractos y constructores sobrecargados como pedía la consigna.
5. Luego le pedí que me genere las clases hijas del dominio (`Pistola`, `Granada`, `ArmaDeFuego`, etc.) usando `@Override` para implementar los métodos abstractos del padre y definiendo constructores que deleguen con `this(...)` y `super(...)`.
6. Una vez que estuvo el dominio, le pedí que me ayude a armar los Controllers REST en Spring Boot: el `IndexController` para la raíz `/` y el `ArmaController` para instanciar armas leyendo parámetros de la URL con `@RequestParam` y comprobar el polimorfismo dinámico en formato JSON.
7. Por último, le pedí que me ayude a revisar las validaciones para proteger los invariantes de los objetos (evitar precios negativos o datos inválidos) y comprobar que la compilación con `./mvnw clean compile` funcionara correctamente.

---

**Repositorio:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026](https://github.com/MatiasEnc/menciso-tp-lp3-2026)  
**Commit de la solución:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/b3dedb9](https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/b3dedb9)
