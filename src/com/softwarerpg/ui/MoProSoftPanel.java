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
@SuppressWarnings("serial") // Componentes Swing: no se serializan
public final class MoProSoftPanel extends JPanel {
    private final GameEngine gameEngine;
    private final Runnable onStateUpdated;

    private JLabel lblOrgName;
    private JLabel lblCapability;
    private JLabel lblBudget;
    private JLabel lblProcessPoints;

    private JLabel lblDirLevel;
    private JLabel lblDirBonus;
    private JButton btnUpgradeDir;
    private JComboBox<String> comboFocus;

    private JLabel lblRhLevel;
    private JLabel lblBsiLevel;
    private JLabel lblCoLevel;

    private JLabel lblOpeLevel;
    private JLabel lblOpeBonus;
    private JButton btnUpgradeOpe;

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
        JPanel dirContent = new JPanel();
        dirContent.setLayout(new BoxLayout(dirContent, BoxLayout.Y_AXIS));
        dirContent.setOpaque(false);

        JLabel dirDesc = new JLabel("<html><body style='width: 220px; color: #D1D5DB; font-size: 11px;'>"
                + "<b>Proceso: Gestión de Negocios.</b> Define la estrategia y financiamiento:</body></html>");
        dirContent.add(dirDesc);
        dirContent.add(Box.createVerticalStrut(10));

        lblDirLevel = new JLabel("• Finanzas DIR: Nivel " + gameEngine.getOrg().getDirLevel());
        lblDirLevel.setForeground(Theme.TEXT_PRIMARY);
        lblDirLevel.setFont(Theme.FONT_BOLD);
        dirContent.add(lblDirLevel);

        lblDirBonus = new JLabel("Retorno financiero: +0% Oro");
        lblDirBonus.setForeground(Theme.ACCENT_AMBER);
        lblDirBonus.setFont(Theme.FONT_REGULAR);
        dirContent.add(lblDirBonus);
        dirContent.add(Box.createVerticalStrut(6));

        btnUpgradeDir = Theme.createStyledButton("Mejorar Finanzas DIR", Theme.ACCENT_CYAN);
        btnUpgradeDir.addActionListener(e -> {
            if (gameEngine.getOrg().upgradeDIR()) {
                refreshData();
                onStateUpdated.run();
            } else {
                showNoFunds();
            }
        });
        dirContent.add(btnUpgradeDir);
        dirContent.add(Box.createVerticalStrut(12));

        JLabel lblFocusTitle = new JLabel("Directriz Estratégica:");
        lblFocusTitle.setFont(Theme.FONT_BOLD);
        lblFocusTitle.setForeground(Theme.ACCENT_CYAN);
        dirContent.add(lblFocusTitle);

        comboFocus = new JComboBox<>(new String[]{"Equilibrado", "Calidad Total", "Agilidad y Entrega", "Innovación"});
        comboFocus.setSelectedItem(gameEngine.getOrg().getStrategicFocus());
        comboFocus.setBackground(Theme.PANEL_SECONDARY);
        comboFocus.setForeground(Theme.TEXT_PRIMARY);
        comboFocus.setFont(Theme.FONT_REGULAR);
        comboFocus.addActionListener(e -> {
            gameEngine.getOrg().setStrategicFocus((String) comboFocus.getSelectedItem());
            onStateUpdated.run();
        });
        dirContent.add(comboFocus);

        JLabel lblFocusDesc = new JLabel("<html><body style='width: 220px; color: #9CA3AF; font-size: 10px; margin-top: 4px;'>"
                + "• Calidad: +25% curación de checklists.<br>"
                + "• Agilidad: +15% daño en ataques.<br>"
                + "• Innovación: +5 MP por turno en combate.</body></html>");
        dirContent.add(lblFocusDesc);

        dirCard.add(dirContent, BorderLayout.CENTER);

        // 2. Gerencia (GER)
        JPanel gerCard = Theme.createCard("2. Gerencia (GER)");
        JPanel gerContent = new JPanel();
        gerContent.setLayout(new BoxLayout(gerContent, BoxLayout.Y_AXIS));
        gerContent.setOpaque(false);

        JLabel gerDesc = new JLabel("<html><body style='width: 220px; color: #D1D5DB; font-size: 11px;'>"
                + "<b>Gestión de Recursos, Procesos y Proyectos:</b> Administra el capital clave:</body></html>");
        gerContent.add(gerDesc);
        gerContent.add(Box.createVerticalStrut(10));

