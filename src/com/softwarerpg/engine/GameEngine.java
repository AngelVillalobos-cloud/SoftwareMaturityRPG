package com.softwarerpg.engine;

import com.softwarerpg.data.GameData;
import com.softwarerpg.model.*;
import java.util.List;
import java.util.Random;

/**
 * Motor principal del juego RPG de Madurez y Calidad de Software.
 * Orquesta al héroe, el equipo TSP, la organización MoProSoft,
 * el historial de bitácoras PSP y las misiones CMMI.
 */
public class GameEngine {
    private final Hero hero;
    private final MoProSoftOrg org;
    private final List<TeamMember> team;
    private final List<Mission> missions;
    private final PSPLog pspLog;
    private final Random random;

    private Mission activeMission;
    private int practiceHunts = 0; // Cazas rápidas desde la última misión nueva completada

    /** Precios y factores de la economía del juego. */
    public static final int CHECKLIST_COST = 80;
    public static final int TEMPLATE_COST = 120;
    public static final double REPEAT_MISSION_REWARD = 0.25;
    public static final double PRACTICE_REWARD_DECAY = 0.5;
    private BattleEngine currentBattle;

    public GameEngine(String heroName, String companyName) {
        this(heroName, companyName, new Random());
    }

    /** Permite inyectar el generador aleatorio (tests deterministas y simulaciones de balance). */
    public GameEngine(String heroName, String companyName, Random random) {
        this.random = random;
        this.hero = new Hero(heroName);
        this.org = new MoProSoftOrg(companyName);
        this.team = GameData.createInitialTeam();
        this.missions = GameData.createMissions();
        this.pspLog = new PSPLog();
    }

    public Hero getHero() { return hero; }
    public MoProSoftOrg getOrg() { return org; }
    public List<TeamMember> getTeam() { return team; }
    public List<Mission> getMissions() { return missions; }
    public PSPLog getPspLog() { return pspLog; }
    public Mission getActiveMission() { return activeMission; }
    public BattleEngine getCurrentBattle() { return currentBattle; }

    private boolean zeroDefectsStreak = false;

    /** true si hay un combate iniciado que aún no termina. */
    public boolean isBattleInProgress() {
        return currentBattle != null && !currentBattle.isBattleOver();
    }

    public boolean hasZeroDefectsStreak() {
        return zeroDefectsStreak;
    }

    public void setZeroDefectsStreak(boolean streak) {
        this.zeroDefectsStreak = streak;
    }

    /**
     * Multiplicador base de Caza Rápida:
     * - Comienza en 1.0 en Nivel 1 CMMI.
     * - Al superar niveles de campaña (CMMI 2, 3, 4, 5), suma +25% por nivel.
     * - Si se alcanza 100% de corrección de defectos (cero defectos pendientes), suma +50% extra.
     * ¡Esto hace que pase del 100% (125%, 150%, 200%) según pidió el usuario!
     */
    public double getPracticeBaseMultiplier() {
        int cmmiBonusIndex = hero.getCmmiLevel().getLevelNumber() - 1; // 0 en Nivel 1
        double base = 1.0 + cmmiBonusIndex * 0.25;
        if (zeroDefectsStreak) {
            base += 0.50; // Bono del 100% de errores corregidos
        }
        return base;
    }

    /** Fracción de la recompensa de la próxima caza rápida: escalada por nivel y decaimiento. */
    public double getPracticeRewardMultiplier() {
        return getPracticeBaseMultiplier() * Math.max(0.05, Math.pow(PRACTICE_REWARD_DECAY, practiceHunts));
    }

    public int getPracticeHunts() { return practiceHunts; }
    public void setPracticeHunts(int hunts) { this.practiceHunts = Math.max(0, hunts); }

    /** Costo de entrenar a un miembro al siguiente nivel PSP: sube con cada nivel. */
    public int trainingCost(TeamMember member) {
        return 100 + 75 * member.getPspTraining().getNext().ordinal();
    }

    public void startMission(Mission mission, BattleEngine.BattleListener listener) {
        this.activeMission = mission;
        final boolean firstCompletion = !mission.isCompleted();
        mission.getBossEnemy().resetHp();
        this.pspLog.resetForNewProject(mission.getTitle(), mission.getEstimatedLoc(), mission.getEstimatedMinutes());

        this.currentBattle = new BattleEngine(
            hero, mission.getBossEnemy(), pspLog, org, team,
            new BattleEngine.BattleListener() {
                @Override
                public void onBattleLog(String message) {
                    listener.onBattleLog(message);
                }

                @Override
                public void onBattleEnded(boolean victory) {
                    if (victory) {
                        if (pspLog.getPendingDefects() == 0 && pspLog.getTotalDefects() > 0) {
                            zeroDefectsStreak = true;
                        }
                        if (firstCompletion) {
                            mission.setCompleted(true);
                            practiceHunts = 0; // un proyecto nuevo renueva las cazas rápidas
                            org.addProcessPoints(mission.getRewardProcessPoints());
                            // Vencer al jefe del nivel actual asciende a la organización en CMMI
                            if (mission.getBossEnemy().isBoss()
                                && mission.getCmmiStage() == hero.getCmmiLevel().getLevelNumber()
                                && hero.getCmmiLevel().getNext() != hero.getCmmiLevel()) {
                                hero.setCmmiLevel(hero.getCmmiLevel().getNext());
                                listener.onBattleLog("🏆 ¡LA ORGANIZACIÓN HA ALCANZADO EL " 
                                        + hero.getCmmiLevel().getName() + " (" + hero.getCmmiLevel().getSubtitle() + ") DE CMMI!");
                            }
                        }
                    }
                    listener.onBattleEnded(victory);
                }
            },
            random
        );
        if (!firstCompletion) currentBattle.setRewardMultiplier(REPEAT_MISSION_REWARD);
    }

