package com.softwarerpg.model;

/**
 * Roles del Team Software Process (TSP).
 * Diapositivas 5 y 6 del PDF de TSP.
 */
public enum TSPRole {
    LIDER_EQUIPO(
        "Líder del Equipo",
        "Dirige al equipo, asegura reporte de datos de procesos, cumplimiento de planes y elabora reportes semanales.",
        "Motivación de Equipo: +25% de esfuerzo y regenera energía colectiva"
    ),
    GESTOR_DESARROLLO(
        "Gestor de Desarrollo",
        "Guía al equipo en la arquitectura, diseño y desarrollo técnico del producto de software.",
        "Ataque de Código Limpio: inflige daño masivo a bugs y reduce deuda técnica"
    ),
    GESTOR_PLANIFICACION(
        "Gestor de Planificación",
        "Apoya en estimación de tamaño/esfuerzo, calendarización de tareas y seguimiento del trabajo.",
        "Presupuesto Temporal: Reduce turnos requeridos y optimiza uso de puntos de esfuerzo"
    ),
    GESTOR_CALIDAD(
        "Gestor de Calidad / Proceso",
        "Administra el plan de calidad, genera estándares de trabajo, modera inspecciones y revisa artefactos.",
        "Inspección por Pares: Inmuniza contra ataques críticos y detecta debilidades del enemigo"
    ),
    GESTOR_REQUERIMIENTOS(
        "Administrador de Requerimientos y Soporte",
        "Dirige especificación de requerimientos, soporte técnico y gestiona el plan de configuración de software.",
        "Control de Configuración: Bloquea mutación o multiplicación de defectos"
    );

    private final String title;
    private final String description;
    private final String skillEffect;

    TSPRole(String title, String description, String skillEffect) {
        this.title = title;
        this.description = description;
        this.skillEffect = skillEffect;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSkillEffect() { return skillEffect; }
}