        // RH
        lblRhLevel = new JLabel("• RH (Recursos Humanos): Nivel " + gameEngine.getOrg().getHumanResourcesLevel());
        lblRhLevel.setForeground(Theme.TEXT_PRIMARY);
        lblRhLevel.setFont(Theme.FONT_BOLD);
        btnUpgradeRh = Theme.createStyledButton("Capacitar RH", Theme.ACCENT_CYAN);
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
        lblBsiLevel = new JLabel("• BSI (Bienes/Servicios): Nivel " + gameEngine.getOrg().getInfrastructureLevel());
        lblBsiLevel.setForeground(Theme.TEXT_PRIMARY);
        lblBsiLevel.setFont(Theme.FONT_BOLD);
        btnUpgradeBsi = Theme.createStyledButton("Mejorar BSI", Theme.ACCENT_AMBER);
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
        lblCoLevel = new JLabel("• CO (Conocimiento): Nivel " + gameEngine.getOrg().getKnowledgeBaseLevel());
        lblCoLevel.setForeground(Theme.TEXT_PRIMARY);
        lblCoLevel.setFont(Theme.FONT_BOLD);
        btnUpgradeCo = Theme.createStyledButton("Expandir CO", Theme.ACCENT_PURPLE);
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
        JPanel opeContent = new JPanel();
        opeContent.setLayout(new BoxLayout(opeContent, BoxLayout.Y_AXIS));
        opeContent.setOpaque(false);

        JLabel opeDesc = new JLabel("<html><body style='width: 220px; color: #D1D5DB; font-size: 11px;'>"
                + "<b>Desarrollo y Mantenimiento Técnico:</b> Ejecución directa de proyectos:</body></html>");
        opeContent.add(opeDesc);
        opeContent.add(Box.createVerticalStrut(10));

        lblOpeLevel = new JLabel("• Nivel Operativo OPE: " + gameEngine.getOrg().getOpeLevel());
        lblOpeLevel.setForeground(Theme.TEXT_PRIMARY);
        lblOpeLevel.setFont(Theme.FONT_BOLD);
        opeContent.add(lblOpeLevel);

        lblOpeBonus = new JLabel("Efecto: +0 Daño | -0% Bugs");
        lblOpeBonus.setForeground(Theme.ACCENT_GREEN);
        lblOpeBonus.setFont(Theme.FONT_REGULAR);
        opeContent.add(lblOpeBonus);
        opeContent.add(Box.createVerticalStrut(8));

        btnUpgradeOpe = Theme.createStyledButton("Mejorar Procesos OPE", Theme.ACCENT_GREEN);
        btnUpgradeOpe.addActionListener(e -> {
            if (gameEngine.getOrg().upgradeOPE()) {
                refreshData();
                onStateUpdated.run();
            } else {
                showNoFunds();
            }
        });
        opeContent.add(btnUpgradeOpe);
        opeContent.add(Box.createVerticalStrut(12));

        JLabel lblOpeRoles = new JLabel("<html><body style='width: 220px; color: #9CA3AF; font-size: 10px;'>"
                + "<b>Roles Involucrados:</b><br>"
                + "• Responsable de Proceso<br>"
                + "• Analistas de Sistemas<br>"
                + "• Programadores<br>"
                + "• Revisores de Código y QA</body></html>");
        opeContent.add(lblOpeRoles);

        opeCard.add(opeContent, BorderLayout.CENTER);

        categoriesPanel.add(dirCard);
        categoriesPanel.add(gerCard);
        categoriesPanel.add(opeCard);

        add(categoriesPanel, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        MoProSoftOrg org = gameEngine.getOrg();
        lblOrgName.setText("Empresa: " + org.getCompanyName());
        lblCapability.setText("Capacidad: " + org.getCapabilityLevelName());
        lblBudget.setText("💰 Presupuesto Empresarial: " + org.getBudget() + " Monedas");
        lblProcessPoints.setText("📈 Puntos de Proceso: " + org.getProcessPoints());

        // DIR
        lblDirLevel.setText("• Finanzas DIR: Nivel " + org.getDirLevel());
        int dirBonusPct = (int) Math.round((org.getBudgetMultiplier() - 1.0) * 100);
        lblDirBonus.setText("Retorno financiero: +" + dirBonusPct + "% Oro en misiones");
        btnUpgradeDir.setText("Mejorar Finanzas DIR (" + (org.getDirLevel() * 160) + " Oro)");

        // GER
        lblRhLevel.setText("• RH (Recursos Humanos): Nivel " + org.getHumanResourcesLevel());
        lblBsiLevel.setText("• BSI (Bienes y Servicios): Nivel " + org.getInfrastructureLevel());
        lblCoLevel.setText("• CO (Conocimiento Org.): Nivel " + org.getKnowledgeBaseLevel());

        btnUpgradeRh.setText("Capacitar RH (" + (org.getHumanResourcesLevel() * 150) + " Oro)");
        btnUpgradeBsi.setText("Mejorar BSI (" + (org.getInfrastructureLevel() * 150) + " Oro)");
        btnUpgradeCo.setText("Expandir CO (" + (org.getKnowledgeBaseLevel() * 150) + " Oro)");

        // OPE
        lblOpeLevel.setText("• Nivel Operativo OPE: " + org.getOpeLevel());
        int defectRedPct = (int) Math.round(org.getDefectReductionFactor() * 100);
        lblOpeBonus.setText("Efecto: +" + org.getOperationAttackBonus() + " Daño | -" + defectRedPct + "% Defectos");
        btnUpgradeOpe.setText("Mejorar Procesos OPE (" + (org.getOpeLevel() * 160) + " Oro)");

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

