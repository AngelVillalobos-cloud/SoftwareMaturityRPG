package com.softwarerpg.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Entrada de la Bitácora de Registro de Defectos de PSP (Defect Recording Log).
 * Conforme a la diapositiva 12 del PDF de PSP:
 * Fecha, Número, Tipo (código Chillagere), Fase Introducido, Fase Eliminado, Tiempo corrección (min), Descripción.
 */
public class DefectLogEntry {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String date;
    private final int number;
    private final DefectType type;
    private final String phaseInjected;
    private final String phaseRemoved;
    private final int fixTimeMinutes;
    private final boolean fixed;
    private final String description;

    public DefectLogEntry(int number, DefectType type, String phaseInjected,
                          String phaseRemoved, int fixTimeMinutes, boolean fixed, String description) {
        this.date = LocalDate.now().format(DATE_FMT);
        this.number = number;
        this.type = type;
        this.phaseInjected = phaseInjected;
        this.phaseRemoved = phaseRemoved;
        this.fixTimeMinutes = fixTimeMinutes;
        this.fixed = fixed;
        this.description = description;
    }

    public String getDate() { return date; }
    public int getNumber() { return number; }
    public DefectType getType() { return type; }
    public String getPhaseInjected() { return phaseInjected; }
    public String getPhaseRemoved() { return phaseRemoved; }
    public int getFixTimeMinutes() { return fixTimeMinutes; }
    public boolean isFixed() { return fixed; }
    public String getDescription() { return description; }
}

