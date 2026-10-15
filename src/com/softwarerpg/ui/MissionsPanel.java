package com.softwarerpg.ui;

import com.softwarerpg.engine.BattleEngine;
import com.softwarerpg.engine.GameEngine;
import com.softwarerpg.model.Mission;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel de Campaña y Selección de Proyectos.
 * Permite desplegar proyectos organizados por niveles de madurez CMMI
 * y acceder a la tienda de artefactos PSP.
 */
@SuppressWarnings("serial") // Componentes Swing: no se serializan
public final class MissionsPanel extends JPanel {
    private final GameEngine gameEngine;
    private final Runnable onStateUpdated;
    private final java.util.function.Consumer<Integer> tabSwitcher;
    private final BattleEngine.BattleListener battleListener;
    private final Runnable onBattleStarting;

    private JPanel missionsListPanel;
    private Runnable lastLaunch; // Último combate desplegado (para "Reintentar")

    public MissionsPanel(GameEngine gameEngine, Runnable onStateUpdated, java.util.function.Consumer<Integer> tabSwitcher,
                         BattleEngine.BattleListener battleListener, Runnable onBattleStarting) {
        this.gameEngine = gameEngine;
        this.onStateUpdated = onStateUpdated;
        this.tabSwitcher = tabSwitcher;
        this.battleListener = battleListener;
        this.onBattleStarting = onBattleStarting;

        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        initComponents();
    }

