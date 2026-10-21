package com.softwarerpg.model;

/**
 * Miembro del equipo de desarrollo (TSP - Team Software Process).
 * Todo ingeniero del equipo está entrenado en PSP (diapositiva 11 de TSP).
 */
public class TeamMember {
    private final String name;
    private TSPRole role;
    private PSPLevel pspTraining;
    private boolean activeInParty;

    public TeamMember(String name, TSPRole role, PSPLevel pspTraining) {
        this.name = name;
        this.role = role;
        this.pspTraining = pspTraining;
        this.activeInParty = true;
    }

    public String getName() { return name; }
    public TSPRole getRole() { return role; }
    public void setRole(TSPRole role) { this.role = role; }
    public PSPLevel getPspTraining() { return pspTraining; }
    public void setPspTraining(PSPLevel pspTraining) { this.pspTraining = pspTraining; }
    public boolean isActiveInParty() { return activeInParty; }
    public void setActiveInParty(boolean activeInParty) { this.activeInParty = activeInParty; }

    /** Restaura el entrenamiento al cargar una partida guardada. */
    public void restoreState(PSPLevel pspTraining) {
        this.pspTraining = pspTraining;
    }

    public void upgradePsp() {
        this.pspTraining = this.pspTraining.getNext();
    }
}

