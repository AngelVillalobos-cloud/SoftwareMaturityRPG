package com.softwarerpg.engine;

import com.softwarerpg.data.GameData;
import com.softwarerpg.model.*;
import java.util.List;

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

    private Mission activeMission;
    private BattleEngine currentBattle;

    public GameEngine(String heroName, String companyName) {
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

    public void startMission(Mission mission, BattleEngine.BattleListener listener) {
        this.activeMission = mission;
        mission.getBossEnemy().resetHp();
        this.pspLog.resetForNewProject(mission.getEstimatedLoc(), mission.getEstimatedMinutes());

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
                        mission.setCompleted(true);
                        org.addProcessPoints(mission.getRewardProcessPoints());
                        // Si la misión corresponde al siguiente nivel CMMI, evaluamos ascenso
                        if (mission.getCmmiStage() == hero.getCmmiLevel().getLevelNumber()
                            && hero.getCmmiLevel().getNext() != hero.getCmmiLevel()) {
                            hero.setCmmiLevel(hero.getCmmiLevel().getNext());
                            listener.onBattleLog("🏆 ¡LA ORGANIZACIÓN HA ALCANZADO EL " 
                                    + hero.getCmmiLevel().getName() + " (" + hero.getCmmiLevel().getSubtitle() + ") DE CMMI!");
                        }
                    }
                    listener.onBattleEnded(victory);
                }
            }
        );
    }

    public void startRandomPracticeBattle(int stage, BattleEngine.BattleListener listener) {
        Enemy enemy = GameData.createRandomBug(stage);
        this.activeMission = null;
        this.currentBattle = new BattleEngine(hero, enemy, pspLog, org, team, listener);
    }

    public boolean buyChecklist() {
        int cost = 60;
        if (hero.getGold() >= cost) {
            hero.spendGold(cost);
            hero.addChecklists(1);
            return true;
        }
        return false;
    }

    public boolean buyDesignTemplate() {
        int cost = 120;
        if (hero.getGold() >= cost) {
            hero.spendGold(cost);
            hero.addDesignTemplates(1);
            return true;
        }
        return false;
    }

    public boolean registerPIP(String idea) {
        if (hero.consumeMp(20)) {
            hero.addPip(1);
            hero.heal(25);
            org.addProcessPoints(25);
            return true;
        }
        return false;
    }

    public boolean trainTeamMemberPSP(TeamMember member) {
        int cost = 150;
        if (hero.getGold() >= cost && member.getPspTraining().getNext() != member.getPspTraining()) {
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
}

