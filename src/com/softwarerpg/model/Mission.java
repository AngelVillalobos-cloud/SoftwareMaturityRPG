package com.softwarerpg.model;

/**
 * Representa un proyecto o misión de desarrollo de software en el RPG.
 */
public class Mission {
    private final String id;
    private final String title;
    private final String client;
    private final String description;
    private final int estimatedLoc;
    private final int estimatedMinutes;
    private final int cmmiStage; // Nivel CMMI al que pertenece (1 a 5)
    private final Enemy bossEnemy;
    private final int rewardXp;
    private final int rewardGold;
    private final int rewardProcessPoints;
    private final String tacticalHint;
    private boolean completed;

    public Mission(String id, String title, String client, String description,
                   int estimatedLoc, int estimatedMinutes, int cmmiStage,
                   Enemy bossEnemy, int rewardXp, int rewardGold, int rewardProcessPoints) {
        this(id, title, client, description, estimatedLoc, estimatedMinutes, cmmiStage,
             bossEnemy, rewardXp, rewardGold, rewardProcessPoints,
             "Planifica con cuidado, usa checklists de inspección y coordina con tu equipo TSP.");
    }

    public Mission(String id, String title, String client, String description,
                   int estimatedLoc, int estimatedMinutes, int cmmiStage,
                   Enemy bossEnemy, int rewardXp, int rewardGold, int rewardProcessPoints,
                   String tacticalHint) {
        this.id = id;
        this.title = title;
        this.client = client;
        this.description = description;
        this.estimatedLoc = estimatedLoc;
        this.estimatedMinutes = estimatedMinutes;
        this.cmmiStage = cmmiStage;
        this.bossEnemy = bossEnemy;
        this.rewardXp = rewardXp;
        this.rewardGold = rewardGold;
        this.rewardProcessPoints = rewardProcessPoints;
        this.tacticalHint = tacticalHint;
        this.completed = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getClient() { return client; }
    public String getDescription() { return description; }
    public int getEstimatedLoc() { return estimatedLoc; }
    public int getEstimatedMinutes() { return estimatedMinutes; }
    public int getCmmiStage() { return cmmiStage; }
    public Enemy getBossEnemy() { return bossEnemy; }
    public int getRewardXp() { return rewardXp; }
    public int getRewardGold() { return rewardGold; }
    public int getRewardProcessPoints() { return rewardProcessPoints; }
    public String getTacticalHint() { return tacticalHint; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}

