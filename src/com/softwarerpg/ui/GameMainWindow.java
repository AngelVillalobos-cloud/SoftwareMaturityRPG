package com.softwarerpg.ui;

import com.softwarerpg.engine.GameEngine;
import com.softwarerpg.model.Hero;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Ventana principal del juego RPG "Software Maturity: The Quest for Level 5".
 */
public class GameMainWindow extends JFrame {
    private final GameEngine gameEngine;

    // Elementos del Header
    private JLabel lblPlayerInfo;
    private JProgressBar topHpBar;
    private JProgressBar topMpBar;
    private JLabel lblGold;
    private JLabel lblCmmiBadge;
    private JLabel lblPspBadge;
    private JLabel lblMoProSoftBadge;

    private JTabbedPane mainTabs;
    private MissionsPanel missionsPanel;
    private BattlePanel battlePanel;
    private PSPLogPanel pspLogPanel;
    private TSPTeamPanel tspTeamPanel;
    private MoProSoftPanel moProSoftPanel;
    private CodexPanel codexPanel;

    public GameMainWindow(GameEngine engine) {
        this.gameEngine = engine;
        setTitle("Software Maturity RPG — La Senda de la Calidad (CMMI | MoProSoft | PSP | TSP)");
        setSize(1100, 750);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
        updateHeader();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout(0, 0));
        rootPanel.setBackground(Theme.BG_DARK);

        // --- BARRA SUPERIOR (HEADER HUD) ---
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(Theme.PANEL_BG);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(10, 16, 10, 16)
        ));

        // Info Jugador izquierda
        JPanel leftHeader = new JPanel(new GridLayout(2, 1, 2, 2));
        leftHeader.setOpaque(false);
        lblPlayerInfo = new JLabel();
        lblPlayerInfo.setFont(Theme.FONT_TITLE);
        lblPlayerInfo.setForeground(Theme.TEXT_PRIMARY);

        JPanel barsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        barsPanel.setOpaque(false);

        topHpBar = new JProgressBar(0, 100);
        topHpBar.setPreferredSize(new Dimension(140, 18));
        topHpBar.setStringPainted(true);
        topHpBar.setForeground(Theme.ACCENT_GREEN);
        topHpBar.setBackground(Theme.PANEL_SECONDARY);

        topMpBar = new JProgressBar(0, 60);
        topMpBar.setPreferredSize(new Dimension(140, 18));
        topMpBar.setStringPainted(true);
        topMpBar.setForeground(Theme.ACCENT_CYAN);
        topMpBar.setBackground(Theme.PANEL_SECONDARY);

        lblGold = new JLabel("💰 0 Oro");
        lblGold.setFont(Theme.FONT_BOLD);
        lblGold.setForeground(Theme.ACCENT_AMBER);

        barsPanel.add(new JLabel("HP:") {{ setForeground(Theme.TEXT_MUTED); setFont(Theme.FONT_BOLD); }});
        barsPanel.add(topHpBar);
        barsPanel.add(new JLabel("MP:") {{ setForeground(Theme.TEXT_MUTED); setFont(Theme.FONT_BOLD); }});
        barsPanel.add(topMpBar);
        barsPanel.add(lblGold);

        leftHeader.add(lblPlayerInfo);
        leftHeader.add(barsPanel);
        headerPanel.add(leftHeader, BorderLayout.WEST);

        // Badges derecha
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        rightHeader.setOpaque(false);

        lblCmmiBadge = createBadge("CMMI: Nivel 1", Theme.ACCENT_RED);
        lblPspBadge = createBadge("PSP: 0", Theme.ACCENT_CYAN);
        lblMoProSoftBadge = createBadge("MoProSoft: Nivel 1", Theme.ACCENT_GREEN);

        JButton btnRest = Theme.createStyledButton("☕ Descanso / Retrospectiva", Theme.ACCENT_AMBER);
        btnRest.addActionListener(e -> {
            gameEngine.getHero().rest();
            JOptionPane.showMessageDialog(this,
                "Has realizado una pausa disciplinada y retrospectiva de sprint.\n¡Salud (HP) y Disciplina (MP) restauradas al 100%!",
                "Sprint Review / Descanso", JOptionPane.INFORMATION_MESSAGE);
            updateAll();
        });

        rightHeader.add(lblCmmiBadge);
        rightHeader.add(lblPspBadge);
        rightHeader.add(lblMoProSoftBadge);
        rightHeader.add(btnRest);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // --- PESTAÑAS PRINCIPALES ---
        mainTabs = new JTabbedPane();
        Theme.applyDarkTabbedPane(mainTabs);

        missionsPanel = new MissionsPanel(gameEngine, this::updateAll, mainTabs::setSelectedIndex);
        battlePanel = new BattlePanel(gameEngine, this::updateAll);
        pspLogPanel = new PSPLogPanel(gameEngine, this::updateAll);
        tspTeamPanel = new TSPTeamPanel(gameEngine, this::updateAll);
        moProSoftPanel = new MoProSoftPanel(gameEngine, this::updateAll);
        codexPanel = new CodexPanel();

        mainTabs.addTab("🗺️ Campaña CMMI", missionsPanel);
        mainTabs.addTab("⚔️ Combate RPG", battlePanel);
        mainTabs.addTab("📊 Bitácora PSP", pspLogPanel);
        mainTabs.addTab("👥 Gremio TSP", tspTeamPanel);
        mainTabs.addTab("🇲🇽 Org. MoProSoft", moProSoftPanel);
        mainTabs.addTab("📖 Códice Didáctico", codexPanel);

        rootPanel.add(mainTabs, BorderLayout.CENTER);

        setContentPane(rootPanel);
    }

    private JLabel createBadge(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_BOLD);
        lbl.setForeground(color);
        lbl.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(color, 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
        return lbl;
    }

    public void updateHeader() {
        Hero hero = gameEngine.getHero();
        lblPlayerInfo.setText("👤 " + hero.getName() + " — Nivel " + hero.getLevel() + " (XP: " + hero.getXp() + ")");

        topHpBar.setMaximum(hero.getMaxHp());
        topHpBar.setValue(hero.getHp());
        topHpBar.setString(hero.getHp() + " / " + hero.getMaxHp());

        topMpBar.setMaximum(hero.getMaxMp());
        topMpBar.setValue(hero.getMp());
        topMpBar.setString(hero.getMp() + " / " + hero.getMaxMp());

        lblGold.setText("💰 " + hero.getGold() + " Oro");

        lblCmmiBadge.setText("🏛️ CMMI: " + hero.getCmmiLevel().getName());
        lblPspBadge.setText("👤 PSP: " + hero.getPspLevel().getCode());
        lblMoProSoftBadge.setText("🇲🇽 " + gameEngine.getOrg().getCapabilityLevelName());
    }

    public void updateAll() {
        updateHeader();
        battlePanel.updateBattleUI();
        pspLogPanel.updateTables();
        tspTeamPanel.refreshMembers();
        moProSoftPanel.refreshData();
        missionsPanel.refreshMissions();
    }
}

