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
                return "Debilidad: Erratas y formato (Tipo 20). 'Codificar' lo destruye con facilidad; la 'Checklist PSP' corrige los defectos que deje.";
            case ASIGNACION:
                return "Debilidad: Variables y límites (Tipo 40). Usa 'Planear y Diseñar (PSP 1)' al inicio: +30% de daño en 3 ataques y sin defectos en 3 codificaciones.";
            case INTERFAZ:
                return "Debilidad: Formatos y llamadas E/S (Tipo 50). Apóyate en el 'Gestor de Desarrollo' o 'Requerimientos' de TSP.";
            case CONSTRUCCION_PAQUETES:
                return "Debilidad: Control de versiones y librerías (Tipo 30). La 'Inspección por Pares' del Gestor de Calidad TSP corrige sus defectos.";
            case DATOS:
                return "Debilidad: Estructuras corruptas (Tipo 70). 'Planear y Diseñar (PSP 1)' antes de codificar evita defectos de datos.";
            case FUNCION:
                return "Debilidad: Lógica y punteros (Tipo 80). Si tienes CMMI 4 usa 'Control Estadístico'; si no, el 'Gestor de Desarrollo' TSP.";
            case SISTEMA:
                return "Debilidad: Memoria y sincronización (Tipo 90). Activa 'Mejora Continua (CMMI-5)' o motivación del 'Líder TSP'.";
            case ENTORNO:
                return "Debilidad: Compilación y arquitectura (Tipo 100). Sinergia en equipo TSP y revisiones tempranas.";
            default:
                return "Mantén disciplina PSP: usa checklists para curar el proyecto y sinergias TSP para daño.";
        }
    }
}

