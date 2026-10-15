package com.softwarerpg.engine;

import com.softwarerpg.model.*;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de balance: simula campañas completas (misiones 1 a 5) con dos estilos de juego.
 * Si se cambian las estadísticas de GameData o las reglas del combate, este test avisa
 * cuando la dificultad se sale del rango acordado.
 */
class CampaignBalanceTest {

    private static final int CAMPAIGNS = 300;
    private boolean lastResult;

    private final BattleEngine.BattleListener listener = new BattleEngine.BattleListener() {
        @Override public void onBattleLog(String message) { }
        @Override public void onBattleEnded(boolean victory) { lastResult = victory; }
    };

    /** Jugador disciplinado: revisa defectos, estima con plantillas, usa TSP y CMMI. */
    private static void processTurn(BattleEngine b, Hero h, PSPLog log, int turn) {
        int cmmi = h.getCmmiLevel().getLevelNumber();
        double hp = (double) h.getHp() / h.getMaxHp();
        if (hp < 0.35 && h.getChecklists() > 0) b.actionInspectCode();
        else if (hp < 0.35 && h.getMp() >= 20) b.actionTeamSynergy(TSPRole.LIDER_EQUIPO);
        else if (log.getPendingDefects() >= 2 && h.getChecklists() > 0 && hp < 0.75) b.actionInspectCode();
        else if (cmmi >= 5 && h.getMp() >= 30 && hp < 0.6) b.actionContinuousImprovement();
        else if (cmmi >= 4 && h.getMp() >= 25) b.actionStatisticalControl();
        else if (turn == 0 && h.getMp() >= 15) b.actionEstimateSize();
        else if (h.getMp() >= 20) b.actionTeamSynergy(TSPRole.GESTOR_DESARROLLO);
        else b.actionWriteCode();
    }

    /** Jugador novato: solo codifica y se cura cuando está a punto de caer. */
    private static void naiveTurn(BattleEngine b, Hero h) {
        if (h.getHp() < h.getMaxHp() * 0.30 && h.getChecklists() > 0) b.actionInspectCode();
        else b.actionWriteCode();
    }

    /** @return % de victorias al primer intento por misión (campañas que llegaron a ella). */
    private double[] firstTryWinRates(boolean process, long seed) {
        int[] reached = new int[5];
        int[] firstTry = new int[5];
        Random random = new Random(seed);
        for (int c = 0; c < CAMPAIGNS; c++) {
            GameEngine g = new GameEngine("H", "O", random);
            Hero h = g.getHero();
            for (int m = 0; m < 5; m++) {
                reached[m]++;
                boolean won = false;
                for (int attempt = 1; attempt <= 30 && !won; attempt++) {
                    h.rest();
                    if (process) while (h.getGold() >= 60 && h.getChecklists() < 3) g.buyChecklist();
                    g.startMission(g.getMissions().get(m), listener);
                    BattleEngine b = g.getCurrentBattle();
                    for (int t = 0; !b.isBattleOver() && t < 300; t++) {
                        if (process) processTurn(b, h, g.getPspLog(), t); else naiveTurn(b, h);
                    }
                    won = lastResult;
                    if (won && attempt == 1) firstTry[m]++;
                }
                if (!won) break;
            }
        }
        double[] rates = new double[5];
        for (int m = 0; m < 5; m++) rates[m] = reached[m] == 0 ? 0 : 100.0 * firstTry[m] / reached[m];
        return rates;
    }

    @Test
    void jugarConProcesoEsRetadorPeroGanable() {
        double[] rates = firstTryWinRates(true, 42);
        assertTrue(rates[0] >= 90 && rates[1] >= 90, "las misiones 1 y 2 son introductorias");
        for (int m = 2; m < 5; m++) {
            assertTrue(rates[m] >= 55 && rates[m] <= 95,
                    String.format("misión %d: %.1f%% de victoria al primer intento (esperado 55-95%%)", m + 1, rates[m]));
        }
    }

    @Test
    void sinProcesoNoSeSuperaLaMision3() {
        double[] rates = firstTryWinRates(false, 7);
        assertTrue(rates[2] < 20, String.format("un novato que solo codifica gana la misión 3 el %.1f%%", rates[2]));
    }
}
