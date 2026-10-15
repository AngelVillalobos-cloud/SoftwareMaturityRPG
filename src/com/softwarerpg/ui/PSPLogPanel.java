package com.softwarerpg.ui;

import com.softwarerpg.engine.GameEngine;
import com.softwarerpg.model.DefectLogEntry;
import com.softwarerpg.model.ProjectSummary;
import com.softwarerpg.model.TimeLogEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel que visualiza las Bitácoras oficiales del PSP (Personal Software Process):
 * 1. Bitácora de Registro de Tiempos (Time Recording Log)
 * 2. Bitácora de Registro de Defectos (Defect Recording Log - Chillarege/IBM)
 * 3. Resumen Post-Mortem y cálculo de Densidad de Defectos Dd = 1000 * D / N
 * 4. Registro en el Plan de Mejora Personal (PIP)
 */
@SuppressWarnings("serial") // Componentes Swing: no se serializan
public final class PSPLogPanel extends JPanel {
    private final GameEngine gameEngine;
    private final Runnable onStateUpdated;

    private JTable timeTable;
    private DefaultTableModel timeTableModel;

    private JTable defectTable;
    private DefaultTableModel defectTableModel;

    private DefaultTableModel historyTableModel;

    private JLabel lblTotalTime;
    private JLabel lblTotalDefects;
    private JLabel lblDefectDensity;
    private JLabel lblLocInfo;
    private JLabel lblPipCount;

    public PSPLogPanel(GameEngine gameEngine, Runnable onStateUpdated) {
        this.gameEngine = gameEngine;
        this.onStateUpdated = onStateUpdated;
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        initComponents();
    }

