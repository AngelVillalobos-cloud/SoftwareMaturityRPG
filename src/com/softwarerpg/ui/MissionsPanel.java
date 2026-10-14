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
public class MissionsPanel extends JPanel {
    private final GameEngine gameEngine;
    private final Runnable onStateUpdated;
    private final java.util.function.Consumer<Integer> tabSwitcher;

    private JPanel missionsListPanel;

    public MissionsPanel(GameEngine gameEngine, Runnable onStateUpdated, java.util.function.Consumer<Integer> tabSwitcher) {
        this.gameEngine = gameEngine;
        this.onStateUpdated = onStateUpdated;
        this.tabSwitcher = tabSwitcher;

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

        JLabel infoLabel = new JLabel("<html><body style='width: 500px; color: #D1D5DB;'>"
                + "Acepta proyectos de clientes y guía a tu equipo por los 5 niveles de madurez CMMI. "
                + "Cada entrega exitosa registra bitácoras en PSP y fortalece a tu organización.</body></html>");
        infoLabel.setFont(Theme.FONT_REGULAR);
        topContent.add(infoLabel, BorderLayout.CENTER);

        JPanel storePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        storePanel.setOpaque(false);

        JButton btnBuyChecklist = Theme.createStyledButton("🛒 Comprar Checklist (60 Oro)", Theme.ACCENT_GREEN);
        btnBuyChecklist.addActionListener(e -> {
            if (gameEngine.buyChecklist()) {
                JOptionPane.showMessageDialog(this, "¡Checklist PSP adquirida! Lista para prevenir defectos en combate.", "Compra Exitosa", JOptionPane.INFORMATION_MESSAGE);
                onStateUpdated.run();
            } else {
                JOptionPane.showMessageDialog(this, "Oro insuficiente.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton btnBuyTemplate = Theme.createStyledButton("📐 Comprar Plantilla Diseño (120 Oro)", Theme.ACCENT_PURPLE);
        btnBuyTemplate.addActionListener(e -> {
            if (gameEngine.buyDesignTemplate()) {
                JOptionPane.showMessageDialog(this, "¡Plantilla de Diseño PSP 2.1 adquirida!", "Compra Exitosa", JOptionPane.INFORMATION_MESSAGE);
                onStateUpdated.run();
            } else {
                JOptionPane.showMessageDialog(this, "Oro insuficiente.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

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
        scrollPane.getViewport().setBackground(Theme.BG_DARK);
        add(scrollPane, BorderLayout.CENTER);

        refreshMissions();
    }

    public void refreshMissions() {
        missionsListPanel.removeAll();
        List<Mission> list = gameEngine.getMissions();

        for (Mission mission : list) {
            JPanel card = new JPanel(new BorderLayout(10, 5));
            card.setBackground(Theme.PANEL_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDER_COLOR, 1, true),
                new EmptyBorder(10, 12, 10, 12)
            ));
            card.setMaximumSize(new Dimension(Short.MAX_VALUE, 125));

            // Info izquierda
            JPanel infoPanel = new JPanel(new GridLayout(3, 1, 2, 2));
            infoPanel.setOpaque(false);

            String statusTag = mission.isCompleted() ? "✅ [COMPLETADA] " : "⏳ [DISPONIBLE] ";
            JLabel titleLabel = new JLabel(statusTag + mission.getTitle() + " (Cliente: " + mission.getClient() + ")");
            titleLabel.setFont(Theme.FONT_HEADER);
            titleLabel.setForeground(mission.isCompleted() ? Theme.ACCENT_GREEN : Theme.ACCENT_CYAN);

            JLabel descLabel = new JLabel("<html><body style='width: 580px; color: #9CA3AF;'>" + mission.getDescription() + "</body></html>");
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

            JButton btnLaunch = Theme.createStyledButton(mission.isCompleted() ? "🔄 Reintentar Proyecto" : "🚀 Desplegar Proyecto", Theme.ACCENT_CYAN);
            btnLaunch.addActionListener(e -> launchMission(mission));
            rightPanel.add(btnLaunch);
            card.add(rightPanel, BorderLayout.EAST);

            missionsListPanel.add(card);
            missionsListPanel.add(Box.createVerticalStrut(10));
        }

        missionsListPanel.revalidate();
        missionsListPanel.repaint();
    }

    private void launchMission(Mission mission) {
        gameEngine.startMission(mission, new BattleEngine.BattleListener() {
            @Override
            public void onBattleLog(String message) {
                // El battle panel se actualizará a través del engine
            }

            @Override
            public void onBattleEnded(boolean victory) {
                refreshMissions();
                onStateUpdated.run();
            }
        });

        tabSwitcher.accept(1); // Cambiar a pestaña de combate (index 1)
        onStateUpdated.run();
    }

    private void startQuickBattle() {
        int stage = gameEngine.getHero().getCmmiLevel().getLevelNumber();
        gameEngine.startRandomPracticeBattle(stage, new BattleEngine.BattleListener() {
            @Override
            public void onBattleLog(String message) {}
            @Override
            public void onBattleEnded(boolean victory) {
                onStateUpdated.run();
            }
        });
        tabSwitcher.accept(1);
        onStateUpdated.run();
    }
}

