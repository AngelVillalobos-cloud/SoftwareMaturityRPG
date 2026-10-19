package com.softwarerpg.ui;

import com.softwarerpg.engine.GameEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enciclopedia y Centro de Aprendizaje Interactivo:
 * 1. Biblioteca Teórica (CMMI, MoProSoft, PSP, TSP).
 * 2. Anki de Calidad de Software (Flashcards de Repaso Activo).
 * 3. Quiz RPG / Desafío de Procesos (Minijuego de preguntas y respuestas con recompensas reales).
 */
@SuppressWarnings("serial")
public final class CodexPanel extends JPanel {

    private final GameEngine gameEngine;
    private final Runnable onStateUpdated;

    public CodexPanel() {
        this(null, null);
    }

    public CodexPanel(GameEngine gameEngine, Runnable onStateUpdated) {
        this.gameEngine = gameEngine;
        this.onStateUpdated = onStateUpdated;

        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        initComponents();
    }

    private void initComponents() {
        JTabbedPane codexTabs = new JTabbedPane();
        Theme.applyDarkTabbedPane(codexTabs);

        codexTabs.addTab("📚 Biblioteca Teórica", createTheoryPanel());
        codexTabs.addTab("🃏 Anki: Flashcards de Calidad", createAnkiPanel());
        codexTabs.addTab("⚔️ Quiz RPG: Desafío de Procesos", createQuizPanel());

        add(codexTabs, BorderLayout.CENTER);
    }

