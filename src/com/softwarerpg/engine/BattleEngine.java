package com.softwarerpg.engine;

import com.softwarerpg.model.*;
import java.util.Random;

/**
 * Motor de combate por turnos RPG temático de ingeniería de software.
 * Modela ataques de código, uso de listas de chequeo PSP, sinergias de equipo TSP,
 * y técnicas avanzadas de calidad CMMI.
 */
public class BattleEngine {
    public interface BattleListener {
        void onBattleLog(String message);
        void onBattleEnded(boolean victory);
    }

    private final Hero hero;
    private final Enemy enemy;
    private final PSPLog pspLog;
    private final MoProSoftOrg org;
    private final java.util.List<TeamMember> team;
    private final BattleListener listener;
    private final Random random = new Random();

    /** Defectos pendientes que remueve una checklist PSP y una inspección por pares TSP. */
    static final int CHECKLIST_REMOVALS = 3;
    static final int PEER_REVIEW_REMOVALS = 2;
    /** Daño extra del enemigo por cada defecto inyectado que siga sin corregir. */
    static final int PENDING_DEFECT_PENALTY = 2;

    private boolean battleOver = false;
    private int turnCount = 0;
    private boolean enemyDefenseWeakened = false;
    private int continuousImprovementTurns = 0; // Efecto CMMI-5

    public BattleEngine(Hero hero, Enemy enemy, PSPLog pspLog, MoProSoftOrg org,
                        java.util.List<TeamMember> team, BattleListener listener) {
        this.hero = hero;
        this.enemy = enemy;
        this.pspLog = pspLog;
        this.org = org;
        this.team = team;
        this.listener = listener;

        // Tamaño real del producto: el error de estimación baja con la disciplina PSP
        // y los ingenieros sin datos históricos tienden a subestimar.
        int psp = hero.getPspLevel().ordinal();
        double bias = Math.max(0, 0.15 - 0.03 * psp);
        double spread = Math.max(0.05, 0.30 - 0.05 * psp);
        double error = Math.max(-0.4, bias + random.nextGaussian() * spread);
        pspLog.setFinalLOC((int) Math.round(pspLog.getEstimatedLOC() * (1 + error)));

        listener.onBattleLog(">>> ¡Ha aparecido " + enemy.getName() + "! <<<");
        listener.onBattleLog(enemy.getSubtitle());
        listener.onBattleLog("\"" + enemy.getIntroQuote() + "\"");
    }

    public boolean isBattleOver() { return battleOver; }
    public Hero getHero() { return hero; }
    public Enemy getEnemy() { return enemy; }
    public int getTurnCount() { return turnCount; }

    /**
     * Acción 1: Codificación / Ataque directo
     */
    public void actionWriteCode() {
        if (battleOver) return;
        turnCount++;
        int baseDamage = 22 + hero.getLevel() * 5;
        if (enemyDefenseWeakened) {
            baseDamage = (int)(baseDamage * 1.4);
            enemyDefenseWeakened = false;
        }

        // Bono de infraestructura MoProSoft BSI
        baseDamage += org.getInfrastructureLevel() * 3;

        listener.onBattleLog("💻 " + hero.getName() + " escribe módulos de código e inflige " + baseDamage + " de daño.");
        enemy.takeDamage(baseDamage);
        logTime("Codificación", 15, "Implementación de módulos contra " + enemy.getName());

        // Posibilidad de inyectar un defecto si el nivel PSP es bajo
        double defectChance = Math.max(0.08, 0.40 - (hero.getPspLevel().ordinal() * 0.07));
        if (random.nextDouble() < defectChance) {
            DefectType defectType = enemy.getAssociatedDefect() != null ? enemy.getAssociatedDefect() : DefectType.SINTAXIS;
            listener.onBattleLog("⚠️ ¡Alerta! Durante la codificación se inyectó un defecto: " + defectType.getName() + ".");
            pspLog.logInjectedDefect(defectType, "Codificación", "Defecto inyectado durante la codificación");
        }

        checkBattleStatus();
        if (!battleOver) enemyTurn();
    }

    /**
     * Acción 2: Inspección formal con Lista de Chequeo (PSP 2)
     */
    public void actionInspectCode() {
        if (battleOver) return;
        turnCount++;

        if (hero.getChecklists() <= 0) {
            listener.onBattleLog("❌ No tienes Listas de Chequeo (Checklists PSP) disponibles. Adquiere más en la organización.");
            return;
        }

        hero.useChecklist();
        int healAmount = 25 + hero.getPspLevel().ordinal() * 6;
        hero.heal(healAmount);

        // La revisión remueve defectos ya inyectados (no crea defectos nuevos)
        int removed = pspLog.removePendingDefects(CHECKLIST_REMOVALS, "Revisión de código", 5);
        logTime("Revisión de código", 10, "Checklist PSP: " + removed + " defecto(s) removido(s)");

        listener.onBattleLog("📋 Aplicas una Lista de Chequeo PSP 2: Revisión de diseño y código.");
        listener.onBattleLog(removed > 0
                ? "🔍 La revisión encontró y corrigió " + removed + " defecto(s) pendiente(s)."
                : "🔍 La revisión no encontró defectos pendientes: el código está limpio.");
        listener.onBattleLog("✨ Salud del proyecto restaurada en +" + healAmount + " HP.");

        checkBattleStatus();
        if (!battleOver) enemyTurn();
    }

