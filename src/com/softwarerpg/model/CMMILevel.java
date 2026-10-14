package com.softwarerpg.model;

/**
 * Niveles de Madurez de CMMI (Capability Maturity Model Integration).
 * Diapositivas 3 a 9 del PDF de CMMI.
 */
public enum CMMILevel {
    NIVEL_1(1, "Inicial", "Caótico y Ad-hoc", 
            "El éxito depende del heroísmo individual. Se abandonan procesos en crisis. Excede tiempo y presupuesto.",
            "Menor calidad / Alto Riesgo",
            "Heroísmo Individual", 
            0.15, 0),
    NIVEL_2(2, "Gestionado", "Administrado", 
            "Gestión de requisitos, proyectos planificados, medidos y controlados. Los planes se mantienen bajo estrés.",
            "Baja calidad / Alto Riesgo",
            "Gestión de Requisitos & QA", 
            0.30, 200),
    NIVEL_3(3, "Definido", "Estandarizado y Proactivo", 
            "Procesos estándar organizacionales. Normas, procedimientos, herramientas y métodos. Gestión de riesgos y prevención.",
            "Calidad media / Riesgo medio",
            "Estandarización Organizacional", 
            0.50, 500),
    NIVEL_4(4, "Cuantitativamente Gestionado", "Previsible y Estadístico", 
            "Subprocesos clave controlados mediante técnicas estadísticas. Eliminación de causas especiales de variación.",
            "Mayor calidad / Menor riesgo",
            "Control Estadístico de Procesos", 
            0.75, 900),
    NIVEL_5(5, "Optimización", "Mejora Continua e Innovación", 
            "Foco en mejora continua, causas comunes de variación e innovaciones tecnológicas para satisfacer objetivos de negocio.",
            "Máxima calidad / Menor riesgo",
            "Innovación y Mejora Continua", 
            1.00, 1500);

    private final int levelNumber;
    private final String name;
    private final String subtitle;
    private final String description;
    private final String resultProfile;
    private final String perk;
    private final double efficiencyBonus;
    private final int requiredOrganizationPoints;

    CMMILevel(int levelNumber, String name, String subtitle, String description,
              String resultProfile, String perk, double efficiencyBonus, int requiredOrganizationPoints) {
        this.levelNumber = levelNumber;
        this.name = name;
        this.subtitle = subtitle;
        this.description = description;
        this.resultProfile = resultProfile;
        this.perk = perk;
        this.efficiencyBonus = efficiencyBonus;
        this.requiredOrganizationPoints = requiredOrganizationPoints;
    }

    public int getLevelNumber() { return levelNumber; }
    public String getName() { return name; }
    public String getSubtitle() { return subtitle; }
    public String getDescription() { return description; }
    public String getResultProfile() { return resultProfile; }
    public String getPerk() { return perk; }
    public double getEfficiencyBonus() { return efficiencyBonus; }
    public int getRequiredOrganizationPoints() { return requiredOrganizationPoints; }

    public CMMILevel getNext() {
        CMMILevel[] vals = values();
        int idx = ordinal();
        if (idx + 1 < vals.length) {
            return vals[idx + 1];
        }
        return this;
    }
}

