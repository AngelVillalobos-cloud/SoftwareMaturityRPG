package com.softwarerpg.model;

/**
 * Miembro del equipo de desarrollo (TSP - Team Software Process).
 * Todo ingeniero del equipo está entrenado en PSP (diapositiva 11 de TSP).
 */
public class TeamMember {
    private final String name;
    private TSPRole role;
    private PSPLevel pspTraining;
    private int energy;
    private int maxEnergy;
    private boolean activeInParty;

    public TeamMember(String name, TSPRole role, PSPLevel pspTraining) {
        this.name = name;
        this.role = role;
        this.pspTraining = pspTraining;
        this.maxEnergy = 100;
        this.energy = 100;
        this.activeInParty = true;
    }

    public String getName() { return name; }
    public TSPRole getRole() { return role; }
    public void setRole(TSPRole role) { this.role = role; }
    public PSPLevel getPspTraining() { return pspTraining; }
    public void setPspTraining(PSPLevel pspTraining) { this.pspTraining = pspTraining; }
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }
    public boolean isActiveInParty() { return activeInParty; }
    public void setActiveInParty(boolean activeInParty) { this.activeInParty = activeInParty; }

    public void consumeEnergy(int amount) {
        this.energy = Math.max(0, this.energy - amount);
    }

    public void restoreEnergy(int amount) {
        this.energy = Math.min(maxEnergy, this.energy + amount);
    }

    public void upgradePsp() {
        this.pspTraining = this.pspTraining.getNext();
        this.maxEnergy += 20;
        this.energy = maxEnergy;
    }
}

