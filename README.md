# Software Maturity RPG — La Senda de la Calidad 🛡️💻

Un videojuego RPG desarrollado en **Java Swing** diseñado específicamente para poner en práctica y asimilar de forma interactiva y divertida todos los conceptos vistos en las materias de calidad y procesos de software: **CMMI**, **MoProSoft**, **PSP** y **TSP**.

---

## 🎮 ¿Cómo se relaciona el juego con los 4 PDFs?

### 1. 🏛️ CMMI (Capability Maturity Model Integration)
El juego se estructura a lo largo de los **5 Niveles de Madurez de CMMI**:
- **Nivel 1 (Inicial / Caótico):** Misiones iniciales donde no hay procesos definidos, el éxito depende del heroísmo individual y los bugs causan retrasos y sobrecostos.
- **Nivel 2 (Gestionado):** Se implementa la gestión de requisitos, planes documentados que se respetan bajo estrés y aseguramiento de la calidad (QA).
- **Nivel 3 (Definido):** Procesos estandarizados en toda la organización, métodos proactivos y gestión integral de riesgos.
- **Nivel 4 (Cuantitativamente Gestionado):** Desbloquea la habilidad de **Control Estadístico**, controlando subprocesos y erradicando causas especiales de variación.
- **Nivel 5 (Optimizado):** Desbloquea la habilidad de **Mejora Continua e Innovación**, mitigando causas comunes de variación y otorgando regeneración pasiva.

### 2. 🇲🇽 MoProSoft (Norma Mexicana NMX-I-059-NYCE)
En la pestaña **Org. MoProSoft** gestionas la estructura empresarial en sus **3 Categorías**:
- **Alta Dirección (DIR):** Gestión de Negocios y presupuesto corporativo.
- **Gerencia (GER):** Inversión y fortalecimiento de los 3 recursos clave:
  - **RH (Recursos Humanos):** Capacitación y mejora del talento.
  - **BSI (Bienes, Servicios e Infraestructura):** Servidores y herramientas CASE para mayor ataque técnico.
  - **CO (Conocimiento de la Organización):** Repositorio de lecciones aprendidas para reducir inyección de defectos.
- **Operación (OPE):** Administración de Proyectos Específicos y Desarrollo/Mantenimiento.
- **Niveles de Capacidad (0 al 5):** Permite evaluar y certificar formalmente a tu organización (Incompleto &rarr; Realizado &rarr; Administrado &rarr; Establecido &rarr; Predecible &rarr; Optimizado).

### 3. 👤 PSP (Personal Software Process - Watts Humphrey)
El protagonista sigue la metodología y disciplina del ingeniero individual:
- **Niveles PSP:** Evolución desde `PSP 0` hasta `PSP 2.1` conforme ganas experiencia.
- **Bitácora de Tiempos (Time Log):** Registra fecha, inicio, fin, tiempo de interrupción, delta en minutos, actividad y completitud (idéntico a la pág. 17).
- **Bitácora de Defectos (Defect Log):** Clasifica los bugs con la taxonomía de **Chillarege / IBM** (Tipos 10 Documentación, 20 Sintaxis, 30 Construcción, 40 Asignación, 50 Interfaz, 60 Chequeo, 70 Datos, 80 Función/Lógica, 90 Sistema, 100 Entorno).
- **Métricas Post-Mortem:** Resumen del proyecto y cálculo en tiempo real de la **Densidad de Defectos**:
  $$Dd = 1000 \times \frac{D}{N}$$
- **Plan de Mejora Personal (PIP):** Permite registrar propuestas de mejora en el propio proceso personal para recuperar energía y puntos organizacionales.
- **Herramientas PSP:** Uso de *Checklists de Inspección* (PSP 2) y *Plantillas de Diseño* (PSP 2.1).

