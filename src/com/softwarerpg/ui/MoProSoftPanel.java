package com.softwarerpg.ui;

import com.softwarerpg.engine.GameEngine;
import com.softwarerpg.model.MoProSoftOrg;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Panel de la Organización según el modelo mexicano MoProSoft (NMX-I-059-NYCE):
 * - 3 Categorías de Procesos: Alta Dirección (DIR), Gerencia (GER), Operación (OPE).
 * - Recursos de Gerencia: RH (Recursos Humanos), BSI (Bienes, Servicios e Infraestructura), CO (Conocimiento Organizacional).
 * - Niveles de Capacidad (0 al 5) y Roles oficiales.
 */
public class MoProSoftPanel extends JPanel {
    private final GameEngine gameEngine;
    private final Runnable onStateUpdated;

    private JLabel lblOrgName;
    private JLabel lblCapability;
    private JLabel lblBudget;
    private JLabel lblProcessPoints;

    private JLabel lblRhLevel;
    private JLabel lblBsiLevel;
    private JLabel lblCoLevel;

    private JButton btnUpgradeCapability;
    private JButton btnUpgradeRh;
    private JButton btnUpgradeBsi;
    private JButton btnUpgradeCo;

    public MoProSoftPanel(GameEngine gameEngine, Runnable onStateUpdated) {
        this.gameEngine = gameEngine;
        this.onStateUpdated = onStateUpdated;
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        initComponents();
    }

    private void initComponents() {
        // --- Panel Superior: Estatus Organizacional ---
        JPanel topCard = Theme.createCard("🇲🇽 Organización MoProSoft (Norma NMX-I-059-NYCE)");
        JPanel topGrid = new JPanel(new GridLayout(2, 2, 10, 5));
        topGrid.setOpaque(false);

        lblOrgName = new JLabel("Empresa: " + gameEngine.getOrg().getCompanyName());
        lblOrgName.setFont(Theme.FONT_HEADER);
        lblOrgName.setForeground(Theme.ACCENT_CYAN);

        lblCapability = new JLabel("Capacidad: " + gameEngine.getOrg().getCapabilityLevelName());
        lblCapability.setFont(Theme.FONT_HEADER);
        lblCapability.setForeground(Theme.ACCENT_GREEN);

        lblBudget = new JLabel("💰 Presupuesto Empresarial: " + gameEngine.getOrg().getBudget() + " Monedas");
        lblBudget.setFont(Theme.FONT_BOLD);
        lblBudget.setForeground(Theme.ACCENT_AMBER);

        lblProcessPoints = new JLabel("📈 Puntos de Proceso: " + gameEngine.getOrg().getProcessPoints());
        lblProcessPoints.setFont(Theme.FONT_BOLD);
        lblProcessPoints.setForeground(Theme.ACCENT_PURPLE);

        topGrid.add(lblOrgName);
        topGrid.add(lblCapability);
        topGrid.add(lblBudget);
        topGrid.add(lblProcessPoints);

        btnUpgradeCapability = Theme.createStyledButton("🏅 Certificar y Subir Nivel de Capacidad MoProSoft", Theme.ACCENT_GREEN);
        btnUpgradeCapability.addActionListener(e -> upgradeCapability());

        JPanel headerContainer = new JPanel(new BorderLayout(10, 8));
        headerContainer.setOpaque(false);
        headerContainer.add(topGrid, BorderLayout.CENTER);
        headerContainer.add(btnUpgradeCapability, BorderLayout.SOUTH);
        topCard.add(headerContainer, BorderLayout.CENTER);

        add(topCard, BorderLayout.NORTH);

        // --- Panel Central: Las 3 Categorías MoProSoft ---
        JPanel categoriesPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        categoriesPanel.setOpaque(false);

        // 1. Alta Dirección (DIR)
        JPanel dirCard = Theme.createCard("1. Alta Dirección (DIR)");
        JTextArea dirText = new JTextArea(
            "• Proceso: Gestión de Negocios\n\n"
            + "Establece los lineamientos estratégicos para la gerencia, define metas comerciales, "
            + "asigna el presupuesto del proyecto y asegura la viabilidad a largo plazo.\n\n"
            + "Rol clave: Grupo Directivo."
        );
        styleTextArea(dirText);
        dirCard.add(dirText, BorderLayout.CENTER);

        // 2. Gerencia (GER)
        JPanel gerCard = Theme.createCard("2. Gerencia (GER)");
        JPanel gerContent = new JPanel();
        gerContent.setLayout(new BoxLayout(gerContent, BoxLayout.Y_AXIS));
        gerContent.setOpaque(false);

        JLabel gerDesc = new JLabel("<html><body style='width: 200px; color: #D1D5DB; font-size: 11px;'>"
                + "Define elementos para Operación, monitorea proyectos y administra los 3 recursos vitales:</body></html>");
        gerContent.add(gerDesc);
        gerContent.add(Box.createVerticalStrut(10));

        // RH
        lblRhLevel = new JLabel("• RH (Recursos Humanos): Nivel " + gameEngine.getOrg().getHumanResourcesLevel());
        lblRhLevel.setForeground(Theme.TEXT_PRIMARY);
        lblRhLevel.setFont(Theme.FONT_BOLD);
        btnUpgradeRh = Theme.createStyledButton("Capacitar RH (" + (gameEngine.getOrg().getHumanResourcesLevel() * 150) + " Oro)", Theme.ACCENT_CYAN);
        btnUpgradeRh.addActionListener(e -> {
            if (gameEngine.getOrg().upgradeRH()) {
                refreshData();
                onStateUpdated.run();
            } else {
                showNoFunds();
            }
        });
        gerContent.add(lblRhLevel);
        gerContent.add(btnUpgradeRh);
        gerContent.add(Box.createVerticalStrut(8));

        // BSI
        lblBsiLevel = new JLabel("• BSI (Bienes y Servicios): Nivel " + gameEngine.getOrg().getInfrastructureLevel());
        lblBsiLevel.setForeground(Theme.TEXT_PRIMARY);
        lblBsiLevel.setFont(Theme.FONT_BOLD);
        btnUpgradeBsi = Theme.createStyledButton("Mejorar BSI (" + (gameEngine.getOrg().getInfrastructureLevel() * 150) + " Oro)", Theme.ACCENT_AMBER);
        btnUpgradeBsi.addActionListener(e -> {
            if (gameEngine.getOrg().upgradeBSI()) {
                refreshData();
                onStateUpdated.run();
            } else {
                showNoFunds();
            }
        });
        gerContent.add(lblBsiLevel);
        gerContent.add(btnUpgradeBsi);
        gerContent.add(Box.createVerticalStrut(8));

        // CO
        lblCoLevel = new JLabel("• CO (Conocimiento Org.): Nivel " + gameEngine.getOrg().getKnowledgeBaseLevel());
        lblCoLevel.setForeground(Theme.TEXT_PRIMARY);
        lblCoLevel.setFont(Theme.FONT_BOLD);
        btnUpgradeCo = Theme.createStyledButton("Expandir CO (" + (gameEngine.getOrg().getKnowledgeBaseLevel() * 150) + " Oro)", Theme.ACCENT_PURPLE);
        btnUpgradeCo.addActionListener(e -> {
            if (gameEngine.getOrg().upgradeCO()) {
                refreshData();
                onStateUpdated.run();
            } else {
                showNoFunds();
            }
        });
        gerContent.add(lblCoLevel);
        gerContent.add(btnUpgradeCo);

        gerCard.add(gerContent, BorderLayout.CENTER);

        // 3. Operación (OPE)
        JPanel opeCard = Theme.createCard("3. Operación (OPE)");
        JTextArea opeText = new JTextArea(
            "• Procesos:\n"
            + "  - Administración de Proyectos Específicos\n"
            + "  - Desarrollo y Mantenimiento de Software\n\n"
            + "Realiza las actividades técnicas de ciclo de vida (requisitos, arquitectura, pruebas) "
            + "según los lineamientos de Gerencia.\n\n"
            + "Roles: Responsable de Proceso, Analistas, Programadores y Revisores."
        );
        styleTextArea(opeText);
        opeCard.add(opeText, BorderLayout.CENTER);

        categoriesPanel.add(dirCard);
        categoriesPanel.add(gerCard);
        categoriesPanel.add(opeCard);

        add(categoriesPanel, BorderLayout.CENTER);

        refreshData();
    }