    /**
     * Acción 3: Estimación y Análisis de Tamaño LOC (PSP 1)
     */
    public void actionEstimateSize() {
        if (battleOver) return;
        turnCount++;

        if (!hero.consumeMp(15)) {
            listener.onBattleLog("❌ No tienes suficiente energía de disciplina (Requiere 15 MP).");
            return;
        }

        enemyDefenseWeakened = true;
        hero.restoreMp(25);
        logTime("Planeación", 10, "Estimación de tamaño (LOC) y calendarización");

        // Re-estimación con datos reales del avance (PSP 1): el estimado se acerca al tamaño real
        int oldEstimate = pspLog.getEstimatedLOC();
        int newEstimate = (int) Math.round((oldEstimate + pspLog.getFinalLOC()) / 2.0);
        pspLog.setEstimatedLOC(newEstimate);
        if (newEstimate != oldEstimate) {
            listener.onBattleLog("📏 Re-estimación de tamaño: de " + oldEstimate + " a " + newEstimate + " LOC.");
        }
        listener.onBattleLog("📐 Realizas estimación formal de tamaño (LOC) y planificación de tareas (PSP 1 / 1.1).");
        listener.onBattleLog("🎯 ¡La defensa del enemigo se reduce drásticamente para tu próximo ataque y recuperas disciplina!");

        checkBattleStatus();
        if (!battleOver) enemyTurn();
    }

    /**
     * Acción 4: Sinergia de Equipo TSP
     */
    public void actionTeamSynergy(TSPRole role) {
        if (battleOver) return;
        turnCount++;

        if (!hero.consumeMp(20)) {
            listener.onBattleLog("❌ Requiere 20 MP de esfuerzo disciplinado para coordinar con el equipo.");
            return;
        }

        TeamMember member = team.stream().filter(m -> m.getRole() == role).findFirst().orElse(null);
        String memberName = member != null ? member.getName() : role.getTitle();

        listener.onBattleLog("🤝 ¡Sinergia TSP activada con " + memberName + "!");
        logTime(synergyPhase(role), 10, "Sinergia TSP con " + memberName);

        switch (role) {
            case LIDER_EQUIPO:
                hero.heal(35);
                hero.restoreMp(25);
                listener.onBattleLog("📢 El Líder de Equipo motiva al grupo y revisa los compromisos semanales. (+35 HP, +25 MP).");
                break;
            case GESTOR_DESARROLLO:
                int devDmg = 45 + org.getKnowledgeBaseLevel() * 5;
                enemy.takeDamage(devDmg);
                listener.onBattleLog("⚡ El Gestor de Desarrollo aplica patrones arquitectónicos sólidos e inflige " + devDmg + " de daño puro.");
                break;
            case GESTOR_PLANIFICACION:
                enemyDefenseWeakened = true;
                listener.onBattleLog("📅 El Gestor de Planificación optimiza el cronograma y calendarización. El enemigo pierde iniciativa.");
                break;
            case GESTOR_CALIDAD:
                hero.heal(20);
                enemy.takeDamage(25);
                int found = pspLog.removePendingDefects(PEER_REVIEW_REMOVALS, "Inspección TSP", 5);
                listener.onBattleLog("🛡️ El Gestor de Calidad modera una inspección por pares. Absorbe daño e inflige 25 a los bugs.");
                if (found > 0) listener.onBattleLog("🔍 La inspección por pares corrigió " + found + " defecto(s) pendiente(s).");
                break;
            case GESTOR_REQUERIMIENTOS:
                listener.onBattleLog("📦 El Administrador de Requerimientos fija la línea base de configuración. Los defectos quedan contenidos.");
                break;
        }

        checkBattleStatus();
        if (!battleOver) enemyTurn();
    }

    /**
     * Acción 5: Control Estadístico CMMI-4
     */
    public void actionStatisticalControl() {
        if (battleOver) return;
        turnCount++;

        if (hero.getCmmiLevel().getLevelNumber() < 4) {
            listener.onBattleLog("🔒 Requiere Nivel CMMI 4 (Cuantitativamente Gestionado) para usar Control Estadístico.");
            return;
        }

        if (!hero.consumeMp(25)) {
            listener.onBattleLog("❌ Requiere 25 MP de esfuerzo.");
            return;
        }

        int statDamage = 60 + (int)(pspLog.calculateDefectDensity() * 2);
        enemy.takeDamage(statDamage);
        logTime("Análisis cuantitativo", 10, "Control estadístico de subprocesos (CMMI 4)");
        listener.onBattleLog("📊 ¡Control Cuantitativo CMMI-4! Aplicas técnicas estadísticas a los subprocesos.");
        listener.onBattleLog("💥 Erradicas causas especiales de variación e infliges " + statDamage + " de daño certero.");

        checkBattleStatus();
        if (!battleOver) enemyTurn();
    }

