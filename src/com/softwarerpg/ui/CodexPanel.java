package com.softwarerpg.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Enciclopedia y Guía Didáctica interactiva con los conceptos de los 4 PDFs de clase:
 * CMMI, MoProSoft, PSP y TSP.
 */
public class CodexPanel extends JPanel {

    public CodexPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        initComponents();
    }

    private void initComponents() {
        JTabbedPane codexTabs = new JTabbedPane();
        Theme.applyDarkTabbedPane(codexTabs);

        codexTabs.addTab("🏛️ CMMI (Niveles 1-5)", createScrollPane(getCmmiText()));
        codexTabs.addTab("🇲🇽 MoProSoft (NMX-I-059)", createScrollPane(getMoProSoftText()));
        codexTabs.addTab("👤 PSP (Proceso Personal)", createScrollPane(getPspText()));
        codexTabs.addTab("👥 TSP (Proceso en Equipo)", createScrollPane(getTspText()));

        add(codexTabs, BorderLayout.CENTER);
    }

    private JScrollPane createScrollPane(String content) {
        JTextPane tp = new JTextPane();
        tp.setContentType("text/html");
        tp.setText("<html><body style='font-family: Segoe UI, sans-serif; background-color: #1E2430; color: #E5E7EB; padding: 15px;'>" + content + "</body></html>");
        tp.setEditable(false);
        tp.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(tp);
        scroll.setBorder(new LineBorder(Theme.BORDER_COLOR, 1));
        return scroll;
    }

    private String getCmmiText() {
        return "<h2 style='color: #38BDF8;'>Capability Maturity Model Integration (CMMI)</h2>"
                + "<p>Modelo de madurez de capacidades de integración que evalúa y mejora los procesos organizacionales en 5 niveles:</p>"
                + "<ul>"
                + "<li><b style='color: #F87171;'>Nivel 1: Inicial</b> - Procesos ad-hoc y caóticos. El éxito depende de las habilidades individuales (heroísmo). Frecuentemente exceden presupuesto y calendario. En momentos de crisis se abandonan los procesos. Resultado: Menor calidad / Alto riesgo.</li>"
                + "<li><b style='color: #60A5FA;'>Nivel 2: Gestionado (Administrado)</b> - Gestión básica de proyectos. Requisitos gestionados, procesos planificados, realizados, medidos y controlados. Los compromisos y productos de trabajo se revisan con partes interesadas. Las prácticas se conservan en momentos de estrés. Resultado: Baja calidad / Alto riesgo.</li>"
                + "<li><b style='color: #4ADE80;'>Nivel 3: Definido</b> - Procesos estandarizados en toda la organización. Se describen en normas, procedimientos, herramientas y métodos. Gestión proactiva de procesos y gestión de riesgos. Los proyectos adaptan el proceso estándar institucional. Resultado: Calidad media / Riesgo medio.</li>"
                + "<li><b style='color: #C084FC;'>Nivel 4: Cuantitativamente Gestionado</b> - Subprocesos controlados mediante técnicas estadísticas y cuantitativas. Alta previsibilidad de resultados. Se identifican y corrigen las <i>Causas Especiales de Variación</i>. Resultado: Mayor calidad / Menor riesgo.</li>"
                + "<li><b style='color: #FBBF24;'>Nivel 5: Optimización</b> - Foco en la mejora continua de procesos e innovaciones tecnológicas. Se analizan y gestionan las <i>Causas Comunes de Variación</i> inherentes a los procesos. Resultado: Máxima calidad / Menor riesgo.</li>"
                + "</ul>";
    }

    private String getMoProSoftText() {
        return "<h2 style='color: #4ADE80;'>MoProSoft (Modelo de Procesos para la Industria de Software)</h2>"
                + "<p>Desarrollado a solicitud de la Secretaría de Economía para servir de base a la <b>Norma Mexicana NMX-I-059/02-NYCE</b> en convenio con la Facultad de Ciencias de la UNAM. Tomó como base ISO 9000:2000, CMM niveles 2 y 3, PMBOK y SWEBOK.</p>"
                + "<h3>Estructura en 3 Categorías:</h3>"
                + "<ol>"
                + "<li><b style='color: #38BDF8;'>Alta Dirección (DIR):</b> Proceso de <i>Gestión de Negocios</i>. Establece los lineamientos estratégicos para Gerencia y recibe retroalimentación de metas.</li>"
                + "<li><b style='color: #FBBF24;'>Gerencia (GER):</b> <i>Gestión de Procesos</i>, <i>Gestión de Proyectos</i> y <i>Gestión de Recursos</i>:<br/>"
                + "&nbsp;&nbsp;• <b>RH:</b> Recursos Humanos.<br/>"
                + "&nbsp;&nbsp;• <b>BSI:</b> Bienes, Servicios e Infraestructura.<br/>"
                + "&nbsp;&nbsp;• <b>CO:</b> Conocimiento de la Organización.</li>"
                + "<li><b style='color: #C084FC;'>Operación (OPE):</b> <i>Administración de Proyectos Específicos</i> y <i>Desarrollo y Mantenimiento de Software</i>. Realiza las actividades técnicas directas.</li>"
                + "</ol>"
                + "<h3>Roles MoProSoft:</h3>"
                + "<p>• <b>Cliente:</b> Solicita el software y financia el proyecto.<br/>"
                + "• <b>Usuario:</b> Utilizará el producto de software.<br/>"
                + "• <b>Grupo Directivo:</b> Dirigen la organización y son responsables de su funcionamiento.<br/>"
                + "• <b>Responsable de Proceso:</b> Asegura la realización de las prácticas del proceso.<br/>"
                + "• <b>Involucrado:</b> Analistas, Programadores, Revisores, etc.</p>";
    }

    private String getPspText() {
        return "<h2 style='color: #C084FC;'>Personal Software Process (PSP) - Watts Humphrey</h2>"
                + "<p>Marco de trabajo disciplinado que ayuda a los ingenieros de software a medir, planificar y mejorar su propia forma de trabajar. Autor: Watts S. Humphrey (1997).</p>"
                + "<h3>Niveles de Madurez del PSP:</h3>"
                + "<ul>"
                + "<li><b>PSP 0:</b> Proceso actual, registro de tiempos y registro de defectos.</li>"
                + "<li><b>PSP 0.1:</b> Estándares de código, medición de tamaño y Plan de Mejora Personal (PIP).</li>"
                + "<li><b>PSP 1:</b> Estimación de tamaño (LOC) y reporte formal de pruebas.</li>"
                + "<li><b>PSP 1.1:</b> Calendario de planeación de tareas y presupuesto semanal de tiempo.</li>"
                + "<li><b>PSP 2:</b> Revisión rigurosa de diseño y código con listas de chequeo (checklists). Prevención de defectos.</li>"
                + "<li><b>PSP 2.1:</b> Plantillas de diseño y técnicas formales de análisis.</li>"
                + "</ul>"
                + "<h3>Fórmula de Densidad de Defectos:</h3>"
                + "<p style='font-size: 14px; background: #262E3E; padding: 8px; border-left: 4px solid #38BDF8;'>"
                + "<b>Dd = 1000 * (D / N)</b><br/>"
                + "Donde: <b>Dd</b> = Defectos por KLOC, <b>D</b> = Defectos encontrados, <b>N</b> = Líneas de código (LOC) nuevas o modificadas.<br/>"
                + "<i>Un ingeniero de software experimentado suele introducir entre 50 y 250 defectos/KLOC.</i>"
                + "</p>"
                + "<h3>Tipos de Defectos (Clasificación Chillagere / IBM):</h3>"
                + "<p>10: Documentación | 20: Sintaxis | 30: Construir/Paquetes | 40: Asignación | 50: Interfaz | 60: Chequeo | 70: Datos | 80: Función/Lógica | 90: Sistema | 100: Entorno.</p>";
    }

    private String getTspText() {
        return "<h2 style='color: #FBBF24;'>Team Software Process (TSP)</h2>"
                + "<p>Método de establecimiento y mejora del trabajo en equipo para procesos de software desarrollado por Watts Humphrey (1996).</p>"
                + "<h3>Los 5 Roles Clave del TSP:</h3>"
                + "<ul>"
                + "<li><b style='color: #38BDF8;'>1. Líder del Equipo:</b> Dirige al equipo, asegura que todos reporten sus datos de procesos y completen su trabajo conforme al plan. Realiza reportes semanales de avance.</li>"
                + "<li><b style='color: #4ADE80;'>2. Gestor de Desarrollo:</b> Guía al equipo en el diseño arquitectónico y desarrollo técnico del producto.</li>"
                + "<li><b style='color: #FBBF24;'>3. Gestor de Planificación:</b> Apoya y guía al equipo en la planificación, estimación de esfuerzo y calendarización del trabajo.</li>"
                + "<li><b style='color: #C084FC;'>4. Gestor de Calidad / Proceso:</b> Define necesidades del proceso, administra el plan de calidad, genera estándares, modera inspecciones y revisa artefactos.</li>"
                + "<li><b style='color: #F87171;'>5. Administrador de Requerimientos / Soporte:</b> Dirige el desarrollo de requerimientos, soporte tecnológico y administra el plan de gestión de configuración.</li>"
                + "</ul>"
                + "<h3>Ciclo de Vida del TSP:</h3>"
                + "<p>Lanzamiento (Launch: Metas, Asignar Roles, Estimar Esfuerzo, Evaluar Riesgos) &rarr; Estrategia &rarr; Planeación &rarr; Requerimientos &rarr; Diseño &rarr; Implementación &rarr; Pruebas &rarr; Post-Mortem y Juntas Semanales.</p>"
                + "<p><b>Condición fundamental:</b> Cada miembro del equipo de ingenieros debe estar entrenado formalmente en <b>PSP</b>.</p>";
    }
}

