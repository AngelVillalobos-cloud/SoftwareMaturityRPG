package com.softwarerpg.ui;

import com.softwarerpg.engine.GameEngine;
import com.softwarerpg.model.TeamMember;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel del Team Software Process (TSP):
 * Gestión de la cuadrilla de ingenieros, asignación de los 5 roles clave,
 * entrenamiento individual en PSP y juntas de seguimiento semanal.
 */
public class TSPTeamPanel extends JPanel {
    private final GameEngine gameEngine;
    private final Runnable onStateUpdated;

    private JPanel membersContainer;

    public TSPTeamPanel(GameEngine gameEngine, Runnable onStateUpdated) {
        this.gameEngine = gameEngine;
        this.onStateUpdated = onStateUpdated;
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        initComponents();
    }

    private void initComponents() {
        // --- Cabecera: Principios del TSP ---
        JPanel headerCard = Theme.createCard("👥 Cuadrilla TSP (Team Software Process) - Formación y Gestión de Equipos");
        JLabel descLabel = new JLabel("<html><body style='width: 650px; color: #D1D5DB;'>"
                + "El TSP ayuda a los equipos a establecer metas, planificar procesos y lograr productos de calidad con cero defectos. "
                + "<b>Regla esencial (TSP):</b> Cada integrante debe estar formalmente entrenado en <b>PSP</b>. "
                + "Entrena a tus miembros para desbloquear mayores sinergias en combate y reducir la tasa de defectos.</body></html>");
        descLabel.setFont(Theme.FONT_REGULAR);
        headerCard.add(descLabel, BorderLayout.CENTER);

        JButton btnWeeklyMeeting = Theme.createStyledButton("🗓️ Realizar Junta Semanal de Seguimiento", Theme.ACCENT_GREEN);
        btnWeeklyMeeting.addActionListener(e -> holdWeeklyMeeting());
        headerCard.add(btnWeeklyMeeting, BorderLayout.EAST);

        add(headerCard, BorderLayout.NORTH);

        // --- Lista de Miembros del Equipo ---
        membersContainer = new JPanel();
        membersContainer.setLayout(new BoxLayout(membersContainer, BoxLayout.Y_AXIS));
        membersContainer.setBackground(Theme.BG_DARK);

        JScrollPane scrollPane = new JScrollPane(membersContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Theme.BG_DARK);
        add(scrollPane, BorderLayout.CENTER);

        refreshMembers();
    }

    public void refreshMembers() {
        membersContainer.removeAll();
        List<TeamMember> team = gameEngine.getTeam();

        for (TeamMember member : team) {
            JPanel card = new JPanel(new BorderLayout(10, 5));
            card.setBackground(Theme.PANEL_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDER_COLOR, 1, true),
                new EmptyBorder(10, 12, 10, 12)
            ));
            card.setMaximumSize(new Dimension(Short.MAX_VALUE, 110));

            // Info izquierda
            JPanel infoPanel = new JPanel(new GridLayout(3, 1, 2, 2));
            infoPanel.setOpaque(false);

            JLabel nameLabel = new JLabel(member.getName() + " — Rol: " + member.getRole().getTitle());
            nameLabel.setFont(Theme.FONT_HEADER);
            nameLabel.setForeground(Theme.ACCENT_CYAN);

            JLabel descLabel = new JLabel(member.getRole().getDescription());
            descLabel.setFont(Theme.FONT_REGULAR);
            descLabel.setForeground(Theme.TEXT_MUTED);

            JLabel skillLabel = new JLabel("⚡ Habilidad Sinergia: " + member.getRole().getSkillEffect());
            skillLabel.setFont(Theme.FONT_REGULAR);
            skillLabel.setForeground(Theme.ACCENT_AMBER);

            infoPanel.add(nameLabel);
            infoPanel.add(descLabel);
            infoPanel.add(skillLabel);
            card.add(infoPanel, BorderLayout.CENTER);

            // Panel derecho: Entrenamiento PSP y botón de mejora
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
            rightPanel.setOpaque(false);

            JLabel pspLabel = new JLabel("Entrenamiento: " + member.getPspTraining().getTitle());
            pspLabel.setFont(Theme.FONT_BOLD);
            pspLabel.setForeground(Theme.ACCENT_PURPLE);

            JButton btnTrain = Theme.createStyledButton("🎓 Entrenar en " + member.getPspTraining().getNext().getCode() + " (150 Oro)", Theme.ACCENT_PURPLE);
            btnTrain.setEnabled(member.getPspTraining().getNext() != member.getPspTraining());
            btnTrain.addActionListener(e -> {
                if (gameEngine.trainTeamMemberPSP(member)) {
                    JOptionPane.showMessageDialog(this,
                        member.getName() + " ha avanzado al nivel " + member.getPspTraining().getTitle() + "!\n"
                        + "Aumenta la eficacia de las revisiones y la moral del equipo.",
                        "Entrenamiento Completado", JOptionPane.INFORMATION_MESSAGE);
                    refreshMembers();
                    onStateUpdated.run();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Oro insuficiente (Requiere 150 de Oro). ¡Completa misiones o vence bugs para financiar la capacitación!",
                        "Fondos Insuficientes", JOptionPane.WARNING_MESSAGE);
                }
            });

            rightPanel.add(pspLabel);
            rightPanel.add(btnTrain);
            card.add(rightPanel, BorderLayout.EAST);

            membersContainer.add(card);
            membersContainer.add(Box.createVerticalStrut(8));
        }

        membersContainer.revalidate();
        membersContainer.repaint();
    }

    private void holdWeeklyMeeting() {
        if (gameEngine.isBattleInProgress()) {
            JOptionPane.showMessageDialog(this,
                "La junta semanal se realiza entre proyectos, no a mitad de un combate.",
                "Combate en curso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        gameEngine.getHero().restoreMp(30);
        for (TeamMember m : gameEngine.getTeam()) {
            m.restoreEnergy(30);
        }
        JOptionPane.showMessageDialog(this,
            "📋 Junta Semanal de Seguimiento realizada con éxito:\n"
            + "- Revisión del esfuerzo y calendarización del trabajo.\n"
            + "- Seguimiento de la calidad y reporte de avance semanal.\n"
            + "¡Toda la party recupera +30 MP y energía de disciplina!",
            "Seguimiento TSP Semanal", JOptionPane.INFORMATION_MESSAGE);
        onStateUpdated.run();
    }
}