    public void startRandomPracticeBattle(int stage, BattleEngine.BattleListener listener) {
        Enemy enemy = GameData.createRandomBug(stage, hero.getLevel());
        this.activeMission = null;
        // Cada caza rápida es un proyecto pequeño con su propio tamaño estimado
        this.pspLog.resetForNewProject("Caza rápida: " + enemy.getName(), 30 + stage * 30, 30 + stage * 20);
        this.currentBattle = new BattleEngine(hero, enemy, pspLog, org, team, new BattleEngine.BattleListener() {
            @Override public void onBattleLog(String message) { listener.onBattleLog(message); }
            @Override public void onBattleEnded(boolean victory) {
                if (victory && pspLog.getPendingDefects() == 0 && pspLog.getTotalDefects() > 0) {
                    zeroDefectsStreak = true;
                }
                listener.onBattleEnded(victory);
            }
        }, random);
        this.currentBattle.setRewardMultiplier(getPracticeRewardMultiplier());
        practiceHunts++;
    }

    public boolean buyChecklist() {
        int cost = CHECKLIST_COST;
        if (hero.getGold() >= cost && hero.getChecklists() < Hero.MAX_CHECKLISTS) {
            hero.spendGold(cost);
            hero.addChecklists(1);
            return true;
        }
        return false;
    }

    public boolean buyDesignTemplate() {
        int cost = TEMPLATE_COST;
        if (hero.getGold() >= cost) {
            hero.spendGold(cost);
            hero.addDesignTemplates(1);
            return true;
        }
        return false;
    }

    public boolean registerPIP(String idea) {
        if (idea == null || idea.trim().isEmpty()) return false;
        if (hero.consumeMp(20)) {
            hero.addPipIdea(idea.trim());
            hero.heal(25);
            org.addProcessPoints(25);
            return true;
        }
        return false;
    }

    public boolean trainTeamMemberPSP(TeamMember member) {
        if (member.getPspTraining().getNext() == member.getPspTraining()) return false;
        int cost = trainingCost(member);
        if (hero.getGold() >= cost) {
            hero.spendGold(cost);
            member.upgradePsp();
            org.addProcessPoints(35);
            return true;
        }
        return false;
    }

    public boolean upgradeCmmiAppraisal() {
        if (hero.getCmmiLevel().getNext() == hero.getCmmiLevel()) return false;
        CMMILevel next = hero.getCmmiLevel().getNext();
        if (org.getProcessPoints() >= next.getRequiredOrganizationPoints() && org.getCapabilityLevel() >= next.getLevelNumber()) {
            hero.setCmmiLevel(next);
            return true;
        }
        return false;
    }

    /**
     * Utilidad de la Bitácora PSP: Auditar y Consolidar Datos.
     * Revisa la consistencia de tiempos y defectos (PSP pág 9).
     * Otorga recuperación de disciplina (+30 MP), salud (+20 HP) y puntos organizacionales (+35).
     */
    public String auditPspLog() {
        int times = pspLog.getTimeLogs().size();
        int defects = pspLog.getTotalDefects();
        int pending = pspLog.getPendingDefects();
        hero.restoreMp(30);
        hero.heal(20);
        org.addProcessPoints(35);

        if (pending == 0 && defects > 0) {
            hero.addGold(40);
            return "✅ ¡Auditoría Perfecta! Tienes " + times + " fases registradas y 0 defectos pendientes (100% resueltos).\n"
                    + "¡Ganas +30 MP, +20 HP, +35 Pts de Proceso y bono de +40 Oro por Cero Defectos!";
        } else if (pending > 0) {
            return "⚠️ Auditoría completada: " + times + " tiempos registrados, pero tienes " + pending + " defecto(s) sin corregir.\n"
                    + "Recuperas +30 MP y +20 HP. ¡Usa Checklists de Inspección en combate para no arrastrar fallas!";
        } else {
            return "📋 Auditoría inicial realizada: " + times + " actividades en bitácora.\n"
                    + "Recuperas +30 MP, +20 HP y +35 Puntos de Proceso por mantener la disciplina de registro.";
        }
    }

    /**
     * Utilidad de la Bitácora PSP: Analizar Patrones de Defectos (Chillarege 10-100).
     * Identifica el tipo de defecto más repetido y genera una regla de defensa preventiva.
     */
    public String analyzeDefectPatterns() {
        java.util.Map<DefectType, Integer> map = pspLog.getDefectsByType();
        if (map.isEmpty()) {
            return "ℹ️ Aún no hay suficientes defectos registrados en la bitácora para encontrar patrones.\n"
                    + "Desarrolla más código o combate bugs para acumular datos empíricos.";
        }
        DefectType maxType = DefectType.SINTAXIS;
        int maxCount = 0;
        for (java.util.Map.Entry<DefectType, Integer> entry : map.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxType = entry.getKey();
            }
        }
        hero.addXp(30);
        org.addProcessPoints(20);
        return "🧠 ¡Patrón Identificado! El defecto más recurrente es: " + maxType.getName() + " (Tipo " + maxType.getCode() + ") con " + maxCount + " ocurrencia(s).\n"
                + "Diagnóstico: \"" + maxType.getDescription() + "\".\n"
                + "Aplicas una regla de prevención personal en tu proceso: +30 XP y +20 Puntos Organizacionales.";
    }

    /** Recompensa otorgada al estudiar y responder correctamente en el Códice / Anki. */
    public void awardCodexReward(int xp, int gold) {
        hero.addXp(xp);
        hero.addGold(gold);
        org.addProcessPoints(15);
    }
}

