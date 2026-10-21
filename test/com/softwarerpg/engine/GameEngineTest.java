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
    void repetirUnaMisionCompletadaPagaUnCuarto() {
        GameEngine g = new GameEngine("H", "O", new FixedRandom(0.5));
        win(g, 0);
        int gold = g.getHero().getGold();
        int fullGold = g.getMissions().get(0).getBossEnemy().getGoldReward();

        win(g, 0);

        // FixedRandom(0.5) no inyecta defectos: siempre aplica el bono de calidad (+20%)
        assertEquals(Math.round(fullGold * GameEngine.REPEAT_MISSION_REWARD * 1.2), g.getHero().getGold() - gold);
    }

    @Test
    void lasCazasRapidasPaganCadaVezMenosHastaSubirDeNivelCmmi() {
        GameEngine g = new GameEngine("H", "O", new FixedRandom(0.5));
        assertEquals(1.0, g.getPracticeRewardMultiplier(), 1e-9);
        g.startRandomPracticeBattle(1, QUIET);
        assertEquals(0.5, g.getPracticeRewardMultiplier(), 1e-9);
        g.startRandomPracticeBattle(1, QUIET);
        assertEquals(0.25, g.getPracticeRewardMultiplier(), 1e-9);

        win(g, 0);
        assertEquals(0.25, g.getPracticeRewardMultiplier(), 1e-9, "la misión intermedia no renueva las cazas");

        win(g, 1); // jefe del nivel 1
        assertEquals(1.0, g.getPracticeRewardMultiplier(), 1e-9, "subir de nivel CMMI renueva las cazas");
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
    void registrarPipGuardaLaIdeaYSoloPagaXpUnaVezPorProyecto() {
        GameEngine g = new GameEngine("H", "O", new FixedRandom(0.5));

        assertEquals(-1, g.registerPIP("   "));
        assertEquals(0, g.registerPIP("Antes de jugar no hay proyecto que analizar"));

        win(g, 0);
        int xp = g.getHero().getXp();
        assertEquals(GameEngine.PIP_XP, g.registerPIP("Usar checklist antes de compilar"));
        assertEquals(0, g.registerPIP("Otra idea del mismo proyecto"));
        assertEquals(xp + GameEngine.PIP_XP, g.getHero().getXp());

        assertEquals(3, g.getHero().getPipCount());
        assertEquals("Usar checklist antes de compilar", g.getHero().getPipIdeas().get(1));
    }

    @Test
    void lasRecompensasDelCodiceSoloSeDanUnaVezPorTarjeta() {
        GameEngine g = new GameEngine("H", "O");
        int gold = g.getHero().getGold();

        assertTrue(g.awardCodexReward("quiz.0", 40, 30));
        assertFalse(g.awardCodexReward("quiz.0", 40, 30), "repetir la misma pregunta no paga");
        assertTrue(g.awardCodexReward("quiz.1", 40, 30));

        assertEquals(gold + 60, g.getHero().getGold());
    }

    @Test
    void elAnalisisDePatronesNoDaRecompensas() {
        GameEngine g = new GameEngine("H", "O", new FixedRandom(0.0)); // siempre inyecta defectos
        g.startMission(g.getMissions().get(0), QUIET);
        g.getCurrentBattle().actionWriteCode();
        int xp = g.getHero().getXp(), gold = g.getHero().getGold();

        String report = g.analyzeDefectPatterns();

        assertTrue(report.contains("Tipo"));
        assertEquals(xp, g.getHero().getXp());
        assertEquals(gold, g.getHero().getGold());
    }

    @Test
    void lasMejorasMoProSoftSePaganConOroYSubenLaCapacidad() {
        GameEngine g = new GameEngine("H", "O");
        MoProSoftOrg org = g.getOrg();
        g.getHero().addGold(10_000);
        int gold = g.getHero().getGold();
        assertEquals(1, org.getCapabilityLevel());

        assertTrue(g.upgradeOrg(MoProSoftOrg.Area.DIR));
        assertEquals(gold - 150, g.getHero().getGold(), "el primer nivel cuesta 150 de oro");
        assertTrue(org.getGoldMultiplier() > 1.0);

        assertTrue(g.upgradeOrg(MoProSoftOrg.Area.OPE));
        assertTrue(org.getCodingDamageBonus() > 0);
        assertTrue(org.getDefectReduction() > 0);
        assertTrue(g.upgradeOrg(MoProSoftOrg.Area.GER));
        assertTrue(org.getDamageReduction() > 0);

        for (MoProSoftOrg.Area a : MoProSoftOrg.Area.values()) g.upgradeOrg(a);
        assertEquals(5, org.getCapabilityLevel(), "todas las categorías al máximo = Optimizado");
        assertFalse(g.upgradeOrg(MoProSoftOrg.Area.DIR), "no pasa del nivel máximo");
    }

    @Test
    void sinOroNoSePuedeMejorarLaOrganizacion() {
        GameEngine g = new GameEngine("H", "O");
        g.getHero().spendGold(g.getHero().getGold());
        assertFalse(g.upgradeOrg(MoProSoftOrg.Area.GER));
        assertEquals(1, g.getOrg().getLevel(MoProSoftOrg.Area.GER));
    }

    @Test
    void entrenarAUnMiembroHaceMasFuerteSuSinergia() {
        GameEngine g = new GameEngine("H", "O");
        g.getHero().addGold(1000);
        TeamMember m = g.getTeam().get(1); // Gestor de Desarrollo
        double before = BattleEngine.synergyPower(m);

        assertTrue(g.trainTeamMemberPSP(m));

        assertEquals(before + 0.10, BattleEngine.synergyPower(m), 1e-9);
    }

    @Test
    void todasLasMisionesTienenPistasTacticas() {
        GameEngine g = new GameEngine("H", "O");
        for (Mission m : g.getMissions()) {
            assertNotNull(m.getTacticalHint());
            assertFalse(m.getTacticalHint().trim().isEmpty());
            assertNotNull(m.getBossEnemy().getTacticalHint());
        }
    }
}
