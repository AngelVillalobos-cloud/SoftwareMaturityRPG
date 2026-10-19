package com.softwarerpg.model;

/**
 * Representa un enemigo en el combate RPG.
 * Los enemigos son manifestaciones de defectos de software, caos de procesos y crisis de proyectos.
 */
public class Enemy {
    private final String name;
    private final String subtitle;
    private int hp;
    private final int maxHp;
    private final int attackPower;
    private final int defense;
    private final DefectType associatedDefect;
    private final int xpReward;
    private final int goldReward;
    private final String introQuote;
    private final boolean isBoss;

    public Enemy(String name, String subtitle, int maxHp, int attackPower, int defense,
                 DefectType associatedDefect, int xpReward, int goldReward, String introQuote, boolean isBoss) {
        this.name = name;
        this.subtitle = subtitle;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackPower = attackPower;
        this.defense = defense;
        this.associatedDefect = associatedDefect;
        this.xpReward = xpReward;
        this.goldReward = goldReward;
        this.introQuote = introQuote;
        this.isBoss = isBoss;
    }

    public String getName() { return name; }
    public String getSubtitle() { return subtitle; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttackPower() { return attackPower; }
    public int getDefense() { return defense; }
    public DefectType getAssociatedDefect() { return associatedDefect; }
    public int getXpReward() { return xpReward; }
    public int getGoldReward() { return goldReward; }
    public String getIntroQuote() { return introQuote; }
    public boolean isBoss() { return isBoss; }

    public void takeDamage(int dmg) {
        int effective = Math.max(1, dmg - defense);
        this.hp = Math.max(0, this.hp - effective);
    }

    public boolean isDefeated() {
        return hp <= 0;
    }

    public void resetHp() {
        this.hp = maxHp;
    }

    public String getTacticalHint() {
        if (associatedDefect == null) return "Escribe código limpio y revisa tus artefactos con checklists.";
        switch (associatedDefect) {
            case SINTAXIS:
                return "Debilidad: Erratas y formato (Tipo 20). Una 'Checklist PSP' previene sus ataques y 'Codificar' lo destruye con facilidad.";
            case ASIGNACION:
                return "Debilidad: Variables y límites (Tipo 40). Usa 'Estimación LOC (PSP 1)' para debilitar su armadura y recuperar MP.";
            case INTERFAZ:
                return "Debilidad: Formatos y llamadas E/S (Tipo 50). Apóyate en el 'Gestor de Desarrollo' o 'Requerimientos' de TSP.";
            case CONSTRUCCION_PAQUETES:
                return "Debilidad: Control de versiones y librerías (Tipo 30). La 'Inspección por Pares' del Gestor de Calidad TSP lo anula.";
            case DATOS:
                return "Debilidad: Estructuras corruptas (Tipo 70). Re-estima el tamaño (PSP 1) para anticipar desbordamientos.";
            case FUNCION:
                return "Debilidad: Lógica y punteros (Tipo 80). Si tienes CMMI 4 usa 'Control Estadístico' para asestar daño crítico certero.";
            case SISTEMA:
                return "Debilidad: Memoria y sincronización (Tipo 90). Activa 'Mejora Continua (CMMI-5)' o motivación del 'Líder TSP'.";
            case ENTORNO:
                return "Debilidad: Compilación y arquitectura (Tipo 100). Sinergia en equipo TSP y revisiones tempranas.";
            default:
                return "Mantén disciplina PSP: usa checklists para curar el proyecto y sinergias TSP para daño.";
        }
    }
}

