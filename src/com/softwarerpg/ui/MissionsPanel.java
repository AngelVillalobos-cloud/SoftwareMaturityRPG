package com.softwarerpg.ui;

import com.softwarerpg.engine.BattleEngine;
import com.softwarerpg.engine.GameEngine;
import com.softwarerpg.model.Hero;
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
    private JButton btnQuickBug;
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
                + "<b>2)</b> En combate <b>Codifica</b> para hacer daño y ganar MP; gasta el MP en <b>Planear</b> o en tu <b>equipo TSP</b>. "
                + "Si tu salud baja o se acumulan defectos, usa una <b>Checklist PSP</b>. "
                + "<b>3)</b> Vencer al jefe de cada nivel te sube de nivel CMMI y desbloquea nuevas acciones. "
                + "<b>4)</b> Invierte el oro en checklists, en entrenar a tu equipo (TSP) y en tu empresa (MoProSoft). "
                + "<i>Entregar sin defectos pendientes paga +20% de oro.</i>"
                + "</body></html>");
        infoLabel.setFont(Theme.FONT_REGULAR);
        topContent.add(infoLabel, BorderLayout.CENTER);

        JPanel storePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        storePanel.setOpaque(false);

        JButton btnBuyChecklist = Theme.createStyledButton("🛒 Comprar Checklist (" + GameEngine.CHECKLIST_COST + " Oro)", Theme.ACCENT_GREEN);
        btnBuyChecklist.setToolTipText("Máximo " + Hero.MAX_CHECKLISTS + " en el inventario. En combate, cada revisión extra del mismo proyecto cura menos.");
        btnBuyChecklist.addActionListener(e -> {
            if (gameEngine.getHero().getChecklists() >= Hero.MAX_CHECKLISTS) {
                Sound.play(Sound.Sfx.ERROR);
                JOptionPane.showMessageDialog(this, "Ya tienes el máximo de " + Hero.MAX_CHECKLISTS + " checklists.", "Inventario lleno", JOptionPane.WARNING_MESSAGE);
            } else if (gameEngine.buyChecklist()) {
                Sound.play(Sound.Sfx.COIN);
                JOptionPane.showMessageDialog(this, "¡Checklist PSP adquirida! Lista para prevenir defectos en combate.", "Compra Exitosa", JOptionPane.INFORMATION_MESSAGE);
                onStateUpdated.run();
            } else {
                Sound.play(Sound.Sfx.ERROR);
                JOptionPane.showMessageDialog(this, "Oro insuficiente.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnBuyChecklist.putClientProperty(Theme.CUSTOM_SOUND, Boolean.TRUE);

        btnQuickBug = Theme.createStyledButton("👾 Caza Rápida de Bugs", Theme.ACCENT_AMBER);
        btnQuickBug.setToolTipText("Combate de práctica. Cada caza paga la mitad que la anterior; vuelve al 100% al subir de nivel CMMI.");
        btnQuickBug.addActionListener(e -> startQuickBattle());

        storePanel.add(btnBuyChecklist);
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
        int practicePct = (int) Math.round(gameEngine.getPracticeRewardMultiplier() * 100);
        btnQuickBug.setText("👾 Caza Rápida (" + practicePct + "% Recompensa)");
        btnQuickBug.setToolTipText("<html>Combate de práctica contra un bug de tu nivel.<br>"
                + "Cada caza paga la mitad que la anterior; vuelve al 100% al subir de nivel CMMI (vencer al jefe).</html>");

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
            card.setMaximumSize(new Dimension(Short.MAX_VALUE, 140));

            // Info izquierda (4 filas para incluir la pista táctica)
            JPanel infoPanel = new JPanel(new GridLayout(4, 1, 2, 2));
            infoPanel.setOpaque(false);

            String statusTag = mission.isCompleted() ? "✅ [COMPLETADA] " : locked ? "🔒 [BLOQUEADA] " : "⭐ [SIGUIENTE MISIÓN] ";
            JLabel titleLabel = new JLabel(statusTag + mission.getTitle() + " (Cliente: " + mission.getClient() + ")");
            titleLabel.setFont(Theme.FONT_HEADER);
            titleLabel.setForeground(mission.isCompleted() ? Theme.ACCENT_GREEN
                    : locked ? Theme.TEXT_MUTED : Theme.ACCENT_AMBER);

            JLabel descLabel = new JLabel("<html><body style='width: 540px; color: #9CA3AF;'>" + mission.getDescription() + "</body></html>");
            descLabel.setFont(Theme.FONT_REGULAR);

            JLabel hintLabel = new JLabel("<html><body style='width: 540px; color: #FBBF24; font-size: 11px;'>" + mission.getTacticalHint() + "</body></html>");
            hintLabel.setFont(Theme.FONT_REGULAR);

            JLabel metricsLabel = new JLabel("📏 Tamaño: " + mission.getEstimatedLoc() + " LOC | ⏱️ Tiempo: " + mission.getEstimatedMinutes() + " min | 👾 Jefe: " + mission.getBossEnemy().getName());
            metricsLabel.setFont(Theme.FONT_BOLD);
            metricsLabel.setForeground(Theme.ACCENT_CYAN);

            infoPanel.add(titleLabel);
            infoPanel.add(descLabel);
            infoPanel.add(hintLabel);
            infoPanel.add(metricsLabel);
            card.add(infoPanel, BorderLayout.CENTER);

            // Botón Desplegar
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
            rightPanel.setOpaque(false);

            String launchText = mission.isCompleted()
                    ? "🔄 Repetir (recompensa " + Math.round(GameEngine.REPEAT_MISSION_REWARD * 100) + "%)"
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

