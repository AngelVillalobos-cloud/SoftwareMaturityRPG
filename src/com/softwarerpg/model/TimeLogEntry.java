package com.softwarerpg.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entrada de la Bitácora de Tiempos del PSP (Time Recording Log).
 * Conforme al estándar visto en el PDF (pág 17):
 * Fecha, Inicio, Fin, Interrupción (min), Delta Tiempo, Actividad, Comentarios, C (Completada), U (Unidades).
 */
public class TimeLogEntry {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String date;
    private final String startTime;
    private final String endTime;
    private final int interruptionMinutes;
    private final int deltaMinutes;
    private final String activity;
    private final String comments;
    private final boolean completed;
    private final int units;

    public TimeLogEntry(LocalDateTime start, LocalDateTime end, int interruptionMinutes,
                        String activity, String comments, boolean completed, int units) {
        this.date = start.format(DATE_FMT);
        this.startTime = start.format(TIME_FMT);
        this.endTime = end.format(TIME_FMT);
        this.interruptionMinutes = interruptionMinutes;
        int rawMins = (int) java.time.Duration.between(start, end).toMinutes();
        this.deltaMinutes = Math.max(1, rawMins - interruptionMinutes);
        this.activity = activity;
        this.comments = comments;
        this.completed = completed;
        this.units = units;
    }

    public TimeLogEntry(String date, String startTime, String endTime, int interruptionMinutes,
                        int deltaMinutes, String activity, String comments, boolean completed, int units) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.interruptionMinutes = interruptionMinutes;
        this.deltaMinutes = deltaMinutes;
        this.activity = activity;
        this.comments = comments;
        this.completed = completed;
        this.units = units;
    }

    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public int getInterruptionMinutes() { return interruptionMinutes; }
    public int getDeltaMinutes() { return deltaMinutes; }
    public String getActivity() { return activity; }
    public String getComments() { return comments; }
    public boolean isCompleted() { return completed; }
    public int getUnits() { return units; }
}

