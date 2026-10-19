package com.softwarerpg.engine;

import com.softwarerpg.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SaveManagerTest {

    private static final BattleEngine.BattleListener QUIET = new BattleEngine.BattleListener() {
        @Override public void onBattleLog(String message) { }
        @Override public void onBattleEnded(boolean victory) { }
    };

    @TempDir
    Path dir;

    /** Juega la misión 1 completa para tener progreso que guardar. */
    private GameEngine playedGame() {
        GameEngine g = new GameEngine("Ada Lovelace", "Analítica S.A.", new FixedRandom(0.5));
        g.startMission(g.getMissions().get(0), QUIET);
        while (!g.getCurrentBattle().isBattleOver()) g.getCurrentBattle().actionWriteCode();
        g.registerPIP("Revisar con checklist antes de compilar");
        g.buyDesignTemplate();
        g.getOrg().upgradeBSI();
        g.trainTeamMemberPSP(g.getTeam().get(2));
        return g;
    }

    @Test
    void guardarYCargarConservaElProgreso() throws IOException {
        GameEngine original = playedGame();
        Path file = dir.resolve("partida.properties");

        SaveManager.save(original, file);
        GameEngine loaded = SaveManager.load(file);

        Hero a = original.getHero(), b = loaded.getHero();
        assertEquals(a.getName(), b.getName());
        assertEquals(a.getLevel(), b.getLevel());
        assertEquals(a.getXp(), b.getXp());
        assertEquals(a.getGold(), b.getGold());
        assertEquals(a.getHp(), b.getHp());
        assertEquals(a.getMaxMp(), b.getMaxMp());
        assertEquals(a.getPspLevel(), b.getPspLevel());
        assertEquals(a.getCmmiLevel(), b.getCmmiLevel());
        assertEquals(a.getChecklists(), b.getChecklists());
        assertEquals(a.getDesignTemplates(), b.getDesignTemplates());
        assertEquals(a.getPipIdeas(), b.getPipIdeas());

        assertEquals(original.getOrg().getCompanyName(), loaded.getOrg().getCompanyName());
        assertEquals(original.getOrg().getBudget(), loaded.getOrg().getBudget());
        assertEquals(original.getOrg().getInfrastructureLevel(), loaded.getOrg().getInfrastructureLevel());
        assertEquals(original.getOrg().getProcessPoints(), loaded.getOrg().getProcessPoints());

        assertEquals(original.getTeam().get(2).getPspTraining(), loaded.getTeam().get(2).getPspTraining());
        assertTrue(loaded.getMissions().get(0).isCompleted());
        assertFalse(loaded.getMissions().get(1).isCompleted());

        assertEquals(1, loaded.getPspLog().getHistory().size());
        ProjectSummary s = loaded.getPspLog().getHistory().get(0);
        assertEquals(original.getPspLog().getHistory().get(0).getActualLOC(), s.getActualLOC());
        assertEquals(original.getPspLog().getHistory().get(0).getYield(), s.getYield(), 1e-9);
        assertFalse(loaded.isBattleInProgress());
    }

    @Test
    void noSePuedeGuardarAMitadDeUnCombate() {
        GameEngine g = new GameEngine("H", "O");
        g.startMission(g.getMissions().get(0), QUIET);

        assertThrows(IllegalStateException.class, () -> SaveManager.save(g, dir.resolve("p.properties")));
    }

    @Test
    void unaPartidaDanadaSeReportaConUnMensajeClaro() throws IOException {
        Path file = dir.resolve("danada.properties");
        Files.writeString(file, "version=1\nhero.name=X\n", StandardCharsets.UTF_8);

        IOException e = assertThrows(IOException.class, () -> SaveManager.load(file));
        assertTrue(e.getMessage().contains("Partida dañada"));
    }

    @Test
    void unaVersionDistintaNoSeCarga() throws IOException {
        Path file = dir.resolve("vieja.properties");
        Files.writeString(file, "version=99\n", StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> SaveManager.load(file));
    }
}
