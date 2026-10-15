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

    @Test
    void ganarLaMisionDelNivelActualAsciendeCmmi() {
        GameEngine g = new GameEngine("H", "O", new FixedRandom(0.5));
        g.startMission(g.getMissions().get(0), QUIET);
        BattleEngine b = g.getCurrentBattle();

        while (!b.isBattleOver()) b.actionWriteCode();

        assertTrue(g.getMissions().get(0).isCompleted());
        assertEquals(CMMILevel.NIVEL_2, g.getHero().getCmmiLevel());
        assertFalse(g.isBattleInProgress());
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
