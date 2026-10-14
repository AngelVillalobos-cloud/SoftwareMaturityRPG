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
- **Bitácora de Defectos (Defect Log):** Clasifica los bugs con la taxonomía de **Chillagere / IBM** (Tipos 10 Documentación, 20 Sintaxis, 30 Construcción, 40 Asignación, 50 Interfaz, 60 Chequeo, 70 Datos, 80 Función/Lógica, 90 Sistema, 100 Entorno).
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

### Opción 1: Con el lanzador directo (Windows)
Haz doble clic sobre el archivo:
- `run_game.bat` (ejecuta el juego ya compilado)
- `build_and_run.bat` (recompila y ejecuta)

### Opción 2: Desde la Terminal / PowerShell
```powershell
# Compilar:
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName)

# Ejecutar:
java -cp bin com.softwarerpg.Main
```

