package com.softwarerpg.model;

/**
 * Modelo de la Organización según la estructura MoProSoft (NMX-I-059-NYCE).
 * Diapositivas 6, 7, 8, 9, 10 y 11 del PDF de MoProSoft.
 * 
 * Se organiza en 3 Categorías:
 * 1) Alta Dirección (DIR) -> Gestión de Negocios
 * 2) Gerencia (GER) -> Gestión de Recursos (RH, BSI, CO), Gestión de Procesos, Gestión de Proyectos
 * 3) Operación (OPE) -> Administración de Proyectos Específicos, Desarrollo y Mantenimiento de Software
 */
public class MoProSoftOrg {
    private String companyName;
    private int capabilityLevel; // 0 Incompleto, 1 Realizado, 2 Administrado, 3 Establecido, 4 Predecible, 5 Optimizado
    private int budget; // Presupuesto organizacional en monedas de oro / inversión

    // 1) Alta Dirección (DIR) -> Gestión de Negocios
    private int dirLevel;
    private String strategicFocus; // Equilibrado, Calidad Total, Agilidad, Innovación

    // 2) Recursos de Gerencia (GER)
    private int humanResourcesLevel; // RH
    private int infrastructureLevel; // BSI (Bienes, Servicios e Infraestructura)
    private int knowledgeBaseLevel;  // CO (Conocimiento de la Organización)

    // 3) Operación (OPE) -> Administración de Proyectos y Desarrollo/Mantenimiento
    private int opeLevel;

    // Puntos de proceso acumulados
    private int processPoints;

    public MoProSoftOrg(String companyName) {
        this.companyName = companyName;
        this.capabilityLevel = 1; // Comienza en Realizado
        this.budget = 1000;
        this.dirLevel = 1;
        this.strategicFocus = "Equilibrado";
        this.humanResourcesLevel = 1;
        this.infrastructureLevel = 1;
        this.knowledgeBaseLevel = 1;
        this.opeLevel = 1;
        this.processPoints = 50;
    }

    public String getCapabilityLevelName() {
        switch (capabilityLevel) {
            case 0: return "Nivel 0 - Incompleto";
            case 1: return "Nivel 1 - Realizado";
            case 2: return "Nivel 2 - Administrado (Gestionado)";
            case 3: return "Nivel 3 - Establecido (Definido)";
            case 4: return "Nivel 4 - Predecible";
            case 5: return "Nivel 5 - Optimizado";
            default: return "Nivel " + capabilityLevel;
        }
    }

    public boolean canUpgradeCapability() {
        int cost = capabilityLevel * 400 + 300;
        int reqPoints = capabilityLevel * 150 + 100;
        return capabilityLevel < 5 && budget >= cost && processPoints >= reqPoints;
    }

    public boolean upgradeCapability() {
        if (!canUpgradeCapability()) return false;
        int cost = capabilityLevel * 400 + 300;
        int reqPoints = capabilityLevel * 150 + 100;
        budget -= cost;
        processPoints -= reqPoints;
        capabilityLevel++;
        return true;
    }

    public boolean upgradeDIR() {
        int cost = dirLevel * 160;
        if (budget >= cost) {
            budget -= cost;
            dirLevel++;
            processPoints += 45;
            return true;
        }
        return false;
    }

    public boolean upgradeOPE() {
        int cost = opeLevel * 160;
        if (budget >= cost) {
            budget -= cost;
            opeLevel++;
            processPoints += 45;
            return true;
        }
        return false;
    }

    public boolean upgradeRH() {
        int cost = humanResourcesLevel * 150;
        if (budget >= cost) {
            budget -= cost;
            humanResourcesLevel++;
            processPoints += 40;
            return true;
        }
        return false;
    }

    public boolean upgradeBSI() {
        int cost = infrastructureLevel * 150;
        if (budget >= cost) {
            budget -= cost;
            infrastructureLevel++;
            processPoints += 40;
            return true;
        }
        return false;
    }

    public boolean upgradeCO() {
        int cost = knowledgeBaseLevel * 150;
        if (budget >= cost) {
            budget -= cost;
            knowledgeBaseLevel++;
            processPoints += 50;
            return true;
        }
        return false;
    }

    /** Multiplicador de presupuesto otorgado por la Alta Dirección (DIR): +15% por nivel extra. */
    public double getBudgetMultiplier() {
        return 1.0 + (dirLevel - 1) * 0.15;
    }

    /** Daño extra otorgado por la madurez operativa de desarrollo (OPE). */
    public int getOperationAttackBonus() {
        return (opeLevel - 1) * 4;
    }

    /** Factor de reducción de defectos inyectados gracias a la disciplina de Operación. */
    public double getDefectReductionFactor() {
        return Math.min(0.20, (opeLevel - 1) * 0.05);
    }

    /** Restaura el estado de la organización al cargar una partida guardada (compatibilidad previa). */
    public void restoreState(int capabilityLevel, int budget, int humanResourcesLevel,
                             int infrastructureLevel, int knowledgeBaseLevel, int processPoints) {
        restoreState(capabilityLevel, budget, humanResourcesLevel, infrastructureLevel, knowledgeBaseLevel, processPoints, 1, 1);
    }

    public void restoreState(int capabilityLevel, int budget, int humanResourcesLevel,
                             int infrastructureLevel, int knowledgeBaseLevel, int processPoints,
                             int dirLevel, int opeLevel) {
        this.capabilityLevel = capabilityLevel;
        this.budget = budget;
        this.humanResourcesLevel = humanResourcesLevel;
        this.infrastructureLevel = infrastructureLevel;
        this.knowledgeBaseLevel = knowledgeBaseLevel;
        this.processPoints = processPoints;
        this.dirLevel = Math.max(1, dirLevel);
        this.opeLevel = Math.max(1, opeLevel);
    }

    // Getters y Setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public int getCapabilityLevel() { return capabilityLevel; }
    public int getBudget() { return budget; }
    public void addBudget(int amount) { this.budget += amount; }
    public int getDirLevel() { return dirLevel; }
    public int getOpeLevel() { return opeLevel; }
    public String getStrategicFocus() { return strategicFocus; }
    public void setStrategicFocus(String strategicFocus) { this.strategicFocus = strategicFocus; }
    public int getHumanResourcesLevel() { return humanResourcesLevel; }
    public int getInfrastructureLevel() { return infrastructureLevel; }
    public int getKnowledgeBaseLevel() { return knowledgeBaseLevel; }
    public int getProcessPoints() { return processPoints; }
    public void addProcessPoints(int pts) { this.processPoints += pts; }
}

