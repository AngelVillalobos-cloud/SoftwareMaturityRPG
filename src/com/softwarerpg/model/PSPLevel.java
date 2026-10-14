package com.softwarerpg.model;

/**
 * Niveles de madurez del Personal Software Process (PSP).
 * Basado en la diapositiva 7 del PDF de PSP.
 */
public enum PSPLevel {
    PSP0("PSP 0", "Disciplina y Medición Base", 
         "Proceso actual, registro básico de tiempos y defectos.", 0),
    PSP0_1("PSP 0.1", "Estándares y Plan PIP", 
         "Estándares de código, medición de tamaño y Plan de Mejora Personal (PIP).", 100),
    PSP1("PSP 1", "Estimación y Pruebas", 
         "Estimación de tamaño en LOC y reporte formal de pruebas.", 250),
    PSP1_1("PSP 1.1", "Calendarización de Tareas", 
         "Calendario de planeación, agendamiento y presupuesto semanal.", 450),
    PSP2("PSP 2", "Calidad y Revisiones", 
         "Revisión rigurosa de diseño y código con listas de chequeo (checklists).", 700),
    PSP2_1("PSP 2.1", "Plantillas de Diseño", 
         "Especificaciones formales de diseño y técnicas de análisis profundo.", 1000);

    private final String code;
    private final String title;
    private final String description;
    private final int xpRequired;

    PSPLevel(String code, String title, String description, int xpRequired) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.xpRequired = xpRequired;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getXpRequired() { return xpRequired; }

    public PSPLevel getNext() {
        PSPLevel[] vals = values();
        int idx = ordinal();
        if (idx + 1 < vals.length) {
            return vals[idx + 1];
        }
        return this;
    }
}

