package com.softwarerpg.engine;

import com.softwarerpg.model.*;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de balance: simula campañas completas (10 misiones) con tres estilos de juego.
 * Si se cambian las estadísticas de GameData o las reglas del combate, este test avisa
 * cuando la dificultad se sale del rango acordado.
 */
class CampaignBalanceTest {

    private static final int CAMPAIGNS = 200;
    private static final int PROCESS = 0, NAIVE = 1, FARMER = 2;
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

    /** Farmero: se cura con checklists en cuanto baja de la mitad de vida. */
    private static void farmTurn(BattleEngine b, Hero h, PSPLog log, int turn) {
        if (h.getHp() < h.getMaxHp() * 0.5 && h.getChecklists() > 0) b.actionInspectCode();
        else processTurn(b, h, log, turn);
    }

    private void fight(GameEngine g, int profile) {
        BattleEngine b = g.getCurrentBattle();
        Hero h = g.getHero();
        for (int t = 0; !b.isBattleOver() && t < 300; t++) {
            if (profile == PROCESS) processTurn(b, h, g.getPspLog(), t);
            else if (profile == NAIVE) naiveTurn(b, h);
            else farmTurn(b, h, g.getPspLog(), t);
        }
    }

    /** @return % de victorias al primer intento por misión (entre las campañas que llegaron a ella). */
    private double[] firstTryWinRates(int profile, long seed) {
        Random random = new Random(seed);
        int missions = new GameEngine("H", "O").getMissions().size();
        int[] reached = new int[missions];
        int[] firstTry = new int[missions];
        for (int c = 0; c < CAMPAIGNS; c++) {
            GameEngine g = new GameEngine("H", "O", random);
            Hero h = g.getHero();
            for (int m = 0; m < missions; m++) {
                reached[m]++;
                boolean won = false;
                for (int attempt = 1; attempt <= 30 && !won; attempt++) {
                    if (profile == FARMER) {
                        for (int k = 0; k < 5; k++) {           // 5 cazas rápidas antes de cada intento
                            h.rest();
                            g.startRandomPracticeBattle(h.getCmmiLevel().getLevelNumber(), listener);
                            fight(g, profile);
                        }
                    }
                    h.rest();
                    int wanted = profile == PROCESS ? 3 : profile == FARMER ? Hero.MAX_CHECKLISTS : 0;
                    while (h.getChecklists() < wanted && g.buyChecklist()) { }
                    g.startMission(g.getMissions().get(m), listener);
                    fight(g, profile);
                    won = lastResult;
                    if (won && attempt == 1) firstTry[m]++;
                }
                if (!won) break;
            }
        }
        double[] rates = new double[missions];
        for (int m = 0; m < missions; m++) rates[m] = reached[m] == 0 ? 0 : 100.0 * firstTry[m] / reached[m];
        return rates;
    }

    @Test
    void jugarConProcesoEsRetadorPeroGanable() {
        double[] rates = firstTryWinRates(PROCESS, 42);
        assertEquals(10, rates.length);
        assertTrue(rates[0] >= 90 && rates[1] >= 90, "las misiones 1 y 2 son introductorias");
        for (int m = 2; m < rates.length; m++) {
            assertTrue(rates[m] >= 50 && rates[m] <= 90,
                    String.format("misión %d: %.1f%% de victoria al primer intento (esperado 50-90%%)", m + 1, rates[m]));
        }
    }

    @Test
    void sinProcesoNoSeAvanzaMasAllaDeLasPrimerasMisiones() {
        double[] rates = firstTryWinRates(NAIVE, 7);
        assertTrue(rates[3] < 20, String.format("un novato que solo codifica gana la misión 4 el %.1f%%", rates[3]));
    }

    @Test
    void farmearNoVuelveTrivialesLosJefesFinales() {
        double[] rates = firstTryWinRates(FARMER, 11);
        double lastBosses = (rates[7] + rates[8] + rates[9]) / 3;
        assertTrue(lastBosses < 85, String.format("farmeando, las misiones 8-10 se ganan el %.1f%% al primer intento", lastBosses));
    }
}