    private void initComponents() {
        // --- Header con Tienda Rápida PSP ---
        JPanel topCard = Theme.createCard("🗺️ Campaña CMMI: La Ascensión del Software");
        JPanel topContent = new JPanel(new BorderLayout(10, 5));
        topContent.setOpaque(false);

        JLabel infoLabel = new JLabel("<html><body style='width: 640px; color: #D1D5DB;'>"
                + "<b style='color:#FBBF24;'>¿Cómo se juega?</b> "
                + "<b>1)</b> Despliega la <b>⭐ siguiente misión</b>. "
                + "<b>2)</b> En combate <b>Codifica</b> para dañar al defecto; si tu salud baja usa la <b>Checklist PSP</b> "
                + "o la <b>Sinergia del Líder</b>. "
                + "<b>3)</b> Al ganar subes de nivel CMMI y se desbloquea la siguiente misión. "
                + "<b>4)</b> Con el oro compra checklists y mejora tu organización (MoProSoft) y tu equipo (TSP)."
                + "</body></html>");
        infoLabel.setFont(Theme.FONT_REGULAR);
        topContent.add(infoLabel, BorderLayout.CENTER);

        JPanel storePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        storePanel.setOpaque(false);

        JButton btnBuyChecklist = Theme.createStyledButton("🛒 Comprar Checklist (60 Oro)", Theme.ACCENT_GREEN);
        btnBuyChecklist.addActionListener(e -> {
            if (gameEngine.buyChecklist()) {
                Sound.play(Sound.Sfx.COIN);
                JOptionPane.showMessageDialog(this, "¡Checklist PSP adquirida! Lista para prevenir defectos en combate.", "Compra Exitosa", JOptionPane.INFORMATION_MESSAGE);
                onStateUpdated.run();
            } else {
                Sound.play(Sound.Sfx.ERROR);
                JOptionPane.showMessageDialog(this, "Oro insuficiente.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton btnBuyTemplate = Theme.createStyledButton("📐 Comprar Plantilla Diseño (120 Oro)", Theme.ACCENT_PURPLE);
        btnBuyTemplate.setToolTipText("Se usa al hacer Estimación LOC en combate: las próximas 3 codificaciones no inyectan defectos.");
        btnBuyTemplate.addActionListener(e -> {
            if (gameEngine.buyDesignTemplate()) {
                Sound.play(Sound.Sfx.COIN);
                JOptionPane.showMessageDialog(this, "¡Plantilla de Diseño PSP 2.1 adquirida!\nSe usará en tu próxima Estimación LOC: 3 codificaciones sin defectos.", "Compra Exitosa", JOptionPane.INFORMATION_MESSAGE);
                onStateUpdated.run();
            } else {
                Sound.play(Sound.Sfx.ERROR);
                JOptionPane.showMessageDialog(this, "Oro insuficiente.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        for (JButton b : new JButton[]{btnBuyChecklist, btnBuyTemplate}) b.putClientProperty(Theme.CUSTOM_SOUND, Boolean.TRUE);

        JButton btnQuickBug = Theme.createStyledButton("👾 Caza Rápida de Bugs", Theme.ACCENT_AMBER);
        btnQuickBug.addActionListener(e -> startQuickBattle());

        storePanel.add(btnBuyChecklist);
        storePanel.add(btnBuyTemplate);
        storePanel.add(btnQuickBug);
        topContent.add(storePanel, BorderLayout.SOUTH);

        topCard.add(topContent, BorderLayout.CENTER);
        add(topCard, BorderLayout.NORTH);

        // --- Lista de Misiones ---
        missionsListPanel = new JPanel();
        missionsListPanel.setLayout(new BoxLayout(missionsListPanel, BoxLayout.Y_AXIS));
        missionsListPanel.setBackground(Theme.BG_DARK);

        JScrollPane scrollPane = new JScrollPane(missionsListPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(Theme.BG_DARK);
        add(scrollPane, BorderLayout.CENTER);

        refreshMissions();
    }

    public void refreshMissions() {
        missionsListPanel.removeAll();
        List<Mission> list = gameEngine.getMissions();

        for (int i = 0; i < list.size(); i++) {
            Mission mission = list.get(i);
            boolean locked = isLocked(i);
            boolean next = !mission.isCompleted() && !locked;
            JPanel card = new JPanel(new BorderLayout(10, 5));
            card.setBackground(Theme.PANEL_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(next ? Theme.ACCENT_AMBER : Theme.BORDER_COLOR, next ? 2 : 1, true),
                new EmptyBorder(10, 12, 10, 12)
            ));
            card.setMaximumSize(new Dimension(Short.MAX_VALUE, 125));

            // Info izquierda
            JPanel infoPanel = new JPanel(new GridLayout(3, 1, 2, 2));
            infoPanel.setOpaque(false);

            String statusTag = mission.isCompleted() ? "✅ [COMPLETADA] " : locked ? "🔒 [BLOQUEADA] " : "⭐ [SIGUIENTE MISIÓN] ";
            JLabel titleLabel = new JLabel(statusTag + mission.getTitle() + " (Cliente: " + mission.getClient() + ")");
            titleLabel.setFont(Theme.FONT_HEADER);
            titleLabel.setForeground(mission.isCompleted() ? Theme.ACCENT_GREEN
                    : locked ? Theme.TEXT_MUTED : Theme.ACCENT_AMBER);

            JLabel descLabel = new JLabel("<html><body style='width: 500px; color: #9CA3AF;'>" + mission.getDescription() + "</body></html>");
            descLabel.setFont(Theme.FONT_REGULAR);

            JLabel metricsLabel = new JLabel("📏 Tamaño Est.: " + mission.getEstimatedLoc() + " LOC | ⏱️ Tiempo Est.: " + mission.getEstimatedMinutes() + " min | 👾 Jefe: " + mission.getBossEnemy().getName());
            metricsLabel.setFont(Theme.FONT_BOLD);
            metricsLabel.setForeground(Theme.ACCENT_AMBER);

            infoPanel.add(titleLabel);
            infoPanel.add(descLabel);
            infoPanel.add(metricsLabel);
            card.add(infoPanel, BorderLayout.CENTER);

            // Botón Desplegar
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
            rightPanel.setOpaque(false);

            String launchText = mission.isCompleted() ? "🔄 Repetir Proyecto"
                    : locked ? "🔒 Bloqueada" : "🚀 Desplegar Proyecto";
            JButton btnLaunch = Theme.createStyledButton(launchText, next ? Theme.ACCENT_AMBER : Theme.ACCENT_CYAN);
            btnLaunch.setEnabled(!locked);
            btnLaunch.addActionListener(e -> launchMission(mission));
            rightPanel.add(btnLaunch);
            card.add(rightPanel, BorderLayout.EAST);

            missionsListPanel.add(card);
            missionsListPanel.add(Box.createVerticalStrut(10));
        }

        missionsListPanel.revalidate();
        missionsListPanel.repaint();
    }

    /** Una misión se desbloquea al completar la anterior. */
    private boolean isLocked(int index) {
        return index > 0 && !gameEngine.getMissions().get(index - 1).isCompleted();
    }

    private void launchMission(Mission mission) {
        lastLaunch = () -> launchMission(mission);
        onBattleStarting.run();
        gameEngine.startMission(mission, battleListener);
        tabSwitcher.accept(1); // Cambiar a pestaña de combate (index 1)
        onStateUpdated.run();
    }

    private void startQuickBattle() {
        lastLaunch = this::startQuickBattle;
        onBattleStarting.run();
        int stage = gameEngine.getHero().getCmmiLevel().getLevelNumber();
        gameEngine.startRandomPracticeBattle(stage, battleListener);
        tabSwitcher.accept(1);
        onStateUpdated.run();
    }

    /** Vuelve a desplegar el último combate (misión o caza rápida). */
    public void retryLast() {
        if (lastLaunch != null) lastLaunch.run();
    }
}