    // =========================================================================
    // 1. BIBLIOTECA TEÓRICA
    // =========================================================================
    private JComponent createTheoryPanel() {
        JTabbedPane theoryTabs = new JTabbedPane();
        Theme.applyDarkTabbedPane(theoryTabs);

        theoryTabs.addTab("🏛️ CMMI (Niveles 1-5)", createScrollPane(getCmmiText()));
        theoryTabs.addTab("🇲🇽 MoProSoft (NMX-I-059)", createScrollPane(getMoProSoftText()));
        theoryTabs.addTab("👤 PSP (Proceso Personal)", createScrollPane(getPspText()));
        theoryTabs.addTab("👥 TSP (Proceso en Equipo)", createScrollPane(getTspText()));

        return theoryTabs;
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

    // =========================================================================
    // 2. ANKI FLASHCARDS INTERACTIVAS
    // =========================================================================
    private static class Flashcard {
        final String category;
        final String frontQuestion;
        final String backAnswer;

        Flashcard(String category, String frontQuestion, String backAnswer) {
            this.category = category;
            this.frontQuestion = frontQuestion;
            this.backAnswer = backAnswer;
        }
    }

    private int currentCardIndex = 0;
    private boolean cardShowingBack = false;
    private final List<Flashcard> flashcards = createFlashcardsList();

    private JLabel lblCardCategory;
    private JTextPane txtCardContent;
    private JLabel lblCardProgress;
    private JButton btnFlipCard;
    private JButton btnLearned;

    private JComponent createAnkiPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Theme.BG_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Cabecera pedagógica
        JPanel headerCard = Theme.createCard("🃏 Repaso Activo con Flashcards (Estilo Anki)");
        JLabel info = new JLabel("<html><body style='color:#D1D5DB;'>"
                + "Aprende jugando: Lee la pregunta, piensa tu respuesta y haz clic en <b>Voltear Tarjeta</b>. "
                + "Si ya la dominas, presiona <b>✅ ¡Entendido!</b> para ganar XP y Oro en el juego."
                + "</body></html>");
        info.setFont(Theme.FONT_REGULAR);
        headerCard.add(info, BorderLayout.CENTER);
        panel.add(headerCard, BorderLayout.NORTH);

        // Tarjeta central interactiva
        JPanel cardDisplay = new JPanel(new BorderLayout(10, 10));
        cardDisplay.setBackground(Theme.PANEL_BG);
        cardDisplay.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.ACCENT_CYAN, 2, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        lblCardCategory = new JLabel("Categoría: CMMI");
        lblCardCategory.setFont(Theme.FONT_HEADER);
        lblCardCategory.setForeground(Theme.ACCENT_AMBER);
        cardDisplay.add(lblCardCategory, BorderLayout.NORTH);

        txtCardContent = new JTextPane();
        txtCardContent.setContentType("text/html");
        txtCardContent.setEditable(false);
        txtCardContent.setBackground(Theme.PANEL_BG);
        txtCardContent.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cardDisplay.add(txtCardContent, BorderLayout.CENTER);

        panel.add(cardDisplay, BorderLayout.CENTER);

        // Barra inferior de controles
        JPanel bottomBar = new JPanel(new BorderLayout(10, 5));
        bottomBar.setOpaque(false);

        lblCardProgress = new JLabel("Tarjeta 1 de " + flashcards.size());
        lblCardProgress.setFont(Theme.FONT_BOLD);
        lblCardProgress.setForeground(Theme.TEXT_MUTED);
        bottomBar.add(lblCardProgress, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnPrev = Theme.createStyledButton("⬅️ Anterior", Theme.PANEL_SECONDARY);
        btnPrev.addActionListener(e -> prevCard());

        btnFlipCard = Theme.createStyledButton("🔄 Voltear Tarjeta", Theme.ACCENT_CYAN);
        btnFlipCard.addActionListener(e -> flipCard());

        JButton btnNext = Theme.createStyledButton("Siguiente ➡️", Theme.PANEL_SECONDARY);
        btnNext.addActionListener(e -> nextCard());

        btnLearned = Theme.createStyledButton("✅ ¡Entendido! (+20 XP, +15 Oro)", Theme.ACCENT_GREEN);
        btnLearned.addActionListener(e -> markCardLearned());

        btnPanel.add(btnPrev);
        btnPanel.add(btnFlipCard);
        btnPanel.add(btnNext);
        btnPanel.add(btnLearned);
        bottomBar.add(btnPanel, BorderLayout.EAST);

        panel.add(bottomBar, BorderLayout.SOUTH);

        updateCardDisplay();
        return panel;
    }

    private void updateCardDisplay() {
        Flashcard card = flashcards.get(currentCardIndex);
        lblCardCategory.setText("📌 " + card.category);
        lblCardProgress.setText("Tarjeta " + (currentCardIndex + 1) + " de " + flashcards.size());

        if (!cardShowingBack) {
            btnFlipCard.setText("🔄 Voltear Tarjeta (Ver Respuesta)");
            btnFlipCard.setBackground(Theme.ACCENT_CYAN.darker());
            txtCardContent.setText("<html><body style='font-family: Segoe UI, sans-serif; color: #F3F4F6; font-size: 14px; padding: 10px;'>"
                    + "<b style='color:#38BDF8;'>PREGUNTA:</b><br><br>"
                    + "<span style='font-size: 16px;'>" + card.frontQuestion + "</span>"
                    + "</body></html>");
        } else {
            btnFlipCard.setText("🔄 Ver Pregunta");
            btnFlipCard.setBackground(Theme.ACCENT_AMBER.darker());
            txtCardContent.setText("<html><body style='font-family: Segoe UI, sans-serif; color: #F3F4F6; font-size: 14px; padding: 10px;'>"
                    + "<b style='color:#4ADE80;'>RESPUESTA Y EXPLICACIÓN:</b><br><br>"
                    + "<span style='font-size: 15px; color: #A7F3D0;'>" + card.backAnswer + "</span>"
                    + "</body></html>");
        }
    }

    private void flipCard() {
        cardShowingBack = !cardShowingBack;
        updateCardDisplay();
        Sound.play(Sound.Sfx.POWER);
    }

    private void prevCard() {
        if (currentCardIndex > 0) {
            currentCardIndex--;
            cardShowingBack = false;
            updateCardDisplay();
        }
    }

    private void nextCard() {
        if (currentCardIndex < flashcards.size() - 1) {
            currentCardIndex++;
            cardShowingBack = false;
            updateCardDisplay();
        }
    }

    private void markCardLearned() {
        if (gameEngine != null) {
            gameEngine.awardCodexReward(20, 15);
            Sound.play(Sound.Sfx.COIN);
            JOptionPane.showMessageDialog(this,
                "¡Excelente aprendizaje!\nGanas +20 XP de Ingeniero y +15 Monedas de Oro para tu empresa.",
                "¡Concepto Aprendido!", JOptionPane.INFORMATION_MESSAGE);
            if (onStateUpdated != null) onStateUpdated.run();
        }
        nextCard();
    }

    private static List<Flashcard> createFlashcardsList() {
        List<Flashcard> list = new ArrayList<>();
        list.add(new Flashcard("CMMI",
            "¿Cuáles son los 5 niveles de madurez de CMMI en orden?",
            "1. <b>Inicial</b> (caótico y ad-hoc)<br>2. <b>Gestionado</b> (básica de gestión, requisitos documentados)<br>3. <b>Definido</b> (estandarizado en la organización, proactivo)<br>4. <b>Cuantitativamente Gestionado</b> (control estadístico, variación especial)<br>5. <b>Optimización</b> (mejora continua, causas comunes)."));

        list.add(new Flashcard("CMMI",
            "¿Cuál es la diferencia fundamental entre CMMI Nivel 4 y Nivel 5?",
            "En el <b>Nivel 4</b> se controlan las <i>Causas Especiales de Variación</i> con técnicas estadísticas.<br>En el <b>Nivel 5</b> se controlan las <i>Causas Comunes de Variación</i> y se centra en innovación y mejora continua."));

        list.add(new Flashcard("MoProSoft",
            "¿Cuáles son las 3 categorías de procesos de la norma mexicana MoProSoft (NMX-I-059)?",
            "1. <b>Alta Dirección (DIR):</b> Gestión de Negocios.<br>2. <b>Gerencia (GER):</b> Gestión de Procesos, Proyectos y Recursos (RH, BSI, CO).<br>3. <b>Operación (OPE):</b> Administración de Proyectos Específicos y Desarrollo/Mantenimiento."));

        list.add(new Flashcard("MoProSoft",
            "En MoProSoft Gerencia (GER), ¿cuáles son los 3 recursos administrados?",
            "• <b>RH:</b> Recursos Humanos (capacitación y personas).<br>• <b>BSI:</b> Bienes, Servicios e Infraestructura (equipos, servidores, herramientas).<br>• <b>CO:</b> Conocimiento de la Organización (base de lecciones aprendidas)."));

        list.add(new Flashcard("PSP",
            "¿Quién creó el Personal Software Process (PSP) y cuál es su objetivo?",
            "Fue creado por <b>Watts S. Humphrey (1997)</b> en el SEI. Su objetivo es brindar una metodología estructurada y disciplinada para que cada ingeniero mida, planifique y mejore su propio trabajo."));

        list.add(new Flashcard("PSP",
            "¿Cuál es la fórmula de Densidad de Defectos (Dd) en PSP?",
            "<b>Dd = 1000 × (D / N)</b><br>Donde <b>D</b> es el total de defectos encontrados y <b>N</b> son las Líneas de Código (LOC) nuevas o modificadas. Un ingeniero experimentado introduce entre 50 y 250 defectos/KLOC."));

        list.add(new Flashcard("PSP",
            "¿Qué significa PIP y en qué nivel de PSP se introduce?",
            "<b>PIP = Plan de Mejora Personal</b> (Personal Improvement Plan). Se introduce en <b>PSP 0.1</b> para que el ingeniero registre propuestas concretas de mejora en su proceso de desarrollo."));

        list.add(new Flashcard("PSP",
            "Según la clasificación Chillarege / IBM en PSP, ¿qué defecto corresponde al Tipo 80?",
            "El <b>Tipo 80</b> es <b>Función / Lógica</b>: errores de lógica, punteros nulos, bucles infinitos, recursión y computación."));

        list.add(new Flashcard("TSP",
            "¿Cuáles son los 5 roles clave del Team Software Process (TSP)?",
            "1. <b>Líder del Equipo</b><br>2. <b>Gestor de Desarrollo</b><br>3. <b>Gestor de Planificación</b><br>4. <b>Gestor de Calidad / Proceso</b><br>5. <b>Administrador de Requerimientos y Soporte</b>."));

        list.add(new Flashcard("TSP",
            "¿Cuál es la condición indispensable para los miembros de un equipo TSP?",
            "<b>Cada integrante debe estar formalmente entrenado en PSP</b> para que el equipo hable el mismo lenguaje métrico y mantenga disciplina personal."));

        return list;
    }

    // =========================================================================
    // 3. QUIZ RPG: DESAFÍO DE PROCESOS
    // =========================================================================
    private static class QuizQuestion {
        final String question;
        final String[] options;
        final int correctIndex;
        final String explanation;

        QuizQuestion(String question, String[] options, int correctIndex, String explanation) {
            this.question = question;
            this.options = options;
            this.correctIndex = correctIndex;
            this.explanation = explanation;
        }
    }

    private int quizIndex = 0;
    private int correctStreak = 0;
    private final List<QuizQuestion> quizQuestions = createQuizList();

    private JLabel lblQuizPrompt;
    private JButton[] quizButtons;
    private JLabel lblQuizFeedback;
    private JLabel lblQuizScore;

    private JComponent createQuizPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Theme.BG_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topCard = Theme.createCard("⚔️ Desafío de Procesos (Minijuego de Preguntas RPG)");
        JPanel topContent = new JPanel(new BorderLayout(10, 5));
        topContent.setOpaque(false);

        JLabel info = new JLabel("<html><body style='color:#D1D5DB;'>"
                + "Responde las preguntas de ingeniería de software para obtener <b>+40 XP</b> y <b>+30 Oro</b> en el juego real. "
                + "Aprende de las explicaciones si fallas alguna."
                + "</body></html>");
        info.setFont(Theme.FONT_REGULAR);
        topContent.add(info, BorderLayout.CENTER);

        lblQuizScore = new JLabel("🏆 Racha de Aciertos: 0");
        lblQuizScore.setFont(Theme.FONT_HEADER);
        lblQuizScore.setForeground(Theme.ACCENT_GREEN);
        topContent.add(lblQuizScore, BorderLayout.EAST);

        topCard.add(topContent, BorderLayout.CENTER);
        panel.add(topCard, BorderLayout.NORTH);

        // Pregunta y opciones centrales
        JPanel centerCard = Theme.createCard("Pregunta de Ingeniería");
        JPanel centerContent = new JPanel(new BorderLayout(10, 15));
        centerContent.setOpaque(false);

        lblQuizPrompt = new JLabel("Cargando pregunta...");
        lblQuizPrompt.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblQuizPrompt.setForeground(Theme.ACCENT_CYAN);
        lblQuizPrompt.setBorder(new EmptyBorder(10, 5, 10, 5));
        centerContent.add(lblQuizPrompt, BorderLayout.NORTH);

        JPanel optionsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        optionsGrid.setOpaque(false);
        quizButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            final int optIdx = i;
            quizButtons[i] = Theme.createStyledButton("Opción " + (i + 1), Theme.PANEL_SECONDARY);
            quizButtons[i].setHorizontalAlignment(SwingConstants.LEFT);
            quizButtons[i].addActionListener(e -> checkQuizAnswer(optIdx));
            optionsGrid.add(quizButtons[i]);
        }
        centerContent.add(optionsGrid, BorderLayout.CENTER);