    private void initComponents() {
        // --- Panel Superior: Resumen Post-Mortem y Métricas ---
        JPanel summaryCard = Theme.createCard("📊 Resumen Post-Mortem & Métricas de Calidad (PSP 1 / 1.1)");
        JPanel metricsGrid = new JPanel(new GridLayout(2, 3, 15, 8));
        metricsGrid.setOpaque(false);

        lblTotalTime = createMetricLabel("⏱️ Tiempo Total: 0 min");
        lblTotalDefects = createMetricLabel("🐛 Defectos Registrados: 0");
        lblDefectDensity = createMetricLabel("📐 Densidad Dd: 0.0 def/KLOC");
        lblLocInfo = createMetricLabel("📝 Tamaño del Proyecto: 100 LOC");
        lblPipCount = createMetricLabel("💡 Ideas en PIP: 0 mejoras");

        JButton btnAddPip = Theme.createStyledButton("✍️ Registrar Idea en PIP", Theme.ACCENT_PURPLE);
        btnAddPip.addActionListener(e -> showAddPipDialog());

        metricsGrid.add(lblTotalTime);
        metricsGrid.add(lblTotalDefects);
        metricsGrid.add(lblDefectDensity);
        metricsGrid.add(lblLocInfo);
        metricsGrid.add(lblPipCount);
        metricsGrid.add(btnAddPip);

        summaryCard.add(metricsGrid, BorderLayout.CENTER);
        add(summaryCard, BorderLayout.NORTH);

        // --- Panel Central: Pestañas con Bitácora de Tiempos y Bitácora de Defectos ---
        JTabbedPane logsTabPane = new JTabbedPane();
        Theme.applyDarkTabbedPane(logsTabPane);

        // 1. Bitácora de Tiempos (Time Log)
        String[] timeCols = {"Fecha", "Inicio", "Fin", "Interrupción (min)", "Δ Tiempo (min)", "Actividad", "Comentarios", "C", "U"};
        timeTableModel = new DefaultTableModel(timeCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        timeTable = createStyledTable(timeTableModel);
        JScrollPane timeScroll = new JScrollPane(timeTable);
        timeScroll.getViewport().setBackground(Theme.PANEL_BG);

        // 2. Bitácora de Defectos (Defect Log)
        String[] defectCols = {"Fecha", "N°", "Tipo Chillarege", "Fase Inyectado", "Fase Eliminado", "Tiempo Corr. (min)", "¿Corregido?", "Descripción"};
        defectTableModel = new DefaultTableModel(defectCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        defectTable = createStyledTable(defectTableModel);
        JScrollPane defectScroll = new JScrollPane(defectTable);
        defectScroll.getViewport().setBackground(Theme.PANEL_BG);

        // 3. Historial de proyectos (resúmenes Post-Mortem)
        String[] historyCols = {"Proyecto", "Resultado", "LOC Est.", "LOC Real", "Error Tamaño", "Min Est.", "Min Real", "Defectos", "Dd", "Yield"};
        historyTableModel = new DefaultTableModel(historyCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable historyTable = createStyledTable(historyTableModel);
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(280);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(110);
        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyScroll.getViewport().setBackground(Theme.PANEL_BG);

        logsTabPane.addTab("⏱️ Bitácora de Tiempos (PSP)", timeScroll);
        logsTabPane.addTab("🐞 Bitácora de Defectos (Tipos 10-100)", defectScroll);
        logsTabPane.addTab("📚 Historial de Proyectos", historyScroll);

        add(logsTabPane, BorderLayout.CENTER);

        updateTables();
    }

    private JLabel createMetricLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_HEADER);
        lbl.setForeground(Theme.TEXT_PRIMARY);
        return lbl;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(Theme.PANEL_BG);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setFont(Theme.FONT_REGULAR);
        table.setRowHeight(24);
        table.setGridColor(Theme.BORDER_COLOR);
        table.getTableHeader().setBackground(Theme.PANEL_SECONDARY);
        table.getTableHeader().setForeground(Theme.ACCENT_CYAN);
        table.getTableHeader().setFont(Theme.FONT_BOLD);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        return table;
    }

    public void updateTables() {
        // Actualizar métricas
        int totalMins = gameEngine.getPspLog().getTotalTimeMinutes();
        int totalDefects = gameEngine.getPspLog().getTotalDefects();
        double density = gameEngine.getPspLog().calculateDefectDensity();
        int loc = gameEngine.getPspLog().getActualLOC();

        lblTotalTime.setText("⏱️ Tiempo Total: " + totalMins + " min");
        lblTotalDefects.setText("🐛 Defectos: " + totalDefects + " (pendientes: " + gameEngine.getPspLog().getPendingDefects() + ")");
        lblDefectDensity.setText(String.format("📐 Dd: %.1f def/KLOC | Yield: %.0f%%", density, gameEngine.getPspLog().calculateYield()));
        lblDefectDensity.setToolTipText("Dd = 1000 × defectos / LOC.  Yield = % de defectos removidos antes de la entrega.");
        lblLocInfo.setText("📝 LOC real: " + loc + " / estimado: " + gameEngine.getPspLog().getEstimatedLOC());
        lblLocInfo.setToolTipText("Las LOC reales crecen con el avance del proyecto. Compara con tu estimado (PSP 1).");
        lblPipCount.setText("💡 Ideas en PIP: " + gameEngine.getHero().getPipCount() + " mejoras");
        lblPipCount.setToolTipText(pipTooltip());

        // Actualizar tabla de tiempos
        timeTableModel.setRowCount(0);
        List<TimeLogEntry> times = gameEngine.getPspLog().getTimeLogs();
        for (TimeLogEntry t : times) {
            timeTableModel.addRow(new Object[]{
                t.getDate(), t.getStartTime(), t.getEndTime(),
                t.getInterruptionMinutes(), t.getDeltaMinutes(),
                t.getActivity(), t.getComments(),
                t.isCompleted() ? "X" : "", t.getUnits()
            });
        }

        // Actualizar tabla de defectos
        defectTableModel.setRowCount(0);
        List<DefectLogEntry> defects = gameEngine.getPspLog().getDefectLogs();
        for (DefectLogEntry d : defects) {
            defectTableModel.addRow(new Object[]{
                d.getDate(), d.getNumber(),
                d.getType().toString(),
                d.getPhaseInjected(), d.getPhaseRemoved(),
                d.getFixTimeMinutes(),
                d.isFixed() ? "Sí" : "No",
                d.getDescription()
            });
        }

        updateHistory();
    }

    private void updateHistory() {
        historyTableModel.setRowCount(0);
        for (ProjectSummary p : gameEngine.getPspLog().getHistory()) {
            historyTableModel.addRow(new Object[]{
                p.getProjectName(), p.isDelivered() ? "✅ Entregado" : "❌ Fallido",
                p.getEstimatedLOC(), p.getActualLOC(), String.format("%+.0f%%", p.getSizeErrorPercent()),
                p.getEstimatedMinutes(), p.getActualMinutes(),
                p.getTotalDefects() + (p.getPendingDefects() > 0 ? " (" + p.getPendingDefects() + " pend.)" : ""),
                String.format("%.1f", p.getDefectDensity()), String.format("%.0f%%", p.getYield())
            });
        }
    }

    /** Lista de ideas del Plan de Mejora Personal para mostrarlas al pasar el ratón. */
    private String pipTooltip() {
        List<String> ideas = gameEngine.getHero().getPipIdeas();
        if (ideas.isEmpty()) return "Aún no registras ideas de mejora.";
        StringBuilder sb = new StringBuilder("<html><b>Plan de Mejora Personal:</b>");
        for (int i = 0; i < ideas.size(); i++) {
            String safe = ideas.get(i).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
            sb.append("<br>").append(i + 1).append(". ").append(safe);
        }
        return sb.append("</html>").toString();
    }

    private void showAddPipDialog() {
        String idea = JOptionPane.showInputDialog(
            this,
            "Introduce una idea para tu Plan de Mejora Personal (PIP):\n(Ejemplo: 'Usar checklists antes de compilar para reducir bugs de tipo 20')",
            "Plan de Mejora Personal (PIP) - PSP 0.1",
            JOptionPane.PLAIN_MESSAGE
        );
        if (idea != null && !idea.trim().isEmpty()) {
            boolean registered = gameEngine.registerPIP(idea.trim());
            if (registered) {
                JOptionPane.showMessageDialog(this,
                    "¡Mejora registrada con éxito!\nHas reflexionado en tu proceso personal: +25 HP restaurados y +25 puntos organizacionales.",
                    "PIP Registrado", JOptionPane.INFORMATION_MESSAGE);
                updateTables();
                onStateUpdated.run();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No tienes suficiente disciplina (Requiere 20 MP para reflexionar en el PIP).",
                    "Esfuerzo Insuficiente", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}

