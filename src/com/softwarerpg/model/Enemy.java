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
}

