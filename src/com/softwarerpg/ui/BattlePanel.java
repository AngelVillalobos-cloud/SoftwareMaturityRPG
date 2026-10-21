package com.softwarerpg.ui;

import com.softwarerpg.engine.BattleEngine;
import com.softwarerpg.engine.GameEngine;
import com.softwarerpg.model.Hero;
import com.softwarerpg.model.Mission;
import com.softwarerpg.model.Enemy;
import com.softwarerpg.model.TSPRole;
import com.softwarerpg.model.TeamMember;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Panel de combate RPG donde el jugador y su equipo se enfrentan a anomalías de software.
 */
@SuppressWarnings("serial") // Componentes Swing: no se serializan
public final class BattlePanel extends JPanel {
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
    private JLabel lblTacticalHint;

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

        heroHpBar = Theme.createProgressBar(0, gameEngine.getHero().getMaxHp(), Theme.ACCENT_GREEN);
        heroHpBar.setValue(gameEngine.getHero().getHp());
        heroHpBar.setStringPainted(true);
        heroHpBar.setForeground(Theme.ACCENT_GREEN);
        heroHpBar.setBackground(Theme.PANEL_SECONDARY);

        heroMpBar = Theme.createProgressBar(0, gameEngine.getHero().getMaxMp(), Theme.ACCENT_CYAN);
        heroMpBar.setValue(gameEngine.getHero().getMp());
        heroMpBar.setStringPainted(true);
        heroMpBar.setForeground(Theme.ACCENT_CYAN);
        heroMpBar.setBackground(Theme.PANEL_SECONDARY);

        heroAuxLabel = new JLabel("📋 Checklists: " + gameEngine.getHero().getChecklists());
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

        enemyHpBar = Theme.createProgressBar(0, 100, Theme.ACCENT_RED);
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

        // --- Banner de Pista Táctica del Ingeniero ---
        JPanel hintCard = Theme.createCard("💡 Pista Táctica de Ingeniería de Software");
        lblTacticalHint = new JLabel("Selecciona un proyecto en la Campaña para recibir el análisis del defecto.");
        lblTacticalHint.setForeground(Theme.ACCENT_AMBER);
        lblTacticalHint.setFont(Theme.FONT_BOLD);
        hintCard.add(lblTacticalHint, BorderLayout.CENTER);

        JPanel topSection = new JPanel(new BorderLayout(0, 8));
        topSection.setOpaque(false);
        topSection.add(arenaPanel, BorderLayout.CENTER);
        topSection.add(hintCard, BorderLayout.SOUTH);
        add(topSection, BorderLayout.NORTH);

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
        btnCode.setToolTipText("Escribe código: daña al defecto y ganas MP. Puede inyectar defectos (menos con más nivel PSP).");
        btnInspect = Theme.createStyledButton("📋 Checklist PSP (Inspección)", Theme.ACCENT_GREEN);
        btnEstimate = Theme.createStyledButton("📐 Planear y Diseñar (PSP 1)", Theme.ACCENT_AMBER);
        btnSynergy = Theme.createStyledButton("🤝 Sinergia TSP (Equipo)", Theme.ACCENT_PURPLE);
        btnStatControl = Theme.createStyledButton("📊 Control Estadístico (CMMI-4)", Theme.ACCENT_CYAN);
        btnContinuous = Theme.createStyledButton("🚀 Mejora Continua (CMMI-5)", Theme.ACCENT_GREEN);

        btnCode.addActionListener(e -> executeAction(() -> gameEngine.getCurrentBattle().actionWriteCode(), Sound.Sfx.ATTACK));
        btnInspect.addActionListener(e -> executeAction(() -> gameEngine.getCurrentBattle().actionInspectCode(), Sound.Sfx.HEAL));
        btnEstimate.addActionListener(e -> executeAction(() -> gameEngine.getCurrentBattle().actionEstimateSize(), Sound.Sfx.POWER));
        btnSynergy.addActionListener(e -> showSynergyMenu());
        btnStatControl.addActionListener(e -> executeAction(() -> gameEngine.getCurrentBattle().actionStatisticalControl(), Sound.Sfx.ATTACK));
        btnContinuous.addActionListener(e -> executeAction(() -> gameEngine.getCurrentBattle().actionContinuousImprovement(), Sound.Sfx.HEAL));
        for (JButton b : new JButton[]{btnCode, btnInspect, btnEstimate, btnStatControl, btnContinuous}) {
            b.putClientProperty(Theme.CUSTOM_SOUND, Boolean.TRUE);
        }

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

