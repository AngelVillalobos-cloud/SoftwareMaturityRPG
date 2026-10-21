package com.softwarerpg.ui;

import com.softwarerpg.engine.GameEngine;
import com.softwarerpg.model.MoProSoftOrg;
import com.softwarerpg.model.MoProSoftOrg.Area;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

/**
 * Panel de la Organización según el modelo mexicano MoProSoft (NMX-I-059-NYCE).
 * Las 3 categorías de procesos (DIR, GER, OPE) tienen una mejora cada una, con un efecto
 * claro en el juego. El nivel de capacidad de la organización sube solo con esas mejoras.
 */
@SuppressWarnings("serial") // Componentes Swing: no se serializan
public final class MoProSoftPanel extends JPanel {
    private final GameEngine gameEngine;
    private final Runnable onStateUpdated;

    private JLabel lblOrgName;
    private JLabel lblCapability;
    private JLabel lblGold;
    private final Map<Area, JLabel> levelLabels = new EnumMap<>(Area.class);
    private final Map<Area, JLabel> effectLabels = new EnumMap<>(Area.class);
    private final Map<Area, JButton> upgradeButtons = new EnumMap<>(Area.class);

    public MoProSoftPanel(GameEngine gameEngine, Runnable onStateUpdated) {
        this.gameEngine = gameEngine;
        this.onStateUpdated = onStateUpdated;
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        initComponents();
    }