    private void styleTextArea(JTextArea ta) {
        ta.setEditable(false);
        ta.setBackground(Theme.PANEL_SECONDARY);
        ta.setForeground(Theme.TEXT_PRIMARY);
        ta.setFont(Theme.FONT_REGULAR);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
    }

    public void refreshData() {
        MoProSoftOrg org = gameEngine.getOrg();
        lblOrgName.setText("Empresa: " + org.getCompanyName());
        lblCapability.setText("Capacidad: " + org.getCapabilityLevelName());
        lblBudget.setText("💰 Presupuesto Empresarial: " + org.getBudget() + " Monedas");
        lblProcessPoints.setText("📈 Puntos de Proceso: " + org.getProcessPoints());

        lblRhLevel.setText("• RH (Recursos Humanos): Nivel " + org.getHumanResourcesLevel());
        lblBsiLevel.setText("• BSI (Bienes y Servicios): Nivel " + org.getInfrastructureLevel());
        lblCoLevel.setText("• CO (Conocimiento Org.): Nivel " + org.getKnowledgeBaseLevel());

        btnUpgradeRh.setText("Capacitar RH (" + (org.getHumanResourcesLevel() * 150) + " Oro)");
        btnUpgradeBsi.setText("Mejorar BSI (" + (org.getInfrastructureLevel() * 150) + " Oro)");
        btnUpgradeCo.setText("Expandir CO (" + (org.getKnowledgeBaseLevel() * 150) + " Oro)");

        int nextCost = org.getCapabilityLevel() * 400 + 300;
        int nextPoints = org.getCapabilityLevel() * 150 + 100;
        if (org.getCapabilityLevel() < 5) {
            btnUpgradeCapability.setText("🏅 Evaluar y Ascender Capacidad MoProSoft (Requiere " + nextCost + " Oro y " + nextPoints + " Puntos)");
            btnUpgradeCapability.setEnabled(org.canUpgradeCapability());
        } else {
            btnUpgradeCapability.setText("🌟 ¡Nivel Máximo de Capacidad MoProSoft Alcanzado (Optimizado)!");
            btnUpgradeCapability.setEnabled(false);
        }
    }

    private void upgradeCapability() {
        if (gameEngine.getOrg().upgradeCapability()) {
            JOptionPane.showMessageDialog(this,
                "🎉 ¡Enhorabuena! La organización ha alcanzado el " + gameEngine.getOrg().getCapabilityLevelName() + "!\n"
                + "Se reduce el daño de los bugs y mejora la eficiencia de los proyectos.",
                "Ascenso Organizacional", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            onStateUpdated.run();
        } else {
            showNoFunds();
        }
    }

    private void showNoFunds() {
        JOptionPane.showMessageDialog(this,
            "Fondos o puntos de proceso insuficientes.\nCompleta proyectos para generar presupuesto y puntos de proceso.",
            "Recursos Insuficientes", JOptionPane.WARNING_MESSAGE);
    }
}

