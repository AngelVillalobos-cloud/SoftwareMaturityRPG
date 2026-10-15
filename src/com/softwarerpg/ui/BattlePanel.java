package com.softwarerpg.ui;

import com.softwarerpg.engine.BattleEngine;
import com.softwarerpg.engine.GameEngine;
import com.softwarerpg.model.Hero;
import com.softwarerpg.model.Mission;
import com.softwarerpg.model.Enemy;
import com.softwarerpg.model.TSPRole;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Panel de combate RPG donde el jugador y su equipo se enfrentan a anomalías de software.
 */
public class BattlePanel extends JPanel {
    private final GameEngine gameEngine;
    private final Runnable onStateUpdated;

    private JProgressBar heroHpBar;
    private JProgressBar heroMpBar;
    private JLabel heroStatusLabel;
    private JLabel heroAuxLabel;

    private JLabel enemyNameLabel;
    private JLabel enemySubtitleLabel;
    private JProgressBar enemyHpBar;
    private JLabel enemyQuoteLabel;

    private CharacterSprite heroSprite;
    private CharacterSprite clientSprite;

    private JTextArea battleLogArea;
    private JScrollPane logScrollPane;

    // Botones de acción
    private JButton btnCode;
    private JButton btnInspect;
    private JButton btnEstimate;
    private JButton btnSynergy;
    private JButton btnStatControl;
    private JButton btnContinuous;

    public BattlePanel(GameEngine gameEngine, Runnable onStateUpdated) {
        this.gameEngine = gameEngine;
        this.onStateUpdated = onStateUpdated;
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        initComponents();
    }

