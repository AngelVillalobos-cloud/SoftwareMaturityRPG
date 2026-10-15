package com.softwarerpg.ui;

import com.softwarerpg.engine.BattleEngine;
import com.softwarerpg.engine.GameEngine;
import com.softwarerpg.model.CMMILevel;
import com.softwarerpg.model.Enemy;
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

    private CMMILevel cmmiAtBattleStart;

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
            if (gameEngine.isBattleInProgress()) {
                JOptionPane.showMessageDialog(this,
                    "No puedes descansar a mitad de un combate.\nTermina el proyecto primero.",
                    "Combate en curso", JOptionPane.WARNING_MESSAGE);
                return;
            }
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

        missionsPanel = new MissionsPanel(gameEngine, this::updateAll, mainTabs::setSelectedIndex,
                createBattleListener(), this::onBattleStarting);
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

    /** Conecta los eventos del motor de combate con la consola y el letrero de resultado. */
    private BattleEngine.BattleListener createBattleListener() {
        return new BattleEngine.BattleListener() {
            @Override
            public void onBattleLog(String message) {
                battlePanel.logMessage(message);
            }

            @Override
            public void onBattleEnded(boolean victory) {
                // Se difiere para que la UI muestre primero el estado final del combate
                SwingUtilities.invokeLater(() -> showBattleResult(victory));
            }
        };
    }

    private void onBattleStarting() {
        battlePanel.clearLog();
        cmmiAtBattleStart = gameEngine.getHero().getCmmiLevel();
    }

    private void showBattleResult(boolean victory) {
        updateAll();
        BattleEngine battle = gameEngine.getCurrentBattle();
        Enemy enemy = battle.getEnemy();
        Hero hero = gameEngine.getHero();

        StringBuilder msg = new StringBuilder("<html><body style='width: 340px;'>");
        if (victory) {
            msg.append("<h2 style='color:#4ADE80;'>🎉 ¡VICTORIA!</h2>")
               .append("Derrotaste a <b>").append(enemy.getName()).append("</b> en ")
               .append(battle.getTurnCount()).append(" turnos.<br><br>")
               .append("⭐ Recompensas: <b>+").append(enemy.getXpReward()).append(" XP</b> y <b>+")
               .append(enemy.getGoldReward()).append(" Oro</b>.");
            if (cmmiAtBattleStart != null && hero.getCmmiLevel() != cmmiAtBattleStart) {
                msg.append("<br><br>🏆 ¡Tu organización ascendió a <b>CMMI ")
                   .append(hero.getCmmiLevel().getName()).append("</b>!");
            }
        } else {
            msg.append("<h2 style='color:#F87171;'>💀 ¡PERDISTE!</h2>")
               .append("El proyecto colapsó ante <b>").append(enemy.getName()).append("</b>.<br><br>")
               .append("💡 Consejo: usa la <b>Checklist PSP</b> o la <b>Sinergia del Líder</b> cuando tu salud baje, ")
               .append("y compra checklists en la Campaña antes de volver a intentarlo.");
        }
        msg.append("<br><br><i>Retrospectiva del equipo: salud y disciplina restauradas.</i></body></html>");

        // Al terminar cualquier combate el equipo hace una retrospectiva y recupera HP/MP
        hero.rest();
        updateAll();

        String back = "🗺️ Volver a la Campaña";
        Object[] options = victory ? new Object[]{back} : new Object[]{"🔁 Reintentar", back};
        int choice = JOptionPane.showOptionDialog(this, msg.toString(),
                victory ? "Proyecto entregado" : "Proyecto fallido",
                JOptionPane.DEFAULT_OPTION,
                victory ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE,
                null, options, options[0]);

        if (!victory && choice == 0) {
            missionsPanel.retryLast();
        } else {
            mainTabs.setSelectedIndex(0);
        }
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

