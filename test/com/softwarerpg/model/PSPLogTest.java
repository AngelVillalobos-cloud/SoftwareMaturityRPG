package com.softwarerpg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PSPLogTest {

    @Test
    void densidadDeDefectosUsaLasLocReales() {
        PSPLog log = new PSPLog();
        log.resetForNewProject("P", 200, 60);
        log.setFinalLOC(200);
        log.updateProgress(1.0);
        log.logInjectedDefect(DefectType.SINTAXIS, "Codificación", "a");
        log.logInjectedDefect(DefectType.SINTAXIS, "Codificación", "b");

        assertEquals(10.0, log.calculateDefectDensity(), 1e-9, "Dd = 1000 × 2 / 200");
    }

    @Test
    void yieldEsElPorcentajeDeDefectosRemovidos() {
        PSPLog log = new PSPLog();
        assertEquals(100.0, log.calculateYield(), "sin defectos el yield es 100%");

        for (int i = 0; i < 4; i++) log.logInjectedDefect(DefectType.DATOS, "Codificación", "d" + i);
        log.removePendingDefects(3, "Revisión de código", 5);

        assertEquals(75.0, log.calculateYield(), 1e-9);
        assertEquals(1, log.getPendingDefects());
    }

    @Test
    void cerrarYReiniciarProyectoConservaElHistorial() {
        PSPLog log = new PSPLog();
        log.resetForNewProject("Proyecto A", 100, 60);
        log.logActivity("Codificación", 15, 0, "c", false);
        log.logInjectedDefect(DefectType.FUNCION, "Codificación", "d");
        log.closeProject(true);

        log.resetForNewProject("Proyecto B", 50, 30);

        assertTrue(log.getTimeLogs().isEmpty());
        assertEquals(0, log.getTotalDefects());
        assertEquals(1, log.getHistory().size());
        ProjectSummary a = log.getHistory().get(0);
        assertEquals("Proyecto A", a.getProjectName());
        assertEquals(15, a.getActualMinutes());
        assertEquals(1, a.getPendingDefects());
    }

    @Test
    void errorDeEstimacionDeTamano() {
        PSPLog log = new PSPLog();
        log.resetForNewProject("P", 100, 60);
        log.setFinalLOC(120);
        log.updateProgress(1.0);

        assertEquals(20.0, log.getSizeEstimationErrorPercent(), 1e-9);
    }
}
