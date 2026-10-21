package com.softwarerpg.ui;

import com.softwarerpg.engine.BattleEngine;
import com.softwarerpg.engine.GameEngine;
import com.softwarerpg.model.TSPRole;
import com.softwarerpg.model.TeamMember;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel del Team Software Process (TSP):
 * Gestión de la cuadrilla de ingenieros, asignación de los 5 roles clave,
 * y entrenamiento individual en PSP (que hace más fuerte su sinergia en combate).
 */
@SuppressWarnings("serial") // Componentes Swing: no se serializan
public final class TSPTeamPanel extends JPanel {
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
                + "En combate, el botón <b>Sinergia TSP</b> pide ayuda a uno de estos roles. "
                + "<b>Cada nivel PSP de entrenamiento hace 10% más fuerte la sinergia del Líder, Desarrollo y Calidad.</b></body></html>");
        descLabel.setFont(Theme.FONT_REGULAR);
        headerCard.add(descLabel, BorderLayout.CENTER);

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

            // Planificación y Requerimientos tienen efectos fijos: el entrenamiento potencia a los otros 3 roles
            boolean scales = member.getRole() == TSPRole.LIDER_EQUIPO || member.getRole() == TSPRole.GESTOR_DESARROLLO
                    || member.getRole() == TSPRole.GESTOR_CALIDAD;
            JLabel skillLabel = new JLabel("⚡ En combate: " + BattleEngine.synergyEffect(member.getRole(), BattleEngine.synergyPower(member))
                    + (scales ? "  (potencia " + Math.round(BattleEngine.synergyPower(member) * 100) + "%)" : ""));
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

            boolean maxed = member.getPspTraining().getNext() == member.getPspTraining();
            int cost = gameEngine.trainingCost(member);
            JButton btnTrain = Theme.createStyledButton(maxed ? "🎓 Entrenamiento completo"
                    : "🎓 Entrenar en " + member.getPspTraining().getNext().getCode() + " (" + cost + " Oro)", Theme.ACCENT_PURPLE);
            btnTrain.setEnabled(!maxed);
            btnTrain.setToolTipText(scales ? "Sube su nivel PSP: su sinergia en combate será 10% más fuerte."
                    : "Sube su nivel PSP (TSP pide que todo el equipo esté entrenado). Su efecto en combate es fijo.");
            btnTrain.putClientProperty(Theme.CUSTOM_SOUND, Boolean.TRUE);
            btnTrain.addActionListener(e -> {
                if (gameEngine.trainTeamMemberPSP(member)) {
                    Sound.play(Sound.Sfx.COIN);
                    JOptionPane.showMessageDialog(this,
                        member.getName() + " ha avanzado al nivel " + member.getPspTraining().getTitle() + ".\n"
                        + "Su sinergia ahora es: " + BattleEngine.synergyEffect(member.getRole(), BattleEngine.synergyPower(member)) + ".",
                        "Entrenamiento Completado", JOptionPane.INFORMATION_MESSAGE);
                    refreshMembers();
                    onStateUpdated.run();
                } else {
                    Sound.play(Sound.Sfx.ERROR);
                    JOptionPane.showMessageDialog(this,
                        "Oro insuficiente (requiere " + cost + " de oro). ¡Completa misiones para financiar la capacitación!",
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
}
