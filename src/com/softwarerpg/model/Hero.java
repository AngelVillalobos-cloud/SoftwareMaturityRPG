package com.softwarerpg.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa al Ingeniero de Software protagonista del RPG.
 * Maneja estadísticas, nivel PSP, nivel CMMI, disciplina y habilidades.
 */
public class Hero {
    private String name;
    private int hp;
    private int maxHp;
    private int mp; // Puntos de Disciplina / Esfuerzo
    private int maxMp;
    private int level;
    private int xp;
    private int gold;

    private PSPLevel pspLevel;
    private CMMILevel cmmiLevel;

    // Recursos de calidad adquiridos
    private int checklists;       // Listas de chequeo PSP 2
    private int designTemplates;  // Plantillas de diseño PSP 2.1
    private final List<String> pipIdeas = new ArrayList<>(); // Plan de Mejora Personal (PIP)

    public Hero(String name) {
        this.name = name;
        this.maxHp = 100;
        this.hp = 100;
        this.maxMp = 60;
        this.mp = 60;
        this.level = 1;
        this.xp = 0;
        this.gold = 300;
        this.pspLevel = PSPLevel.PSP0;
        this.cmmiLevel = CMMILevel.NIVEL_1;
        this.checklists = 2;
        this.designTemplates = 1;
    }

    public void addXp(int amount) {
        this.xp += amount;
        // Subida de nivel PSP (puede subir varios niveles con una recompensa grande)
        while (pspLevel.getNext() != pspLevel && xp >= pspLevel.getNext().getXpRequired()) {
            pspLevel = pspLevel.getNext();
            maxHp += 20;
            maxMp += 15;
            hp = maxHp;
            mp = maxMp;
        }
        // Subida de nivel general del héroe
        while (xp >= xpForLevel(level + 1)) {
            level++;
            maxHp += 25;
            maxMp += 20;
            hp = maxHp;
            mp = maxMp;
        }
    }

    /**
     * XP acumulada necesaria para alcanzar un nivel: 70 × (n-1) × n.
     * Nivel 2 = 140, 3 = 420, 4 = 840, 5 = 1400... Con las recompensas de la campaña
     * se llega a la misión N con nivel N aproximadamente.
     */
    public static int xpForLevel(int targetLevel) {
        return 70 * (targetLevel - 1) * targetLevel;
    }

    public void takeDamage(int dmg) {
        this.hp = Math.max(0, this.hp - dmg);
    }

    public void heal(int amount) {
        this.hp = Math.min(maxHp, this.hp + amount);
    }

    public void restoreMp(int amount) {
        this.mp = Math.min(maxMp, this.mp + amount);
    }

    public boolean consumeMp(int amount) {
        if (mp >= amount) {
            mp -= amount;
            return true;
        }
        return false;
    }

    /** Restaura las estadísticas numéricas al cargar una partida guardada. */
    public void restoreState(int hp, int maxHp, int mp, int maxMp, int level, int xp, int gold,
                             int checklists, int designTemplates) {
        this.maxHp = maxHp;
        this.hp = Math.min(hp, maxHp);
        this.maxMp = maxMp;
        this.mp = Math.min(mp, maxMp);
        this.level = level;
        this.xp = xp;
        this.gold = gold;
        this.checklists = checklists;
        this.designTemplates = designTemplates;
    }

    public void rest() {
        this.hp = maxHp;
        this.mp = maxMp;
    }

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMp() { return mp; }
    public int getMaxMp() { return maxMp; }
    public int getLevel() { return level; }
    public int getXp() { return xp; }
    public int getGold() { return gold; }
    public void addGold(int amount) { this.gold += amount; }
    public void spendGold(int amount) { this.gold = Math.max(0, this.gold - amount); }
    public PSPLevel getPspLevel() { return pspLevel; }
    public void setPspLevel(PSPLevel pspLevel) { this.pspLevel = pspLevel; }
    public CMMILevel getCmmiLevel() { return cmmiLevel; }
    public void setCmmiLevel(CMMILevel cmmiLevel) { this.cmmiLevel = cmmiLevel; }
    public int getChecklists() { return checklists; }
    public void addChecklists(int qty) { this.checklists += qty; }
    public void useChecklist() { if (checklists > 0) checklists--; }
    public int getDesignTemplates() { return designTemplates; }
    public void addDesignTemplates(int qty) { this.designTemplates += qty; }
    public void useDesignTemplate() { if (designTemplates > 0) designTemplates--; }
    public int getPipCount() { return pipIdeas.size(); }
    public List<String> getPipIdeas() { return Collections.unmodifiableList(pipIdeas); }
    public void addPipIdea(String idea) { pipIdeas.add(idea); }
}

