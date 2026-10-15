package com.softwarerpg.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestor del Proceso de Software Personal (PSP):
 * Mantiene bitácoras de tiempo y defectos, calcula métricas de calidad (densidad Dd)
 * y genera el resumen Post-Mortem.
 */
public class PSPLog {
    private final List<TimeLogEntry> timeLogs = new ArrayList<>();
    private final List<DefectLogEntry> defectLogs = new ArrayList<>();
    private int nextDefectId = 1;

    public static final String PENDING_PHASE = "Pendiente";

    // Métricas acumuladas del proyecto
    private int estimatedLOC = 100;
    private int actualLOC = 100;
    private int estimatedMinutes = 120;

    public void addTimeLog(TimeLogEntry entry) {
        timeLogs.add(entry);
    }

    /** Registra un defecto encontrado y corregido en el mismo momento. */
    public void logDefect(DefectType type, String phaseInjected, String phaseRemoved,
                          int fixMinutes, String description) {
        defectLogs.add(new DefectLogEntry(nextDefectId++, type, phaseInjected, phaseRemoved, fixMinutes, true, description));
    }

    /** Registra un defecto inyectado que aún no se ha detectado ni corregido. */
    public void logInjectedDefect(DefectType type, String phaseInjected, String description) {
        defectLogs.add(new DefectLogEntry(nextDefectId++, type, phaseInjected, PENDING_PHASE, 0, false, description));
    }

    /**
     * Una revisión remueve hasta {@code max} defectos pendientes (los más antiguos primero).
     * @return cuántos defectos se removieron
     */
    public int removePendingDefects(int max, String phaseRemoved, int fixMinutesEach) {
        int removed = 0;
        for (DefectLogEntry d : defectLogs) {
            if (removed >= max) break;
            if (!d.isFixed()) {
                d.markRemoved(phaseRemoved, fixMinutesEach);
                removed++;
            }
        }
        return removed;
    }

    public int getPendingDefects() {
        return (int) defectLogs.stream().filter(d -> !d.isFixed()).count();
    }

    public int getRemovedDefects() {
        return getTotalDefects() - getPendingDefects();
    }

    /**
     * Yield del proceso (PSP): porcentaje de defectos removidos antes de la entrega.
     * Sin defectos registrados el yield es 100%.
     */
    public double calculateYield() {
        int total = getTotalDefects();
        if (total == 0) return 100.0;
        return 100.0 * getRemovedDefects() / total;
    }

    public List<TimeLogEntry> getTimeLogs() {
        return Collections.unmodifiableList(timeLogs);
    }

    public List<DefectLogEntry> getDefectLogs() {
        return Collections.unmodifiableList(defectLogs);
    }

    public int getTotalTimeMinutes() {
        return timeLogs.stream().mapToInt(TimeLogEntry::getDeltaMinutes).sum();
    }

    public int getTotalFixMinutes() {
        return defectLogs.stream().mapToInt(DefectLogEntry::getFixTimeMinutes).sum();
    }

    public int getTotalDefects() {
        return defectLogs.size();
    }

    /**
     * Calcula la Densidad de Defectos por KLOC:
     * Dd = 1000 * (D / N) donde D = Defectos encontrados, N = Líneas de código
     * Según diapositiva 14 del PDF de PSP.
     */
    public double calculateDefectDensity() {
        if (actualLOC <= 0) return 0.0;
        return (1000.0 * defectLogs.size()) / actualLOC;
    }

    /**
     * Resumen de defectos clasificados por tipo (Chillagere / IBM)
     */
    public Map<DefectType, Integer> getDefectsByType() {
        Map<DefectType, Integer> map = new HashMap<>();
        for (DefectLogEntry e : defectLogs) {
            map.put(e.getType(), map.getOrDefault(e.getType(), 0) + 1);
        }
        return map;
    }

    public int getEstimatedLOC() { return estimatedLOC; }
    public void setEstimatedLOC(int estimatedLOC) { this.estimatedLOC = estimatedLOC; }

    public int getActualLOC() { return actualLOC; }
    public void setActualLOC(int actualLOC) { this.actualLOC = actualLOC; }

    public int getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public void resetForNewProject(int estimatedLOC, int estimatedMins) {
        this.estimatedLOC = estimatedLOC;
        this.actualLOC = estimatedLOC;
        this.estimatedMinutes = estimatedMins;
    }
}

