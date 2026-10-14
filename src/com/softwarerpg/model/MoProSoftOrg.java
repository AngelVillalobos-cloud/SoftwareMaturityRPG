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

    // Recursos de Gerencia (GER)
    private int humanResourcesLevel; // RH
    private int infrastructureLevel; // BSI (Bienes, Servicios e Infraestructura)
    private int knowledgeBaseLevel;  // CO (Conocimiento de la Organización)

    // Puntos de proceso acumulados
    private int processPoints;

    public MoProSoftOrg(String companyName) {
        this.companyName = companyName;
        this.capabilityLevel = 1; // Comienza en Realizado
        this.budget = 1000;
        this.humanResourcesLevel = 1;
        this.infrastructureLevel = 1;
        this.knowledgeBaseLevel = 1;
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

    // Getters y Setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public int getCapabilityLevel() { return capabilityLevel; }
    public int getBudget() { return budget; }
    public void addBudget(int amount) { this.budget += amount; }
    public int getHumanResourcesLevel() { return humanResourcesLevel; }
    public int getInfrastructureLevel() { return infrastructureLevel; }
    public int getKnowledgeBaseLevel() { return knowledgeBaseLevel; }
    public int getProcessPoints() { return processPoints; }
    public void addProcessPoints(int pts) { this.processPoints += pts; }
}