        lblQuizFeedback = new JLabel("Elige una respuesta para continuar.");
        lblQuizFeedback.setFont(Theme.FONT_REGULAR);
        lblQuizFeedback.setForeground(Theme.TEXT_MUTED);
        lblQuizFeedback.setBorder(new EmptyBorder(8, 5, 5, 5));
        centerContent.add(lblQuizFeedback, BorderLayout.SOUTH);

        centerCard.add(centerContent, BorderLayout.CENTER);
        panel.add(centerCard, BorderLayout.CENTER);

        JButton btnNextQuestion = Theme.createStyledButton("Siguiente Pregunta ➡️", Theme.ACCENT_PURPLE);
        btnNextQuestion.addActionListener(e -> loadNextQuestion());
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setOpaque(false);
        bottomBar.add(btnNextQuestion);
        panel.add(bottomBar, BorderLayout.SOUTH);

        loadQuizQuestion();
        return panel;
    }

    private void loadQuizQuestion() {
        QuizQuestion q = quizQuestions.get(quizIndex);
        lblQuizPrompt.setText("<html><body style='width: 700px;'>" + (quizIndex + 1) + ". " + q.question + "</body></html>");
        for (int i = 0; i < 4; i++) {
            quizButtons[i].setText("<html><body style='padding: 4px;'>" + (char)('A' + i) + ") " + q.options[i] + "</body></html>");
            quizButtons[i].setEnabled(true);
            quizButtons[i].setBackground(Theme.PANEL_SECONDARY);
        }
        lblQuizFeedback.setText("Selecciona tu respuesta.");
        lblQuizFeedback.setForeground(Theme.TEXT_MUTED);
    }

    private void checkQuizAnswer(int selected) {
        QuizQuestion q = quizQuestions.get(quizIndex);
        for (JButton b : quizButtons) b.setEnabled(false);

        if (selected == q.correctIndex) {
            correctStreak++;
            Sound.play(Sound.Sfx.VICTORY);
            lblQuizFeedback.setText("<html><b style='color:#4ADE80;'>¡CORRECTO!</b> " + q.explanation + "</html>");
            lblQuizScore.setText("🏆 Racha de Aciertos: " + correctStreak);
            if (gameEngine != null) {
                gameEngine.awardCodexReward(40, 30);
                if (onStateUpdated != null) onStateUpdated.run();
            }
        } else {
            correctStreak = 0;
            Sound.play(Sound.Sfx.ERROR);
            lblQuizFeedback.setText("<html><b style='color:#F87171;'>INCORRECTO.</b> La correcta era la <b>" + (char)('A' + q.correctIndex) + "</b>. " + q.explanation + "</html>");
            lblQuizScore.setText("🏆 Racha de Aciertos: " + correctStreak);
        }
    }

    private void loadNextQuestion() {
        quizIndex = (quizIndex + 1) % quizQuestions.size();
        loadQuizQuestion();
    }

    private static List<QuizQuestion> createQuizList() {
        List<QuizQuestion> list = new ArrayList<>();
        list.add(new QuizQuestion(
            "En CMMI, ¿cuál es la característica primordial del Nivel 1 (Inicial)?",
            new String[]{
                "Procesos controlados con Seis Sigma",
                "Procesos ad-hoc, caóticos y dependientes del heroísmo individual",
                "Gestión de riesgos estandarizada en toda la empresa",
                "Definición institucional de normas y procedimientos"
            },
            1,
            "En CMMI Nivel 1 no hay procesos probados; los éxitos no son repetibles y suelen exceder presupuesto y tiempo."
        ));

        list.add(new QuizQuestion(
            "¿Cuáles son las 3 categorías principales del modelo mexicano MoProSoft (NMX-I-059)?",
            new String[]{
                "Inicio, Desarrollo y Mantenimiento",
                "Planificación, Codificación y Pruebas",
                "Alta Dirección (DIR), Gerencia (GER) y Operación (OPE)",
                "Requisitos, Arquitectura y Calidad"
            },
            2,
            "MoProSoft organiza a la empresa mexicana en DIR (Gestión de Negocios), GER (Recursos RH/BSI/CO, Procesos, Proyectos) y OPE (Operación técnica)."
        ));

        list.add(new QuizQuestion(
            "En PSP, si un proyecto tuvo 15 defectos y 300 LOC reales, ¿cuál es su Densidad de Defectos (Dd = 1000 * D / N)?",
            new String[]{
                "15 def/KLOC",
                "50 def/KLOC",
                "45 def/KLOC",
                "200 def/KLOC"
            },
            1,
            "Dd = 1000 * (15 / 300) = 1000 * 0.05 = 50 def/KLOC. Un desarrollador experimentado promedia entre 50 y 250 def/KLOC."
        ));

        list.add(new QuizQuestion(
            "En TSP, ¿qué rol se encarga de dirigir el diseño arquitectónico y desarrollo técnico del software?",
            new String[]{
                "Líder del Equipo",
                "Administrador de Requerimientos",
                "Gestor de Calidad",
                "Gestor de Desarrollo"
            },
            3,
            "El Gestor de Desarrollo guía técnicamente al equipo en la arquitectura, patrones y codificación del producto."
        ));

        list.add(new QuizQuestion(
            "¿Qué nivel de PSP introduce listas de chequeo (checklists) para revisión rigurosa de diseño y código?",
            new String[]{
                "PSP 0",
                "PSP 1",
                "PSP 2",
                "PSP 0.1"
            },
            2,
            "PSP 2 introduce el manejo de calidad mediante revisión formal de diseño y código con listas de chequeo."
        ));

        list.add(new QuizQuestion(
            "¿Qué tipo de variación se controla en el Nivel 4 de CMMI mediante técnicas estadísticas?",
            new String[]{
                "Causas comunes de variación",
                "Causas especiales de variación",
                "Variación no medible",
                "Variación del mercado financiero"
            },
            1,
            "El Nivel 4 controla causas especiales para lograr procesos predecibles cuantitativamente. El Nivel 5 optimiza las causas comunes."
        ));

        return list;
    }

    // =========================================================================
    // TEXTOS TEÓRICOS DE REFERENCIA
    // =========================================================================
    private String getCmmiText() {
        return "<h2 style='color: #38BDF8;'>Capability Maturity Model Integration (CMMI)</h2>"
                + "<p>Modelo de madurez de capacidades que evalúa y mejora los procesos organizacionales en 5 niveles:</p>"
                + "<ul>"
                + "<li><b style='color: #F87171;'>Nivel 1: Inicial</b> - Procesos ad-hoc y caóticos. El éxito depende del heroísmo individual. Se exceden presupuestos y calendarios. En crisis se abandonan los procesos. (Menor calidad / Alto riesgo).</li>"
                + "<li><b style='color: #60A5FA;'>Nivel 2: Gestionado (Administrado)</b> - Gestión básica de proyectos. Requisitos administrados, planes documentados que se respetan bajo estrés, aseguramiento de la calidad (QA) y gestión de configuración. (Baja calidad / Alto riesgo).</li>"
                + "<li><b style='color: #4ADE80;'>Nivel 3: Definido</b> - Procesos estandarizados en toda la organización. Descritos en normas, procedimientos y métodos proactivos. Gestión de riesgos integrada. (Calidad media / Riesgo medio).</li>"
                + "<li><b style='color: #C084FC;'>Nivel 4: Cuantitativamente Gestionado</b> - Subprocesos clave medidos y controlados con técnicas estadísticas. Se identifican y corrigen las <i>Causas Especiales de Variación</i>. Alto grado de previsibilidad. (Mayor calidad / Menor riesgo).</li>"
                + "<li><b style='color: #FBBF24;'>Nivel 5: Optimización</b> - Foco en mejora continua e innovaciones tecnológicas. Se analizan y erradican las <i>Causas Comunes de Variación</i> inherentes al proceso. (Máxima calidad / Menor riesgo).</li>"
                + "</ul>";
    }

    private String getMoProSoftText() {
        return "<h2 style='color: #4ADE80;'>MoProSoft (Modelo de Procesos para la Industria de Software)</h2>"
                + "<p>Desarrollado a solicitud de la Secretaría de Economía como base para la <b>Norma Mexicana NMX-I-059/02-NYCE</b> en convenio con la UNAM. Diseñado especialmente para PyMEs mexicanas.</p>"
                + "<h3>Estructura en 3 Categorías:</h3>"
                + "<ol>"
                + "<li><b style='color: #38BDF8;'>Alta Dirección (DIR):</b> Proceso de <i>Gestión de Negocios</i>. Define lineamientos estratégicos, presupuesto y metas corporativas.</li>"
                + "<li><b style='color: #FBBF24;'>Gerencia (GER):</b> <i>Gestión de Procesos</i>, <i>Gestión de Proyectos</i> y <i>Gestión de Recursos</i>:<br>"
                + "&nbsp;&nbsp;• <b>RH:</b> Recursos Humanos.<br>"
                + "&nbsp;&nbsp;• <b>BSI:</b> Bienes, Servicios e Infraestructura.<br>"
                + "&nbsp;&nbsp;• <b>CO:</b> Conocimiento de la Organización.</li>"
                + "<li><b style='color: #C084FC;'>Operación (OPE):</b> <i>Administración de Proyectos Específicos</i> y <i>Desarrollo y Mantenimiento de Software</i>. Realiza las actividades técnicas de ciclo de vida.</li>"
                + "</ol>"
                + "<h3>Roles Oficiales:</h3>"
                + "<p>• <b>Cliente:</b> Financia el proyecto.<br>"
                + "• <b>Usuario:</b> Utiliza el producto.<br>"
                + "• <b>Grupo Directivo:</b> Dirige la organización.<br>"
                + "• <b>Responsable de Proceso:</b> Asegura cumplimiento de prácticas.<br>"
                + "• <b>Involucrado:</b> Analistas, Programadores, Revisores.</p>";
    }

    private String getPspText() {
        return "<h2 style='color: #C084FC;'>Personal Software Process (PSP) - Watts Humphrey</h2>"
                + "<p>Marco disciplinado que enseña a los ingenieros a medir, planificar y mejorar su trabajo personal. Creado por Watts S. Humphrey en 1997 (SEI).</p>"
                + "<h3>Niveles de Madurez PSP:</h3>"
                + "<ul>"
                + "<li><b>PSP 0:</b> Proceso actual, registro de tiempos y defectos.</li>"
                + "<li><b>PSP 0.1:</b> Estándares de codificación, medición de tamaño y Plan de Mejora Personal (PIP).</li>"
                + "<li><b>PSP 1:</b> Estimación de tamaño (LOC) y reporte formal de pruebas.</li>"
                + "<li><b>PSP 1.1:</b> Calendario de planeación de tareas y presupuesto semanal.</li>"
                + "<li><b>PSP 2:</b> Revisión rigurosa de diseño y código con listas de chequeo (checklists). Prevención de defectos.</li>"
                + "<li><b>PSP 2.1:</b> Plantillas formales de diseño y técnicas de análisis.</li>"
                + "</ul>"
                + "<h3>Fórmula de Densidad de Defectos:</h3>"
                + "<p style='font-size: 14px; background: #262E3E; padding: 10px; border-left: 4px solid #38BDF8;'>"
                + "<b>Dd = 1000 × (D / N)</b><br>"
                + "<b>D</b> = Defectos encontrados | <b>N</b> = Líneas de código (LOC) nuevas o modificadas.<br>"
                + "<i>Un desarrollador experimentado introduce entre 50 y 250 defectos/KLOC.</i>"
                + "</p>"
                + "<h3>Taxonomía Chillarege / IBM (Tipos de Defecto):</h3>"
                + "<p>10 Doc | 20 Sintaxis | 30 Paquetes/Build | 40 Asignación | 50 Interfaz | 60 Chequeo | 70 Datos | 80 Función/Lógica | 90 Sistema | 100 Entorno.</p>";
    }

    private String getTspText() {
        return "<h2 style='color: #FBBF24;'>Team Software Process (TSP)</h2>"
                + "<p>Metodología de establecimiento y mejora del trabajo en equipo desarrollada por Watts Humphrey (1996).</p>"
                + "<h3>Los 5 Roles Oficiales del TSP:</h3>"
                + "<ul>"
                + "<li><b style='color: #38BDF8;'>1. Líder del Equipo:</b> Dirige al equipo, asegura el reporte de datos de proceso y elabora el reporte semanal de avance.</li>"
                + "<li><b style='color: #4ADE80;'>2. Gestor de Desarrollo:</b> Guía en el diseño arquitectónico y desarrollo técnico del software.</li>"
                + "<li><b style='color: #FBBF24;'>3. Gestor de Planificación:</b> Apoya en la estimación de esfuerzo, cronograma y seguimiento del trabajo.</li>"
                + "<li><b style='color: #C084FC;'>4. Gestor de Calidad / Proceso:</b> Administra el plan de calidad, genera estándares y modera inspecciones.</li>"
                + "<li><b style='color: #F87171;'>5. Administrador de Requerimientos / Soporte:</b> Dirige la especificación de requerimientos y el plan de configuración.</li>"
                + "</ul>"
                + "<p><b>Condición indispensable:</b> Cada miembro debe estar capacitado en <b>PSP</b>.</p>";
    }
}