    /**
     * Acción 6: Mejora Continua e Innovación CMMI-5
     */
    public void actionContinuousImprovement() {
        if (battleOver) return;
        turnCount++;

        if (hero.getCmmiLevel().getLevelNumber() < 5) {
            listener.onBattleLog("🔒 Requiere Nivel CMMI 5 (Optimización) para desplegar innovación y mejora continua.");
            return;
        }

        if (!hero.consumeMp(30)) {
            listener.onBattleLog("❌ Requiere 30 MP de esfuerzo.");
            return;
        }

        continuousImprovementTurns = 3;
        hero.heal(50);
        logTime("Mejora de proceso", 10, "Mejora continua e innovación (CMMI 5)");
        listener.onBattleLog("🚀 ¡Optimización CMMI-5! Despliegue de tecnología innovadora y gestión de causas comunes de variación.");
        listener.onBattleLog("🌟 Salud restaurada en +50 HP y regeneración activa durante 3 turnos.");

        checkBattleStatus();
        if (!battleOver) enemyTurn();
    }

    /**
     * Turno del enemigo
     */
    private void enemyTurn() {
        if (battleOver) return;

        // Efecto continuo de regeneración CMMI-5 si está activo
        if (continuousImprovementTurns > 0) {
            continuousImprovementTurns--;
            hero.heal(15);
            listener.onBattleLog("✨ Mejora continua activa: +15 HP regenerados.");
        }

        int rawAtk = enemy.getAttackPower() + random.nextInt(6);
        // Los defectos sin corregir agravan las fallas en producción
        int pending = pspLog.getPendingDefects();
        rawAtk += pending * PENDING_DEFECT_PENALTY;
        // Reducción por madurez de procesos MoProSoft
        double defenseFactor = 1.0 - (org.getCapabilityLevel() * 0.08);
        int finalDamage = Math.max(3, (int)(rawAtk * defenseFactor));

        hero.takeDamage(finalDamage);
        listener.onBattleLog("👾 " + enemy.getName() + " contraataca con [" + enemy.getSubtitle() + "] causando " + finalDamage + " de daño"
                + (pending > 0 ? " (agravado por " + pending + " defecto(s) pendiente(s))." : "."));

        checkBattleStatus();
    }

    /** Registra en la bitácora de tiempos la actividad PSP del turno, con una interrupción de 0 a 3 min. */
    private void logTime(String phase, int minutes, String comment) {
        pspLog.logActivity(phase, minutes, random.nextInt(4), comment, false);
    }

    private static String synergyPhase(TSPRole role) {
        switch (role) {
            case LIDER_EQUIPO: return "Junta de equipo (TSP)";
            case GESTOR_DESARROLLO: return "Diseño";
            case GESTOR_PLANIFICACION: return "Planeación";
            case GESTOR_CALIDAD: return "Inspección (TSP)";
            case GESTOR_REQUERIMIENTOS: return "Gestión de configuración";
            default: return "Trabajo en equipo";
        }
    }

    private void checkBattleStatus() {
        pspLog.updateProgress(1.0 - (double) enemy.getHp() / enemy.getMaxHp());
        if (enemy.isDefeated()) {
            battleOver = true;
            listener.onBattleLog("🎉 ¡VICTORIA! Has derrotado a " + enemy.getName() + ".");
            listener.onBattleLog("⭐ Recompensas obtenidas: +" + enemy.getXpReward() + " XP, +" + enemy.getGoldReward() + " Oro.");

            hero.addXp(enemy.getXpReward());
            hero.addGold(enemy.getGoldReward());
            org.addBudget(enemy.getGoldReward() / 2);
            org.addProcessPoints(30);

            pspLog.logActivity("Post-Mortem", 10, 0, "Proyecto entregado: consolidación de métricas", true);
            listener.onBattleLog(String.format("📏 Post-Mortem: tamaño estimado %d LOC vs real %d LOC (error %+.0f%%); tiempo estimado %d min vs real %d min.",
                    pspLog.getEstimatedLOC(), pspLog.getActualLOC(), pspLog.getSizeEstimationErrorPercent(),
                    pspLog.getEstimatedMinutes(), pspLog.getTotalTimeMinutes()));

            listener.onBattleEnded(true);
        } else if (hero.getHp() <= 0) {
            battleOver = true;
            pspLog.logActivity("Post-Mortem", 10, 0, "Proyecto fallido: análisis de causas", true);
            listener.onBattleLog("💀 DERROTA: El proyecto ha colapsado ante la acumulación de defectos.");
            listener.onBattleLog("⚠️ Revisa tus bitácoras de tiempos y defectos en la pestaña PSP para identificar las fallas.");
            listener.onBattleEnded(false);
        }
    }
}