    private void executeAction(Runnable action, Sound.Sfx sound) {
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

        Sound.play(sound);
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
            TeamMember member = gameEngine.getTeam().stream().filter(m -> m.getRole() == role).findFirst().orElse(null);
            JMenuItem item = new JMenuItem(role.getTitle() + "  →  " + BattleEngine.synergyEffect(role, BattleEngine.synergyPower(member)));
            item.setToolTipText(role.getDescription());
            item.setFont(Theme.FONT_REGULAR);
            item.addActionListener(e -> executeAction(() -> gameEngine.getCurrentBattle().actionTeamSynergy(role), Sound.Sfx.POWER));
            menu.add(item);
        }
        menu.show(btnSynergy, 0, -menu.getPreferredSize().height);
    }

    public void logMessage(String msg) {
        battleLogArea.append(EmojiSupport.clean(msg, battleLogArea.getFont()) + "\n");
        battleLogArea.setCaretPosition(battleLogArea.getDocument().getLength());
    }

    private static final int COST_ESTIMATE = BattleEngine.COST_PLAN;
    private static final int COST_SYNERGY = BattleEngine.COST_SYNERGY;
    private static final int COST_STAT = BattleEngine.COST_STAT;
    private static final int COST_CONTINUOUS = BattleEngine.COST_CONTINUOUS;

    /** Textos de los botones con su costo para que el jugador sepa qué puede usar. */
    private void updateActionLabels(Hero hero) {
        int cmmi = hero.getCmmiLevel().getLevelNumber();
        btnCode.setText("💻 Codificar · +" + BattleEngine.CODING_MP_GAIN + " MP");
        btnInspect.setText("📋 Checklist PSP (Curar) · quedan " + hero.getChecklists());
        btnInspect.setToolTipText("Revisión con lista de chequeo: cura y corrige hasta 3 defectos pendientes. "
                + "Cada revisión extra en el mismo proyecto cura menos. Cómpralas en la Campaña.");
        BattleEngine battle = gameEngine.getCurrentBattle();
        boolean planned = battle != null && battle.isPlanned();
        btnEstimate.setText(planned ? "✅ Proyecto planeado" : "📐 Planear y Diseñar · " + COST_ESTIMATE + " MP");
        btnEstimate.setToolTipText("PSP 1 / 2.1 (una vez por proyecto, ideal al inicio): re-estimas el tamaño, "
                + "tus próximos 3 ataques hacen +30% de daño y tus próximas 3 codificaciones no inyectan defectos.");
        btnSynergy.setText("🤝 Sinergia TSP · " + COST_SYNERGY + " MP");
        btnSynergy.setToolTipText("Pide ayuda a un rol de tu equipo TSP (elige en el menú).");
        btnStatControl.setText(cmmi >= 4 ? "📊 Control Estadístico · " + COST_STAT + " MP" : "🔒 Control Estadístico (requiere CMMI 4)");
        btnStatControl.setToolTipText("CMMI 4: daño de 60 a 100 según el yield de tu proceso (% de defectos ya corregidos).");
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
        int pending = gameEngine.getPspLog().getPendingDefects();
        heroAuxLabel.setText("📋 Checklists: " + hero.getChecklists() + "   |   🐞 Defectos pendientes: " + pending
                + (pending > 0 ? " (cada uno aumenta el daño que recibes)" : ""));
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

            Mission activeMission = gameEngine.getActiveMission();
            String hintText = (activeMission != null) ? activeMission.getTacticalHint() : enemy.getTacticalHint();
            lblTacticalHint.setText("<html><body style='width: 820px;'>" + hintText + "</body></html>");

            boolean active = !battle.isBattleOver();
            int cmmi = hero.getCmmiLevel().getLevelNumber();
            btnCode.setEnabled(active);
            btnInspect.setEnabled(active && hero.getChecklists() > 0);
            btnEstimate.setEnabled(active && !battle.isPlanned() && hero.getMp() >= COST_ESTIMATE);
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