    private void initComponents() {
        // --- Encabezado: estado de la organización ---
        JPanel topCard = Theme.createCard("🇲🇽 Organización MoProSoft (Norma NMX-I-059-NYCE)");
        JPanel topContent = new JPanel(new BorderLayout(10, 8));
        topContent.setOpaque(false);

        JPanel topGrid = new JPanel(new GridLayout(1, 3, 10, 5));
        topGrid.setOpaque(false);
        lblOrgName = header(Theme.ACCENT_CYAN);
        lblCapability = header(Theme.ACCENT_GREEN);
        lblGold = header(Theme.ACCENT_AMBER);
        topGrid.add(lblOrgName);
        topGrid.add(lblCapability);
        topGrid.add(lblGold);
        topContent.add(topGrid, BorderLayout.NORTH);

        JLabel intro = new JLabel("<html><body style='width: 820px; color: #D1D5DB;'>"
                + "MoProSoft organiza a una empresa de software en <b>3 categorías de procesos</b>. "
                + "Invierte el oro de tus proyectos en cada una (máximo nivel " + MoProSoftOrg.MAX_AREA_LEVEL + "). "
                + "Con cada mejora sube el <b>nivel de capacidad</b> de la organización: de 1 (Realizado) a 5 (Optimizado)."
                + "</body></html>");
        intro.setFont(Theme.FONT_REGULAR);
        topContent.add(intro, BorderLayout.CENTER);
        topCard.add(topContent, BorderLayout.CENTER);
        add(topCard, BorderLayout.NORTH);

        // --- Las 3 categorías ---
        JPanel categoriesPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        categoriesPanel.setOpaque(false);
        categoriesPanel.add(createAreaCard(Area.DIR, "1. Alta Dirección (DIR)",
                "<b>Gestión de Negocios.</b> Define la estrategia, las metas comerciales y el presupuesto.",
                "Rol: Grupo Directivo.", Theme.ACCENT_AMBER));
        categoriesPanel.add(createAreaCard(Area.GER, "2. Gerencia (GER)",
                "<b>Gestión de Procesos, Proyectos y Recursos:</b> Recursos Humanos (RH), "
                        + "Bienes, Servicios e Infraestructura (BSI) y Conocimiento de la Organización (CO).",
                "Rol: Gerentes de procesos y recursos.", Theme.ACCENT_CYAN));
        categoriesPanel.add(createAreaCard(Area.OPE, "3. Operación (OPE)",
                "<b>Administración de Proyectos Específicos y Desarrollo y Mantenimiento de Software:</b> "
                        + "requisitos, análisis, diseño, construcción y pruebas.",
                "Roles: Responsable de Proceso, Analistas, Programadores y Revisores.", Theme.ACCENT_GREEN));
        add(categoriesPanel, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createAreaCard(Area area, String title, String description, String roles, Color accent) {
        JPanel card = Theme.createCard(title);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel desc = new JLabel("<html><body style='width: 230px; color: #D1D5DB;'>" + description + "</body></html>");
        desc.setFont(Theme.FONT_REGULAR);
        content.add(desc);
        content.add(Box.createVerticalStrut(12));

        JLabel level = new JLabel();
        level.setFont(Theme.FONT_BOLD);
        level.setForeground(Theme.TEXT_PRIMARY);
        levelLabels.put(area, level);
        content.add(level);

        JLabel effect = new JLabel();
        effect.setFont(Theme.FONT_REGULAR);
        effect.setForeground(accent);
        effectLabels.put(area, effect);
        content.add(effect);
        content.add(Box.createVerticalStrut(10));

        JButton upgrade = Theme.createStyledButton("Mejorar", accent);
        upgrade.putClientProperty(Theme.CUSTOM_SOUND, Boolean.TRUE);
        upgrade.addActionListener(e -> upgrade(area));
        upgradeButtons.put(area, upgrade);
        content.add(upgrade);
        content.add(Box.createVerticalStrut(12));

        JLabel rolesLabel = new JLabel("<html><body style='width: 230px; color: #9CA3AF; font-size: 10px;'>" + roles + "</body></html>");
        content.add(rolesLabel);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private static JLabel header(Color color) {
        JLabel label = new JLabel();
        label.setFont(Theme.FONT_HEADER);
        label.setForeground(color);
        return label;
    }

    /** Texto del efecto actual y del siguiente nivel de cada categoría. */
    private static String effectText(MoProSoftOrg org, Area area) {
        int lvl = org.getLevel(area);
        switch (area) {
            case DIR:
                return "+" + Math.round((org.getGoldMultiplier() - 1) * 100) + "% oro por proyecto"
                        + (lvl < MoProSoftOrg.MAX_AREA_LEVEL ? "  (siguiente: +" + lvl * 15 + "%)" : "");
            case GER:
                return "-" + Math.round(org.getDamageReduction() * 100) + "% daño recibido"
                        + (lvl < MoProSoftOrg.MAX_AREA_LEVEL ? "  (siguiente: -" + lvl * 8 + "%)" : "");
            default:
                return "+" + org.getCodingDamageBonus() + " daño y -" + Math.round(org.getDefectReduction() * 100) + "% defectos al codificar"
                        + (lvl < MoProSoftOrg.MAX_AREA_LEVEL ? "  (siguiente: +" + lvl * 5 + " / -" + lvl * 6 + "%)" : "");
        }
    }

    public void refreshData() {
        MoProSoftOrg org = gameEngine.getOrg();
        lblOrgName.setText("Empresa: " + org.getCompanyName());
        lblCapability.setText("Capacidad: " + org.getCapabilityLevelName());
        lblGold.setText("💰 Oro disponible: " + gameEngine.getHero().getGold());

        for (Area area : Area.values()) {
            levelLabels.get(area).setText("Nivel " + org.getLevel(area) + " de " + MoProSoftOrg.MAX_AREA_LEVEL);
            effectLabels.get(area).setText("<html><body style='width: 230px;'>" + effectText(org, area) + "</body></html>");
            JButton b = upgradeButtons.get(area);
            if (org.isMaxed(area)) {
                b.setText("🌟 Nivel máximo");
                b.setEnabled(false);
            } else {
                b.setText("⬆️ Mejorar (" + org.upgradeCost(area) + " Oro)");
                b.setEnabled(true);
            }
        }
    }

    private void upgrade(Area area) {
        int before = gameEngine.getOrg().getCapabilityLevel();
        if (gameEngine.upgradeOrg(area)) {
            Sound.play(Sound.Sfx.COIN);
            refreshData();
            onStateUpdated.run();
            if (gameEngine.getOrg().getCapabilityLevel() > before) {
                JOptionPane.showMessageDialog(this,
                    "🎉 ¡La organización alcanzó el " + gameEngine.getOrg().getCapabilityLevelName() + " de capacidad MoProSoft!",
                    "Ascenso Organizacional", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            Sound.play(Sound.Sfx.ERROR);
            JOptionPane.showMessageDialog(this,
                "Oro insuficiente (requiere " + gameEngine.getOrg().upgradeCost(area) + ").\nCompleta proyectos para conseguir más oro.",
                "Oro insuficiente", JOptionPane.WARNING_MESSAGE);
        }
    }
}