### 4. 👥 TSP (Team Software Process)
En la pestaña **Gremio TSP** lideras una cuadrilla de ingenieros con los **5 roles oficiales**:
1. **Líder del Equipo:** Coordina, motiva y realiza el reporte semanal.
2. **Gestor de Desarrollo:** Lidera la arquitectura y diseño técnico.
3. **Gestor de Planificación:** Seguimiento de calendarios y estimación de esfuerzo.
4. **Gestor de Calidad / Proceso:** Modera inspecciones por pares y gestiona estándares.
5. **Administrador de Requerimientos / Soporte:** Gestiona la configuración y tecnología de soporte.
- **Condición clave de TSP:** Cada miembro es entrenado individualmente en **PSP**.
- **Juntas Semanales:** Permiten coordinar el esfuerzo y recargar energía de disciplina.

---

## 🚀 ¿Cómo Ejecutar el Juego?

Requisito: **JDK 17 o superior** (`java -version` y `javac -version` deben funcionar).

### Opción 1: Con los lanzadores (Windows)
Haz doble clic sobre:
- `run_game.bat` (ejecuta el juego; si aún no está compilado, lo compila primero)
- `build_and_run.bat` (recompila y ejecuta)
- `run_tests.bat` (ejecuta las pruebas JUnit; la primera vez descarga Maven automáticamente)

### Opción 2: Con Maven Wrapper (Windows, Linux o macOS, sin instalar Maven)
```powershell
# Windows
mvnw.cmd package
java -jar target\software-maturity-rpg.jar
```
```bash
# Linux / macOS
./mvnw package
java -jar target/software-maturity-rpg.jar
```
`package` compila, ejecuta las pruebas y genera el jar ejecutable. Solo pruebas: `mvnw.cmd test` o `./mvnw test`.

### Opción 3: Solo con el JDK
```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName)
java -cp bin com.softwarerpg.Main
```

## 🎮 Loop de juego

1. En **Campaña CMMI** despliega la misión marcada como **⭐ SIGUIENTE MISIÓN** (las demás se desbloquean en orden).
2. En **Combate RPG**, *Codifica* para dañar al defecto. Si tu salud baja, usa la *Checklist PSP* o la *Sinergia del Líder*. Cada botón muestra su costo y, al pasar el ratón, su efecto.
3. Al terminar aparece el letrero de **¡VICTORIA!** o **¡PERDISTE!** con el resumen Post-Mortem PSP (tamaño y tiempo estimado vs. real, defectos, Dd y yield). En derrota puedes reintentar.
4. Con el oro compra checklists y plantillas, mejora tu organización (MoProSoft) y entrena a tu equipo (TSP).

La campaña tiene **10 misiones** (dos por nivel CMMI: una intermedia y el jefe que te asciende). El oro es escaso a propósito:

- Repetir una misión ya completada paga el **25%**; cada *Caza Rápida* paga la mitad que la anterior (se renueva al completar una misión nueva) y sus bugs escalan con tu nivel.
- Máximo **5 checklists** en el inventario; dentro de un mismo combate cada revisión extra cura menos.
- Entrenar al equipo TSP cuesta más en cada nivel.

## 💾 Partidas guardadas

La partida se guarda sola al terminar cada combate y también desde el menú **Partida → Guardar** (Ctrl+S). Al abrir el juego se ofrece continuar la partida guardada. El archivo está en `%USERPROFILE%\.software-maturity-rpg\partida.properties` (Windows) o `~/.software-maturity-rpg/partida.properties` (Linux/macOS).

## 🧪 Pruebas

`test/` contiene pruebas JUnit 5 del motor de combate, la bitácora PSP, el guardado y una **prueba de balance** que simula 200 campañas completas de las 10 misiones con tres perfiles: un jugador que aplica el proceso (checklists, estimación, TSP, CMMI) gana las misiones 3 a 10 entre 50% y 90% al primer intento; uno que solo codifica no pasa de la misión 4; y uno que "farmea" oro con cazas rápidas no puede aplanar las misiones finales. Si cambias las estadísticas de `GameData`, ejecuta las pruebas para confirmar que el balance sigue en rango.
