package com.softwarerpg.model;

/**
 * Tipos de defectos según la clasificación de Chillagere e IBM (presente en PSP).
 */
public enum DefectType {
    DOCUMENTACION(10, "Documentación", "Comentarios, mensajes confusos o desactualizados", 1.0),
    SINTAXIS(20, "Sintaxis", "Ortografía, puntuación, erratas, formato de instrucciones", 1.1),
    CONSTRUCCION_PAQUETES(30, "Construir / Paquetes", "Gestión del cambio, librerías, control de versión", 1.2),
    ASIGNACION(40, "Asignación", "Declaración, nombres duplicados, ámbito, límites de variables", 1.3),
    INTERFAZ(50, "Interfaz", "Llamadas a procedimientos y referencias, E/S, formatos de usuario", 1.4),
    CHEQUEO(60, "Chequeo", "Mensajes de error ausentes, validaciones inadecuadas", 1.3),
    DATOS(70, "Datos", "Estructura corrupta, contenido inválido o desbordamiento", 1.5),
    FUNCION(80, "Función / Lógica", "Lógica errónea, punteros nulos, bucles infinitos, recursión", 1.8),
    SISTEMA(90, "Sistema", "Configuración, temporización de hilos, fuga de memoria", 2.0),
    ENTORNO(100, "Entorno", "Problemas de compilación, diseño de arquitectura y plataforma", 2.2);

    private final int code;
    private final String name;
    private final String description;
    private final double severityMultiplier;

    DefectType(int code, String name, String description, double severityMultiplier) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.severityMultiplier = severityMultiplier;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getSeverityMultiplier() {
        return severityMultiplier;
    }

    public static DefectType getByCode(int code) {
        for (DefectType t : values()) {
            if (t.code == code) return t;
        }
        return SINTAXIS;
    }

    @Override
    public String toString() {
        return "[" + code + "] " + name;
    }
}

