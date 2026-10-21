package com.softwarerpg.engine;

import com.softwarerpg.data.GameData;
import com.softwarerpg.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BattleEngineTest {

    /** Listener que registra los mensajes y el resultado del combate. */
    static class RecordingListener implements BattleEngine.BattleListener {
        final List<String> logs = new ArrayList<>();
        Boolean result;

        @Override public void onBattleLog(String message) { logs.add(message); }
        @Override public void onBattleEnded(boolean victory) { result = victory; }
    }

    private Hero hero;
    private PSPLog log;
    private MoProSoftOrg org;
    private RecordingListener listener;

    @BeforeEach
    void setUp() {
        hero = new Hero("Tester");
        log = new PSPLog();
        log.resetForNewProject("Proyecto de prueba", 100, 120);
        org = new MoProSoftOrg("QA SA");
        listener = new RecordingListener();
    }

    /** Enemigo de prueba: 20 de ataque, 5 de defensa. */
    private Enemy enemy(int hp) {
        return new Enemy("Bug de prueba", "Defecto", hp, 20, 5, DefectType.FUNCION, 100, 50, "¡Hola!", true);
    }

    private BattleEngine battle(Enemy enemy, Random random) {
        return new BattleEngine(hero, enemy, log, org, GameData.createInitialTeam(), listener, random);
    }

    // ------------------------------------------------------------------ Codificar y contraataque

    @Test
    void codificarDanaAlEnemigoYElEnemigoContraataca() {
        Enemy e = enemy(500);
        BattleEngine b = battle(e, new FixedRandom(0.5));

        b.actionWriteCode();

        // Daño = 22 + 5×nivel(1) = 27, menos 5 de defensa
        assertEquals(478, e.getHp());
        // Ataque 20 sin mejoras de Gerencia (GER)
        assertEquals(80, hero.getHp());
        assertEquals(1, b.getTurnCount());
    }

    @Test
    void codificarGeneraDisciplinaYPlanearLaGasta() {
        BattleEngine b = battle(enemy(5000), new FixedRandom(0.5));
        hero.consumeMp(hero.getMp()); // sin MP

        b.actionEstimateSize();
        assertEquals(0, b.getTurnCount(), "sin MP no se puede planear");

        b.actionWriteCode();
        b.actionWriteCode();
        assertEquals(2 * BattleEngine.CODING_MP_GAIN, hero.getMp());

        b.actionEstimateSize();
        assertEquals(2 * BattleEngine.CODING_MP_GAIN - BattleEngine.COST_PLAN, hero.getMp(), "planear ya no devuelve más MP del que cuesta");
    }

    @Test
    void lasMejorasDeOperacionYGerenciaSeNotanEnCombate() {
        org.upgrade(MoProSoftOrg.Area.OPE);
        org.upgrade(MoProSoftOrg.Area.GER);
        Enemy e = enemy(500);
        BattleEngine b = battle(e, new FixedRandom(0.5));

        b.actionWriteCode();

        assertEquals(500 - (27 + 5 - 5), e.getHp(), "OPE: +5 de daño");
        assertEquals(100 - 18, hero.getHp(), "GER: -8% de daño recibido (20 → 18)");
    }

    @Test
    void losDefectosPendientesAgravanElAtaqueEnemigo() {
        BattleEngine b = battle(enemy(500), new FixedRandom(0.5));
        log.logInjectedDefect(DefectType.FUNCION, "Codificación", "a");
        log.logInjectedDefect(DefectType.FUNCION, "Codificación", "b");

        b.actionWriteCode();

        // 20 + 2 pendientes × 4 = 28
        assertEquals(72, hero.getHp());
    }

    // ------------------------------------------------------------------ #1 Ciclo de vida de defectos

    @Test
    void codificarConPspBajoInyectaDefectosPendientes() {
        BattleEngine b = battle(enemy(500), new FixedRandom(0.0)); // siempre inyecta

        b.actionWriteCode();

        assertEquals(1, log.getTotalDefects());
        DefectLogEntry d = log.getDefectLogs().get(0);
        assertFalse(d.isFixed(), "un defecto recién inyectado no está corregido");
        assertEquals(PSPLog.PENDING_PHASE, d.getPhaseRemoved());
    }

    @Test
    void laChecklistRemueveDefectosPendientesSinCrearNuevos() {
        BattleEngine b = battle(enemy(500), new FixedRandom(0.5));
        for (int i = 0; i < 4; i++) log.logInjectedDefect(DefectType.SINTAXIS, "Codificación", "d" + i);

        b.actionInspectCode();

        assertEquals(4, log.getTotalDefects(), "la revisión no debe registrar defectos nuevos");
        assertEquals(1, log.getPendingDefects(), "una checklist remueve hasta 3 defectos");
        assertEquals("Revisión de código", log.getDefectLogs().get(0).getPhaseRemoved());
        assertEquals(1, hero.getChecklists());
    }

    @Test
    void cadaChecklistExtraEnElMismoProyectoCuraMenos() {
        BattleEngine b = battle(enemy(5000), new FixedRandom(0.5));
        hero.addChecklists(3);

        hero.takeDamage(80); int before = hero.getHp(); b.actionInspectCode();
        int firstHeal = hero.getHp() - before + 20;   // + el contraataque de ese turno (20)
        hero.rest(); hero.takeDamage(60); before = hero.getHp(); b.actionInspectCode();
        int secondHeal = hero.getHp() - before + 20;

        assertEquals(25, firstHeal, "PSP 0: curación base completa");
        assertEquals(12, secondHeal, "la segunda revisión cura la mitad");
    }

    @Test
    void elInventarioDeChecklistsTieneTope() {
        hero.addChecklists(100);
        assertEquals(Hero.MAX_CHECKLISTS, hero.getChecklists());
    }

    @Test
    void laChecklistSinDefectosPendientesNoRegistraNada() {
        BattleEngine b = battle(enemy(500), new FixedRandom(0.5));

        b.actionInspectCode();

        assertEquals(0, log.getTotalDefects());
        assertEquals(100.0, log.calculateYield());
    }

    @Test
    void elGestorDeCalidadRemueveDefectosPendientes() {
        BattleEngine b = battle(enemy(500), new FixedRandom(0.5));
        for (int i = 0; i < 3; i++) log.logInjectedDefect(DefectType.DATOS, "Codificación", "d" + i);

        b.actionTeamSynergy(TSPRole.GESTOR_CALIDAD);

        assertEquals(3, log.getTotalDefects());
        assertEquals(1, log.getPendingDefects());
    }

    // ------------------------------------------------------------------ Acciones fallidas

    @Test
    void sinChecklistsLaInspeccionNoConsumeTurno() {
        Enemy e = enemy(500);
        BattleEngine b = battle(e, new FixedRandom(0.5));
        hero.useChecklist();
        hero.useChecklist();

        b.actionInspectCode();

        assertEquals(0, b.getTurnCount());
        assertEquals(100, hero.getHp(), "el enemigo no debe atacar si la acción falla");
        assertTrue(log.getTimeLogs().isEmpty());
    }

    @Test
    void elControlEstadisticoRequiereCmmi4() {
        Enemy e = enemy(500);
        BattleEngine b = battle(e, new FixedRandom(0.5));

        b.actionStatisticalControl();

        assertEquals(500, e.getHp());
        assertEquals(0, b.getTurnCount());
    }

    // ------------------------------------------------------------------ #3 Control Estadístico

    @Test
    void controlEstadisticoConYieldPerfectoHaceDanoMaximo() {
        hero.setCmmiLevel(CMMILevel.NIVEL_4);
        Enemy e = enemy(500);
        BattleEngine b = battle(e, new FixedRandom(0.5));

        b.actionStatisticalControl();

        assertEquals(500 - (100 - 5), e.getHp(), "60 + 40 × yield(100%) = 100, menos defensa");
    }

    @Test
    void controlEstadisticoConDefectosPendientesHaceDanoBase() {
        hero.setCmmiLevel(CMMILevel.NIVEL_4);
        Enemy e = enemy(500);
        BattleEngine b = battle(e, new FixedRandom(0.5));
        for (int i = 0; i < 15; i++) log.logInjectedDefect(DefectType.FUNCION, "Codificación", "d" + i);

        b.actionStatisticalControl();

        assertEquals(500 - (60 - 5), e.getHp(), "más defectos ya no dan más daño (antes 60 + Dd×2)");
    }

    // ------------------------------------------------------------------ #4 Administrador de Requerimientos

    @Test
    void laLineaBaseDeConfiguracionEvitaDefectos() {
        BattleEngine b = battle(enemy(5000), new FixedRandom(0.0)); // sin línea base siempre inyecta

        b.actionTeamSynergy(TSPRole.GESTOR_REQUERIMIENTOS);

        for (int i = 0; i < 3; i++) b.actionWriteCode();
        assertEquals(0, log.getTotalDefects(), "3 codificaciones protegidas");

        b.actionWriteCode();
        assertEquals(1, log.getTotalDefects(), "la 4a codificación ya no está protegida");
    }

    @Test
    void elGestorDePlanificacionReduceALaMitadDosContraataques() {
        BattleEngine b = battle(enemy(5000), new FixedRandom(0.5));

        b.actionTeamSynergy(TSPRole.GESTOR_PLANIFICACION); // 20 → 10
        b.actionWriteCode();                              // 20 → 10
        b.actionWriteCode();                              // ya sin colchón: 20

        assertEquals(100 - 10 - 10 - 20, hero.getHp());
    }

    @Test
    void laSinergiaEsMasFuerteConMasEntrenamientoPsp() {
        List<TeamMember> team = GameData.createInitialTeam();
        TeamMember dev = team.get(1); // Gestor de Desarrollo
        Enemy e = enemy(500);
        BattleEngine b = new BattleEngine(hero, e, log, org, team, listener, new FixedRandom(0.5));
        int expected = (int) Math.round(45 * BattleEngine.synergyPower(dev)) - 5;

        b.actionTeamSynergy(TSPRole.GESTOR_DESARROLLO);

        assertEquals(500 - expected, e.getHp());
        dev.upgradePsp();
        assertTrue(BattleEngine.synergyPower(dev) > 1.0 + 0.10 * PSPLevel.PSP1.ordinal());
    }

    // ------------------------------------------------------------------ #2 LOC y planeación

    @Test
    void planearReestimaElTamanoYProtegeTresCodificaciones() {
        BattleEngine b = battle(enemy(5000), new FixedRandom(0.0));
        // PSP 0: tamaño real = estimado × (1 + 15% de sesgo) = 115
        assertEquals(115, log.getFinalLOC());

        b.actionEstimateSize();

        assertEquals(108, log.getEstimatedLOC(), "el estimado se acerca a la mitad del camino del real");
        for (int i = 0; i < 3; i++) b.actionWriteCode();
        assertEquals(0, log.getTotalDefects(), "planear y diseñar protege 3 codificaciones");
        b.actionWriteCode();
        assertEquals(1, log.getTotalDefects());
    }

    @Test
    void planearSeHaceUnaVezPorProyectoYPotenciaTresAtaques() {
        Enemy e = enemy(5000);
        BattleEngine b = battle(e, new FixedRandom(0.5));

        b.actionEstimateSize();
        int turns = b.getTurnCount();
        b.actionEstimateSize();
        assertEquals(turns, b.getTurnCount(), "la segunda planeación no gasta turno");

        for (int i = 0; i < 4; i++) b.actionWriteCode();
        // 3 ataques planeados: 27 × 1.3 = 35 (-5 def = 30); el 4o normal: 27 - 5 = 22
        assertEquals(5000 - 3 * 30 - 22, e.getHp());
    }

    @Test
    void lasLocRealesCrecenConElAvanceDelProyecto() {
        Enemy e = enemy(250);
        BattleEngine b = battle(e, new FixedRandom(0.5));
        assertEquals(0, log.getActualLOC());

        b.actionWriteCode(); // 22 de 250 HP = 8.8% del proyecto

        assertEquals(Math.round(115 * 22 / 250.0), log.getActualLOC());
    }

    // ------------------------------------------------------------------ #5 Bitácora de tiempos

    @Test
    void laBitacoraDeTiemposEsContinuaYUsaFasesPsp() {
        BattleEngine b = battle(enemy(5000), new FixedRandom(0.5));

        b.actionEstimateSize();
        b.actionWriteCode();
        b.actionInspectCode();

        List<TimeLogEntry> times = log.getTimeLogs();
        assertEquals(3, times.size(), "una entrada por acción del jugador, ninguna por turno enemigo");
        assertEquals("Planeación", times.get(0).getActivity());
        assertEquals("Codificación", times.get(1).getActivity());
        assertEquals("Revisión de código", times.get(2).getActivity());
        for (int i = 1; i < times.size(); i++) {
            assertEquals(times.get(i - 1).getEndTime(), times.get(i).getStartTime(), "cada actividad empieza donde terminó la anterior");
        }
        assertEquals(10 + 15 + 10, log.getTotalTimeMinutes());
    }

    // ------------------------------------------------------------------ Fin del combate

    @Test
    void victoriaOtorgaRecompensasYArchivaElProyecto() {
        BattleEngine b = battle(enemy(20), new FixedRandom(0.5));

        b.actionWriteCode();

        assertTrue(b.isBattleOver());
        assertEquals(Boolean.TRUE, listener.result);
        assertEquals(100, hero.getXp());
        assertEquals(300 + 60, hero.getGold(), "50 de oro + 20% de bono de calidad (sin defectos pendientes)");
        assertTrue(b.hasQualityBonus());
        assertEquals(115, log.getActualLOC(), "al entregar, las LOC reales llegan al tamaño final");
        assertEquals(1, log.getHistory().size());
        assertTrue(log.getHistory().get(0).isDelivered());
        TimeLogEntry last = log.getTimeLogs().get(log.getTimeLogs().size() - 1);
        assertEquals("Post-Mortem", last.getActivity());
        assertTrue(last.isCompleted());
    }

    @Test
    void derrotaNotificaAlListenerYArchivaProyectoFallido() {
        BattleEngine b = battle(enemy(5000), new FixedRandom(0.5));
        hero.takeDamage(99);

        b.actionWriteCode();

        assertEquals(Boolean.FALSE, listener.result);
        assertFalse(log.getHistory().get(0).isDelivered());
    }

    @Test
    void noSePuedeActuarDespuesDeTerminarElCombate() {
        BattleEngine b = battle(enemy(20), new FixedRandom(0.5));
        b.actionWriteCode();
        int turns = b.getTurnCount();
        int logs = log.getTimeLogs().size();

        b.actionWriteCode();
        b.actionInspectCode();

        assertEquals(turns, b.getTurnCount());
        assertEquals(logs, log.getTimeLogs().size());
    }
}