    private void initComponents() {
        // --- Panel Superior: Arena (Héroe vs Enemigo) ---
        JPanel arenaPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        arenaPanel.setOpaque(false);

        // Tarjeta Héroe
        JPanel heroCard = Theme.createCard("👨‍💻 Ingeniero de Software (Tú)");
        JPanel heroContent = new JPanel(new GridLayout(5, 1, 4, 4));
        heroContent.setOpaque(false);

        heroStatusLabel = new JLabel("Nivel " + gameEngine.getHero().getLevel() + " | " + gameEngine.getHero().getPspLevel().getTitle());
        heroStatusLabel.setForeground(Theme.TEXT_PRIMARY);
        heroStatusLabel.setFont(Theme.FONT_BOLD);

        heroHpBar = new JProgressBar(0, gameEngine.getHero().getMaxHp());
        heroHpBar.setValue(gameEngine.getHero().getHp());
        heroHpBar.setStringPainted(true);
        heroHpBar.setForeground(Theme.ACCENT_GREEN);
        heroHpBar.setBackground(Theme.PANEL_SECONDARY);

        heroMpBar = new JProgressBar(0, gameEngine.getHero().getMaxMp());
        heroMpBar.setValue(gameEngine.getHero().getMp());
        heroMpBar.setStringPainted(true);
        heroMpBar.setForeground(Theme.ACCENT_CYAN);
        heroMpBar.setBackground(Theme.PANEL_SECONDARY);

        heroAuxLabel = new JLabel("📋 Checklists: " + gameEngine.getHero().getChecklists() + " | 📑 Plantillas: " + gameEngine.getHero().getDesignTemplates());
        heroAuxLabel.setForeground(Theme.TEXT_MUTED);
        heroAuxLabel.setFont(Theme.FONT_REGULAR);

        heroContent.add(heroStatusLabel);
        heroContent.add(new JLabel("Salud del Proyecto (HP):") {{ setForeground(Theme.TEXT_MUTED); }});
        heroContent.add(heroHpBar);
        heroContent.add(heroMpBar);
        heroContent.add(heroAuxLabel);
        heroCard.add(heroContent, BorderLayout.CENTER);
        heroSprite = new CharacterSprite(CharacterSprite.Kind.ENGINEER);
        heroCard.add(heroSprite, BorderLayout.WEST);

        // Tarjeta Enemigo / Bug
        JPanel enemyCard = Theme.createCard("👾 Defecto / Amenaza  vs  😠 Cliente");
        JPanel enemyContent = new JPanel(new GridLayout(4, 1, 4, 4));
        enemyContent.setOpaque(false);

        enemyNameLabel = new JLabel("Sin enemigo activo");
        enemyNameLabel.setForeground(Theme.ACCENT_RED);
        enemyNameLabel.setFont(Theme.FONT_HEADER);

        enemySubtitleLabel = new JLabel("Selecciona una misión en la pestaña Campaña");
        enemySubtitleLabel.setForeground(Theme.TEXT_MUTED);
        enemySubtitleLabel.setFont(Theme.FONT_REGULAR);

        enemyHpBar = new JProgressBar(0, 100);
        enemyHpBar.setValue(0);
        enemyHpBar.setStringPainted(true);
        enemyHpBar.setForeground(Theme.ACCENT_RED);
        enemyHpBar.setBackground(Theme.PANEL_SECONDARY);

        enemyQuoteLabel = new JLabel("Esperando inicio de proyecto...");
        enemyQuoteLabel.setForeground(Theme.ACCENT_AMBER);
        enemyQuoteLabel.setFont(Theme.FONT_REGULAR);

        enemyContent.add(enemyNameLabel);
        enemyContent.add(enemySubtitleLabel);
        enemyContent.add(enemyHpBar);
        enemyContent.add(enemyQuoteLabel);
        enemyCard.add(enemyContent, BorderLayout.CENTER);
        clientSprite = new CharacterSprite(CharacterSprite.Kind.CLIENT_MAN);
        enemyCard.add(clientSprite, BorderLayout.EAST);

        arenaPanel.add(heroCard);
        arenaPanel.add(enemyCard);
        add(arenaPanel, BorderLayout.NORTH);

        // --- Panel Central: Consola de Combate ---
        JPanel consolePanel = Theme.createCard("📜 Consola de Depuración & Eventos RPG");
        battleLogArea = new JTextArea();
        battleLogArea.setEditable(false);
        battleLogArea.setBackground(new Color(15, 18, 24));
        battleLogArea.setForeground(Theme.TEXT_PRIMARY);
        battleLogArea.setFont(Theme.FONT_MONO);
        battleLogArea.setLineWrap(true);
        battleLogArea.setWrapStyleWord(true);
        logScrollPane = new JScrollPane(battleLogArea);
        logScrollPane.setBorder(new LineBorder(Theme.BORDER_COLOR, 1));
        consolePanel.add(logScrollPane, BorderLayout.CENTER);
        add(consolePanel, BorderLayout.CENTER);

        // --- Panel Inferior: Barra de Habilidades / Acciones ---
        JPanel actionsCard = Theme.createCard("⚡ Acciones de Proceso e Ingeniería");
        JPanel buttonsGrid = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonsGrid.setOpaque(false);

        btnCode = Theme.createStyledButton("💻 Codificar (Ataque)", Theme.ACCENT_CYAN);
        btnCode.setToolTipText("Escribe código y daña al defecto (22 + 5 × tu nivel). Con PSP bajo puedes inyectar defectos.");
        btnInspect = Theme.createStyledButton("📋 Checklist PSP (Inspección)", Theme.ACCENT_GREEN);
        btnEstimate = Theme.createStyledButton("📐 Estimación LOC (PSP 1)", Theme.ACCENT_AMBER);
        btnSynergy = Theme.createStyledButton("🤝 Sinergia TSP (Equipo)", Theme.ACCENT_PURPLE);
        btnStatControl = Theme.createStyledButton("📊 Control Estadístico (CMMI-4)", Theme.ACCENT_CYAN);
        btnContinuous = Theme.createStyledButton("🚀 Mejora Continua (CMMI-5)", Theme.ACCENT_GREEN);

        btnCode.addActionListener(e -> executeAction(() -> gameEngine.getCurrentBattle().actionWriteCode()));
        btnInspect.addActionListener(e -> executeAction(() -> gameEngine.getCurrentBattle().actionInspectCode()));
        btnEstimate.addActionListener(e -> executeAction(() -> gameEngine.getCurrentBattle().actionEstimateSize()));
        btnSynergy.addActionListener(e -> showSynergyMenu());
        btnStatControl.addActionListener(e -> executeAction(() -> gameEngine.getCurrentBattle().actionStatisticalControl()));
        btnContinuous.addActionListener(e -> executeAction(() -> gameEngine.getCurrentBattle().actionContinuousImprovement()));

        buttonsGrid.add(btnCode);
        buttonsGrid.add(btnInspect);
        buttonsGrid.add(btnEstimate);
        buttonsGrid.add(btnSynergy);
        buttonsGrid.add(btnStatControl);
        buttonsGrid.add(btnContinuous);

        actionsCard.add(buttonsGrid, BorderLayout.CENTER);
        add(actionsCard, BorderLayout.SOUTH);

        updateBattleUI();
    }

