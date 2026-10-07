# Bitácora de uso de IA

**Asistente usado:** Antigravity (Google DeepMind)  
**Modelo exacto:** Gemini 3.8 Flash (High)  
**Fecha:** 7 de Octubre de 2026  

---

## Resumen de cómo usé la IA (Prompts)

Para hacer este TP me apoyé en el asistente de IA para acelerar el código rutinario y armar bien la estructura inicial. Estos son los pasos principales que le fui pidiendo:

1. Primero le pedí ayuda para configurar el entorno y la conexión SSH desde la terminal de mi Mac hacia mi máquina virtual de Ubuntu en VirtualBox para poder correr `./mvnw` y compilar sin problemas.
2. Le pasé la estructura de paquetes que pedía el template de la cátedra (`py.edu.uc.lp3.me.cs2`) para separar las capas de dominio, excepciones y controladores REST.
3. Para la parte del dominio, le expliqué que quería modelar las armas del Counter-Strike 2. Le pedí que arme la clase base abstracta `Arma` con atributos encapsulados, métodos abstractos y constructores sobrecargados como pedía la consigna.
4. Después le pedí que me genere las clases hijas del dominio (`Pistola`, `Granada`, `ArmaDeFuego`, etc.) usando `@Override` para implementar los métodos abstractos del padre y definiendo constructores que deleguen con `this(...)` y `super(...)`.
5. Una vez que estuvo el dominio, le pedí que me ayude a armar los Controllers REST en Spring Boot: el `IndexController` para la raíz `/` y el `ArmaController` para instanciar armas leyendo parámetros de la URL con `@RequestParam` y comprobar el polimorfismo dinámico en formato JSON.
6. Por último, le pedí que me ayude a revisar las validaciones para proteger los invariantes de los objetos (evitar precios negativos o datos inválidos) y comprobar que la compilación con `./mvnw clean compile` funcionara correctamente.

---

**Repositorio:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026](https://github.com/MatiasEnc/menciso-tp-lp3-2026)  
**Commit de la solución:** [https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/b3dedb9](https://github.com/MatiasEnc/menciso-tp-lp3-2026/commit/b3dedb9)
