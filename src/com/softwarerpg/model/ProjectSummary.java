package com.softwarerpg.model;

/**
 * Resumen Post-Mortem de un proyecto terminado (Project Plan Summary de PSP).
 * El historial de estos resúmenes es la base de datos personal que PSP usa
 * para mejorar las estimaciones de proyectos futuros.
 */
public class ProjectSummary {
    private final String projectName;
    private final boolean delivered;
    private final int estimatedLOC;
    private final int actualLOC;
    private final int estimatedMinutes;
    private final int actualMinutes;
    private final int totalDefects;
    private final int pendingDefects;
    private final double defectDensity;
    private final double yield;

    public ProjectSummary(String projectName, boolean delivered, int estimatedLOC, int actualLOC,
                          int estimatedMinutes, int actualMinutes, int totalDefects, int pendingDefects,
                          double defectDensity, double yield) {
        this.projectName = projectName;
        this.delivered = delivered;
        this.estimatedLOC = estimatedLOC;
        this.actualLOC = actualLOC;
        this.estimatedMinutes = estimatedMinutes;
        this.actualMinutes = actualMinutes;
        this.totalDefects = totalDefects;
        this.pendingDefects = pendingDefects;
        this.defectDensity = defectDensity;
        this.yield = yield;
    }

    public String getProjectName() { return projectName; }
    public boolean isDelivered() { return delivered; }
    public int getEstimatedLOC() { return estimatedLOC; }
    public int getActualLOC() { return actualLOC; }
    public int getEstimatedMinutes() { return estimatedMinutes; }
    public int getActualMinutes() { return actualMinutes; }
    public int getTotalDefects() { return totalDefects; }
    public int getPendingDefects() { return pendingDefects; }
    public double getDefectDensity() { return defectDensity; }
    public double getYield() { return yield; }

    public double getSizeErrorPercent() {
        return estimatedLOC <= 0 ? 0.0 : 100.0 * (actualLOC - estimatedLOC) / estimatedLOC;
    }
}
