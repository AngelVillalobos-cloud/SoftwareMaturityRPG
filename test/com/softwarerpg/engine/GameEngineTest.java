package com.softwarerpg.engine;

import com.softwarerpg.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    private static final BattleEngine.BattleListener QUIET = new BattleEngine.BattleListener() {
        @Override public void onBattleLog(String message) { }
        @Override public void onBattleEnded(boolean victory) { }
    };

    @Test
    void cadaMisionEmpiezaConBitacorasLimpias() {
        GameEngine g = new GameEngine("H", "O", new FixedRandom(0.0)); // siempre inyecta defectos
        g.startMission(g.getMissions().get(0), QUIET);
        g.getCurrentBattle().actionWriteCode();
        assertTrue(g.getPspLog().getTotalDefects() > 0);

        g.startMission(g.getMissions().get(1), QUIET);

        assertEquals(0, g.getPspLog().getTotalDefects(), "los defectos no se arrastran entre proyectos");
        assertTrue(g.getPspLog().getTimeLogs().isEmpty());
        assertEquals(g.getMissions().get(1).getTitle(), g.getPspLog().getProjectName());
    }

    /** Gana la misión indicada a base de codificar (con vida completa para no perder). */
    private static void win(GameEngine g, int index) {
        g.getHero().rest();
        g.startMission(g.getMissions().get(index), QUIET);
        BattleEngine b = g.getCurrentBattle();
        while (!b.isBattleOver()) { g.getHero().rest(); b.actionWriteCode(); }
    }

    @Test
    void soloElJefeDelNivelAsciendeCmmi() {
        GameEngine g = new GameEngine("H", "O", new FixedRandom(0.5));

        win(g, 0); // misión intermedia del nivel 1
        assertTrue(g.getMissions().get(0).isCompleted());
        assertEquals(CMMILevel.NIVEL_1, g.getHero().getCmmiLevel(), "la misión intermedia no asciende");

        win(g, 1); // jefe del nivel 1
        assertEquals(CMMILevel.NIVEL_2, g.getHero().getCmmiLevel());
        assertFalse(g.isBattleInProgress());
    }

    @Test
    void repetirUnaMisionCompletadaPagaUnCuartoYNoDaPuntosDeProceso() {
        GameEngine g = new GameEngine("H", "O", new FixedRandom(0.5));
        win(g, 0);
        int gold = g.getHero().getGold();
        int points = g.getOrg().getProcessPoints();
        int fullGold = g.getMissions().get(0).getBossEnemy().getGoldReward();

        win(g, 0);

        assertEquals(Math.round(fullGold * GameEngine.REPEAT_MISSION_REWARD), g.getHero().getGold() - gold);
        assertTrue(g.getOrg().getProcessPoints() - points < 30, "sin los puntos de proceso de la misión");
    }

    @Test
    void lasCazasRapidasPaganCadaVezMenosHastaCompletarUnaMisionNueva() {
        GameEngine g = new GameEngine("H", "O", new FixedRandom(0.5));
        assertEquals(1.0, g.getPracticeRewardMultiplier(), 1e-9);
        g.startRandomPracticeBattle(1, QUIET);
        assertEquals(0.5, g.getPracticeRewardMultiplier(), 1e-9);
        g.startRandomPracticeBattle(1, QUIET);
        assertEquals(0.25, g.getPracticeRewardMultiplier(), 1e-9);

        win(g, 0);

        assertEquals(1.0, g.getPracticeRewardMultiplier(), 1e-9, "una misión nueva renueva las cazas");
    }

    @Test
    void entrenarAlEquipoCuestaMasEnCadaNivel() {
        GameEngine g = new GameEngine("H", "O");
        TeamMember m = g.getTeam().get(2); // inicia en PSP 0.1
        int first = g.trainingCost(m);
        m.upgradePsp();
        assertTrue(g.trainingCost(m) > first);
    }

    @Test
    void laCazaRapidaNoDejaUnaMisionActivaObsoleta() {
        GameEngine g = new GameEngine("H", "O", new FixedRandom(0.5));
        g.startMission(g.getMissions().get(0), QUIET);

        g.startRandomPracticeBattle(1, QUIET);

        assertNull(g.getActiveMission());
        assertTrue(g.getPspLog().getProjectName().startsWith("Caza rápida"));
        assertTrue(g.isBattleInProgress());
    }

    @Test
    void registrarPipGuardaLaIdeaYRechazaIdeasVacias() {
        GameEngine g = new GameEngine("H", "O");

        assertFalse(g.registerPIP("   "));
        assertTrue(g.registerPIP("Usar checklist antes de compilar"));

        assertEquals(1, g.getHero().getPipCount());
        assertEquals("Usar checklist antes de compilar", g.getHero().getPipIdeas().get(0));
    }
}