    private void executeAction(Runnable action) {
        if (gameEngine.getCurrentBattle() == null) {
            JOptionPane.showMessageDialog(this,
                "No hay un proyecto o batalla activa.\nVe a la pestaña 'Campaña' para seleccionar un proyecto.",
                "Sin Proyecto Activo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (gameEngine.getCurrentBattle().isBattleOver()) {
            JOptionPane.showMessageDialog(this,
                "Este combate ha finalizado. Selecciona otro en la pestaña 'Campaña'.",
                "Combate Terminado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        action.run();
        updateBattleUI();
        onStateUpdated.run();
    }

    private void showSynergyMenu() {
        if (gameEngine.getCurrentBattle() == null || gameEngine.getCurrentBattle().isBattleOver()) {
            JOptionPane.showMessageDialog(this, "No hay batalla activa.");
            return;
        }

        JPopupMenu menu = new JPopupMenu();
        for (TSPRole role : TSPRole.values()) {
            JMenuItem item = new JMenuItem(role.getTitle() + "  →  " + synergyEffect(role));
            item.setToolTipText(role.getDescription());
            item.setFont(Theme.FONT_REGULAR);
            item.addActionListener(e -> executeAction(() -> gameEngine.getCurrentBattle().actionTeamSynergy(role)));
            menu.add(item);
        }
        menu.show(btnSynergy, 0, -menu.getPreferredSize().height);
    }

    /** Efecto real de cada rol TSP en combate (lo que el jugador necesita saber para decidir). */
    private static String synergyEffect(TSPRole role) {
        switch (role) {
            case LIDER_EQUIPO: return "Recupera +35 HP y +25 MP";
            case GESTOR_DESARROLLO: return "Daño fuerte (45 + 5 × nivel CO)";
            case GESTOR_PLANIFICACION: return "Tu próximo ataque hace +40% de daño";
            case GESTOR_CALIDAD: return "Inspección por pares: +20 HP y 25 de daño";
            case GESTOR_REQUERIMIENTOS: return "Fija la línea base de configuración";
            default: return role.getSkillEffect();
        }
    }

    public void logMessage(String msg) {
        battleLogArea.append(msg + "\n");
        battleLogArea.setCaretPosition(battleLogArea.getDocument().getLength());
    }

    private static final int COST_ESTIMATE = 15;
    private static final int COST_SYNERGY = 20;
    private static final int COST_STAT = 25;
    private static final int COST_CONTINUOUS = 30;

    /** Textos de los botones con su costo para que el jugador sepa qué puede usar. */
    private void updateActionLabels(Hero hero) {
        int cmmi = hero.getCmmiLevel().getLevelNumber();
        btnInspect.setText("📋 Checklist PSP (Curar) · quedan " + hero.getChecklists());
        btnInspect.setToolTipText("Revisión con lista de chequeo: recupera salud. Consume 1 checklist (cómpralas en la Campaña).");
        btnEstimate.setText("📐 Estimación LOC · " + COST_ESTIMATE + " MP");
        btnEstimate.setToolTipText("Planeación PSP 1: tu próximo ataque hace +40% de daño y recuperas 25 MP.");
        btnSynergy.setText("🤝 Sinergia TSP · " + COST_SYNERGY + " MP");
        btnSynergy.setToolTipText("Pide ayuda a un rol de tu equipo TSP (elige en el menú).");
        btnStatControl.setText(cmmi >= 4 ? "📊 Control Estadístico · " + COST_STAT + " MP" : "🔒 Control Estadístico (requiere CMMI 4)");
        btnStatControl.setToolTipText("CMMI 4: ataque certero basado en control estadístico de tus procesos.");
        btnContinuous.setText(cmmi >= 5 ? "🚀 Mejora Continua · " + COST_CONTINUOUS + " MP" : "🔒 Mejora Continua (requiere CMMI 5)");
        btnContinuous.setToolTipText("CMMI 5: +50 HP y regeneras +15 HP durante 3 turnos.");
    }

    private void updateSprites(Hero hero, BattleEngine battle) {
        double heroRatio = (double) hero.getHp() / hero.getMaxHp();
        String heroLine;
        if (battle == null) heroLine = "¿Siguiente proyecto?";
        else if (hero.getHp() <= 0) heroLine = "Necesito un respiro...";
        else if (heroRatio < 0.35) heroLine = "¡Necesito café!";
        else if (battle.isBattleOver()) heroLine = "¡Entregado!";
        else heroLine = "¡A programar!";
        heroSprite.setState(heroRatio, hero.getHp() <= 0, heroLine);

        if (battle == null) {
            clientSprite.setState(0.4, false, "Esperando propuesta...");
            clientSprite.setToolTipText(null);
            return;
        }
        Enemy enemy = battle.getEnemy();
        // Señor o señora según el defecto, de forma estable entre combates
        clientSprite.setKind((enemy.getName().hashCode() & 1) == 0
                ? CharacterSprite.Kind.CLIENT_MAN : CharacterSprite.Kind.CLIENT_WOMAN);
        double anger = (double) enemy.getHp() / enemy.getMaxHp();
        String clientLine;
        if (enemy.isDefeated()) clientLine = "¡Excelente entrega!";
        else if (hero.getHp() <= 0) clientLine = "¡Cancelo el contrato!";
        else if (anger > 0.66) clientLine = "¡Lo quería para AYER!";
        else if (anger > 0.33) clientLine = "¿Ya mero queda o qué?";
        else clientLine = "Mmm... ya se ve mejor.";
        clientSprite.setState(anger, enemy.isDefeated(), clientLine);
        Mission mission = gameEngine.getActiveMission();
        clientSprite.setToolTipText(mission != null ? "Cliente: " + mission.getClient() : "Cliente de caza rápida");
    }

    public void clearLog() {
        battleLogArea.setText("");
    }

    public void updateBattleUI() {
        Hero hero = gameEngine.getHero();
        heroHpBar.setMaximum(hero.getMaxHp());
        heroHpBar.setValue(hero.getHp());
        heroHpBar.setString(hero.getHp() + " / " + hero.getMaxHp() + " HP");

        heroMpBar.setMaximum(hero.getMaxMp());
        heroMpBar.setValue(hero.getMp());
        heroMpBar.setString(hero.getMp() + " / " + hero.getMaxMp() + " MP");

        heroStatusLabel.setText("Nivel " + hero.getLevel() + " | " + hero.getPspLevel().getTitle() + " | CMMI: " + hero.getCmmiLevel().getName());
        heroAuxLabel.setText("📋 Checklists: " + hero.getChecklists() + " | 📑 Plantillas: " + hero.getDesignTemplates());
        updateActionLabels(hero);

        BattleEngine battle = gameEngine.getCurrentBattle();
        updateSprites(hero, battle);
        if (battle != null) {
            Enemy enemy = battle.getEnemy();
            enemyNameLabel.setText(enemy.getName() + " (" + enemy.getAssociatedDefect().getName() + ")");
            enemySubtitleLabel.setText(enemy.getSubtitle());
            enemyHpBar.setMaximum(enemy.getMaxHp());
            enemyHpBar.setValue(enemy.getHp());
            enemyHpBar.setString(enemy.getHp() + " / " + enemy.getMaxHp() + " HP");
            enemyQuoteLabel.setText("\"" + enemy.getIntroQuote() + "\"");

            boolean active = !battle.isBattleOver();
            int cmmi = hero.getCmmiLevel().getLevelNumber();
            btnCode.setEnabled(active);
            btnInspect.setEnabled(active && hero.getChecklists() > 0);
            btnEstimate.setEnabled(active && hero.getMp() >= COST_ESTIMATE);
            btnSynergy.setEnabled(active && hero.getMp() >= COST_SYNERGY);
            btnStatControl.setEnabled(active && cmmi >= 4 && hero.getMp() >= COST_STAT);
            btnContinuous.setEnabled(active && cmmi >= 5 && hero.getMp() >= COST_CONTINUOUS);
        } else {
            enemyNameLabel.setText("Sin enemigo activo");
            enemySubtitleLabel.setText("Selecciona un proyecto en la pestaña Campaña");
            enemyHpBar.setMaximum(100);
            enemyHpBar.setValue(0);
            enemyHpBar.setString("0 / 0 HP");
            enemyQuoteLabel.setText("En espera de asignación de proyecto...");

            btnCode.setEnabled(false);
            btnInspect.setEnabled(false);
            btnEstimate.setEnabled(false);
            btnSynergy.setEnabled(false);
            btnStatControl.setEnabled(false);
            btnContinuous.setEnabled(false);
        }
    }
}

