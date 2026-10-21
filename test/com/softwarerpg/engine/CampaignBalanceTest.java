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

    /** Jugador disciplinado: planea al inicio, corrige defectos, usa a su equipo TSP y las acciones CMMI. */
    private static void processTurn(BattleEngine b, Hero h, PSPLog log, int turn) {
        int cmmi = h.getCmmiLevel().getLevelNumber();
        double hp = (double) h.getHp() / h.getMaxHp();
        int mp = h.getMp();
        if (hp < 0.35 && h.getChecklists() > 0) b.actionInspectCode();
        else if (hp < 0.35 && mp >= BattleEngine.COST_SYNERGY) b.actionTeamSynergy(TSPRole.LIDER_EQUIPO);
        else if (log.getPendingDefects() >= 2 && h.getChecklists() > 0) b.actionInspectCode();
        else if (cmmi >= 5 && mp >= BattleEngine.COST_CONTINUOUS && hp < 0.6) b.actionContinuousImprovement();
        else if (turn == 0 && mp >= BattleEngine.COST_PLAN) b.actionEstimateSize();
        else if (cmmi >= 4 && mp >= BattleEngine.COST_STAT) b.actionStatisticalControl();
        else if (mp >= BattleEngine.COST_SYNERGY) b.actionTeamSynergy(TSPRole.GESTOR_DESARROLLO);
        else b.actionWriteCode();
    }

    /**
     * Entre misiones invierte el oro: guarda para 3 checklists y gasta el resto en lo más barato
     * entre entrenar al equipo (Desarrollo, Líder, Calidad) y mejorar la empresa (OPE, GER, DIR).
     */
    private static void invest(GameEngine g) {
        Hero h = g.getHero();
        MoProSoftOrg org = g.getOrg();
        while (true) {
            int reserve = GameEngine.CHECKLIST_COST * Math.max(0, 3 - h.getChecklists());
            int best = Integer.MAX_VALUE;
            Runnable buy = null;
            for (int i : new int[]{1, 0, 3}) {
                TeamMember m = g.getTeam().get(i);
                int cost = g.trainingCost(m);
                if (m.getPspTraining().getNext() != m.getPspTraining() && cost < best) {
                    best = cost;
                    buy = () -> g.trainTeamMemberPSP(m);
                }
            }
            for (MoProSoftOrg.Area a : new MoProSoftOrg.Area[]{MoProSoftOrg.Area.OPE, MoProSoftOrg.Area.GER, MoProSoftOrg.Area.DIR}) {
                if (!org.isMaxed(a) && org.upgradeCost(a) < best) {
                    best = org.upgradeCost(a);
                    buy = () -> g.upgradeOrg(a);
                }
            }
            if (buy == null || h.getGold() - best < reserve) return;
            buy.run();
        }
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
                        for (int k = 0; k < 3; k++) {           // 3 cazas rápidas antes de cada intento
                            h.rest();
                            g.startRandomPracticeBattle(h.getCmmiLevel().getLevelNumber(), listener);
                            fight(g, profile);
                        }
                    }
                    h.rest();
                    if (profile != NAIVE) invest(g);
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
        System.out.println("PROCESO " + java.util.Arrays.toString(rates));
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
        System.out.println("NOVATO  " + java.util.Arrays.toString(rates));
        assertTrue(rates[2] < 20, String.format("un novato que solo codifica gana la misión 3 el %.1f%%", rates[2]));
    }

    @Test
    void farmearNoVuelveTrivialesLosJefesFinales() {
        double[] rates = firstTryWinRates(FARMER, 11);
        System.out.println("FARMERO " + java.util.Arrays.toString(rates));
        double lastBosses = (rates[7] + rates[8] + rates[9]) / 3;
        assertTrue(lastBosses < 85, String.format("farmeando, las misiones 8-10 se ganan el %.1f%% al primer intento", lastBosses));
        for (int m = 2; m < rates.length; m++) {
            assertTrue(rates[m] < 100 || m == 5 || m == 2, String.format("farmeando, la misión %d no debería ser segura (%.1f%%)", m + 1, rates[m]));
        }
    }
}
