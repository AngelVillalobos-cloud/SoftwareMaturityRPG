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
    private final Random random;

    /** Defectos pendientes que remueve una checklist PSP y una inspección por pares TSP. */
    static final int CHECKLIST_REMOVALS = 3;
    /** Curación de la checklist: base + extra por nivel PSP, dividida entre las revisiones ya hechas. */
    static final int CHECKLIST_BASE_HEAL = 25;
    static final int CHECKLIST_HEAL_PER_PSP = 6;
    static final int PEER_REVIEW_REMOVALS = 2;
    /** Daño extra del enemigo por cada defecto inyectado que siga sin corregir. */
    static final int PENDING_DEFECT_PENALTY = 4;
    /** Control Estadístico: daño base + bonificación proporcional al yield (máx. 100). */
    static final int STAT_BASE_DAMAGE = 60;
    /** Codificaciones protegidas por la línea base de configuración del Administrador de Requerimientos. */
    static final int CONFIG_BASELINE_TURNS = 3;
    /** Codificaciones sin defectos después de planear y diseñar (PSP 1 / 2.1). */
    static final int PLANNED_CODINGS = 3;
    /** Ataques que reciben el bono de daño de la planeación. */
    static final int PLANNED_ATTACKS = 3;
    /** Disciplina (MP) que se gana en cada codificación: trabajar con método la genera. */
    public static final int CODING_MP_GAIN = 8;
    /** Multiplicador de daño de los ataques planeados. */
    static final double PLANNED_DAMAGE_BONUS = 1.3;
    /** Contraataques reducidos a la mitad por el colchón del Gestor de Planificación. */
    static final int SCHEDULE_BUFFER_TURNS = 2;
    /** Bono de oro al entregar sin defectos pendientes (yield 100%). */
    static final double QUALITY_GOLD_BONUS = 0.20;
    /** Costos de disciplina (MP) de cada acción especial. */
    public static final int COST_PLAN = 10;
    public static final int COST_SYNERGY = 20;
    public static final int COST_STAT = 25;
    public static final int COST_CONTINUOUS = 30;
    /** Variación del daño de codificar y del ataque enemigo (±15%). */
    static final double DAMAGE_VARIANCE = 0.15;
    static final int STAT_YIELD_BONUS = 40;

    private boolean battleOver = false;
    private int turnCount = 0;
    private int plannedAttacks = 0;             // Ataques con el bono de la planeación
    private boolean planned = false;            // La planeación se hace una vez por proyecto
    private int continuousImprovementTurns = 0; // Efecto CMMI-5
    private int configBaselineTurns = 0;        // Codificaciones protegidas por la línea base de configuración
    private int halvedEnemyAttacks = 0;         // Contraataques a la mitad (colchón de planificación)
    private int plannedCodings = 0;             // Codificaciones planeadas y diseñadas: sin defectos
    private int checklistsUsed = 0;             // Revisiones hechas en este proyecto (rendimiento decreciente)
    private double rewardMultiplier = 1.0;      // Recompensas reducidas al farmear o repetir proyectos
    private int xpEarned = 0;
    private int goldEarned = 0;
    private boolean qualityBonus = false;

    public BattleEngine(Hero hero, Enemy enemy, PSPLog pspLog, MoProSoftOrg org,
                        java.util.List<TeamMember> team, BattleListener listener) {
        this(hero, enemy, pspLog, org, team, listener, new Random());
    }

    /** Permite inyectar el generador aleatorio (tests deterministas y simulaciones de balance). */
    public BattleEngine(Hero hero, Enemy enemy, PSPLog pspLog, MoProSoftOrg org,
                        java.util.List<TeamMember> team, BattleListener listener, Random random) {
        this.random = random;
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
    public int getXpEarned() { return xpEarned; }
    /** true si ya se planeó este proyecto (Planear se usa una vez, al inicio). */
    public boolean isPlanned() { return planned; }
    public int getGoldEarned() { return goldEarned; }
    /** true si el proyecto se entregó sin defectos pendientes (bono de calidad). */
    public boolean hasQualityBonus() { return qualityBonus; }

    /** Fracción de las recompensas que se otorgan al ganar (1.0 = completas). */
    public void setRewardMultiplier(double multiplier) {
        this.rewardMultiplier = Math.max(0, Math.min(1, multiplier));
    }

    /**
     * Acción 1: Codificación / Ataque directo
     */
    public void actionWriteCode() {
        if (battleOver) return;
        // Daño base + madurez de Operación (OPE) de MoProSoft
        int baseDamage = vary(planned(22 + hero.getLevel() * 5 + org.getCodingDamageBonus()));
        hero.restoreMp(CODING_MP_GAIN);

        listener.onBattleLog("💻 " + hero.getName() + " escribe módulos de código e inflige " + baseDamage + " de daño.");
        enemy.takeDamage(baseDamage);
        logTime("Codificación", 15, "Implementación de módulos contra " + enemy.getName());

        // Posibilidad de inyectar un defecto: baja con el nivel PSP y la madurez de Operación (OPE)
        double defectChance = defectChance();
        if (configBaselineTurns > 0) {
            configBaselineTurns--;
            listener.onBattleLog("🔒 Línea base de configuración activa: los cambios están controlados y no se inyectan defectos.");
        } else if (plannedCodings > 0) {
            plannedCodings--;
            listener.onBattleLog("📑 El código sigue el diseño que planeaste (PSP 2.1): no se inyectan defectos.");
        } else if (random.nextDouble() < defectChance) {
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

        if (hero.getChecklists() <= 0) {
            listener.onBattleLog("❌ No tienes Listas de Chequeo (Checklists PSP) disponibles. Adquiere más en la organización.");
            return;
        }

        hero.useChecklist();
        // Rendimiento decreciente: cada revisión adicional del mismo producto encuentra menos
        // (la 1a cura completo, la 2a la mitad, la 3a un tercio...).
        int baseHeal = CHECKLIST_BASE_HEAL + hero.getPspLevel().ordinal() * CHECKLIST_HEAL_PER_PSP;
        int healAmount = baseHeal / (1 + checklistsUsed);
        checklistsUsed++;
        hero.heal(healAmount);

        // La revisión remueve defectos ya inyectados (no crea defectos nuevos)
        int removed = pspLog.removePendingDefects(CHECKLIST_REMOVALS, "Revisión de código", 5);
        logTime("Revisión de código", 10, "Checklist PSP: " + removed + " defecto(s) removido(s)");

        listener.onBattleLog("📋 Aplicas una Lista de Chequeo PSP 2: Revisión de diseño y código.");
        listener.onBattleLog(removed > 0
                ? "🔍 La revisión encontró y corrigió " + removed + " defecto(s) pendiente(s)."
                : "🔍 La revisión no encontró defectos pendientes: el código está limpio.");
        listener.onBattleLog("✨ Salud del proyecto restaurada en +" + healAmount + " HP"
                + (checklistsUsed > 1 ? " (revisión nº " + checklistsUsed + ": cada revisión extra rinde menos)." : "."));

        checkBattleStatus();
        if (!battleOver) enemyTurn();
    }

    /**
     * Acción 3: Planear y diseñar (PSP 1 / PSP 2.1). Se hace una vez por proyecto:
     * re-estima el tamaño, los siguientes 3 ataques hacen +30% de daño y las siguientes
     * 3 codificaciones siguen el diseño (no inyectan defectos).
     */
    public void actionEstimateSize() {
        if (battleOver) return;

        if (planned) {
            listener.onBattleLog("ℹ️ Este proyecto ya está planeado: la planeación se hace una vez, al inicio.");
            return;
        }
        if (!hero.consumeMp(COST_PLAN)) {
            listener.onBattleLog("❌ No tienes suficiente disciplina (requiere " + COST_PLAN + " MP). Codifica para ganar MP.");
            return;
        }

        planned = true;
        plannedAttacks = PLANNED_ATTACKS;
        plannedCodings = PLANNED_CODINGS;
        logTime("Planeación", 10, "Estimación de tamaño (LOC) y diseño");

        // Re-estimación con datos reales del avance (PSP 1): el estimado se acerca al tamaño real
        int oldEstimate = pspLog.getEstimatedLOC();
        int newEstimate = (int) Math.round((oldEstimate + pspLog.getFinalLOC()) / 2.0);
        pspLog.setEstimatedLOC(newEstimate);
        if (newEstimate != oldEstimate) {
            listener.onBattleLog("📏 Re-estimación de tamaño: de " + oldEstimate + " a " + newEstimate + " LOC.");
        }
        listener.onBattleLog("📐 Planeas y diseñas antes de programar (PSP 1 / 2.1): tus próximos " + PLANNED_ATTACKS
                + " ataques hacen +" + Math.round((PLANNED_DAMAGE_BONUS - 1) * 100) + "% de daño y tus próximas " + PLANNED_CODINGS + " codificaciones no inyectan defectos.");

        checkBattleStatus();
        if (!battleOver) enemyTurn();
    }

    /**
     * Acción 4: Sinergia de Equipo TSP
     */
    public void actionTeamSynergy(TSPRole role) {
        if (battleOver) return;

        if (!hero.consumeMp(COST_SYNERGY)) {
            listener.onBattleLog("❌ Requiere " + COST_SYNERGY + " MP de disciplina para coordinar con el equipo. Codifica para ganar MP.");
            return;
        }

        TeamMember member = team.stream().filter(m -> m.getRole() == role).findFirst().orElse(null);
        String memberName = member != null ? member.getName() : role.getTitle();
        double power = synergyPower(member);

        listener.onBattleLog("🤝 ¡Sinergia TSP activada con " + memberName + "!");
        logTime(synergyPhase(role), 10, "Sinergia TSP con " + memberName);

        switch (role) {
            case LIDER_EQUIPO: {
                int heal = (int) Math.round(30 * power);
                hero.heal(heal);
                listener.onBattleLog("📢 El Líder de Equipo motiva al grupo y revisa los compromisos: +" + heal + " HP.");
                break;
            }
            case GESTOR_DESARROLLO: {
                int devDmg = planned((int) Math.round(45 * power));
                enemy.takeDamage(devDmg);
                listener.onBattleLog("⚡ El Gestor de Desarrollo aplica patrones arquitectónicos sólidos e inflige " + devDmg + " de daño.");
                break;
            }
            case GESTOR_PLANIFICACION:
                halvedEnemyAttacks = SCHEDULE_BUFFER_TURNS;
                listener.onBattleLog("📅 El Gestor de Planificación ajusta el calendario: los próximos "
                        + SCHEDULE_BUFFER_TURNS + " contraataques hacen la mitad de daño.");
                break;
            case GESTOR_CALIDAD: {
                int heal = (int) Math.round(15 * power);
                hero.heal(heal);
                int found = pspLog.removePendingDefects(PEER_REVIEW_REMOVALS, "Inspección TSP", 5);
                listener.onBattleLog("🛡️ El Gestor de Calidad modera una inspección por pares: +" + heal + " HP"
                        + (found > 0 ? " y corrige " + found + " defecto(s) pendiente(s)." : "; no había defectos pendientes."));
                break;
            }
            case GESTOR_REQUERIMIENTOS:
                configBaselineTurns = CONFIG_BASELINE_TURNS;
                listener.onBattleLog("📦 El Administrador de Requerimientos fija la línea base de configuración: las próximas "
                        + CONFIG_BASELINE_TURNS + " codificaciones no inyectan defectos.");
                break;
        }

        checkBattleStatus();
        if (!battleOver) enemyTurn();
    }

    /** Aplica el bono de la planeación a los ataques planeados (cualquier acción que haga daño). */
    private int planned(int damage) {
        if (plannedAttacks <= 0) return damage;
        plannedAttacks--;
        return (int) Math.round(damage * PLANNED_DAMAGE_BONUS);
    }

    /** Potencia de la sinergia: +10% por cada nivel PSP en el que esté entrenado el miembro. */
    public static double synergyPower(TeamMember member) {
        return member == null ? 1.0 : 1.0 + 0.10 * member.getPspTraining().ordinal();
    }

    /** Efecto de la sinergia de un rol con la potencia indicada (texto para botones y menús). */
    public static String synergyEffect(TSPRole role, double power) {
        switch (role) {
            case LIDER_EQUIPO: return "Cura +" + Math.round(30 * power) + " HP";
            case GESTOR_DESARROLLO: return "Ataque fuerte de " + Math.round(45 * power) + " de daño";
            case GESTOR_PLANIFICACION: return "Los próximos " + SCHEDULE_BUFFER_TURNS + " contraataques hacen la mitad de daño";
            case GESTOR_CALIDAD: return "Corrige " + PEER_REVIEW_REMOVALS + " defectos pendientes y cura +" + Math.round(15 * power) + " HP";
            case GESTOR_REQUERIMIENTOS: return "Tus próximas " + CONFIG_BASELINE_TURNS + " codificaciones no inyectan defectos";
            default: return role.getSkillEffect();
        }
    }

    /** Probabilidad de inyectar un defecto al codificar (PSP y OPE la reducen; mínimo 5%). */
    double defectChance() {
        return Math.max(0.05, 0.40 - hero.getPspLevel().ordinal() * 0.06 - org.getDefectReduction());
    }

    /**
     * Acción 5: Control Estadístico CMMI-4
     */
    public void actionStatisticalControl() {
        if (battleOver) return;

        if (hero.getCmmiLevel().getLevelNumber() < 4) {
            listener.onBattleLog("🔒 Requiere Nivel CMMI 4 (Cuantitativamente Gestionado) para usar Control Estadístico.");
            return;
        }

        if (!hero.consumeMp(COST_STAT)) {
            listener.onBattleLog("❌ Requiere " + COST_STAT + " MP de disciplina.");
            return;
        }

        // Un proceso bajo control (yield alto: defectos removidos a tiempo) produce un golpe más certero
        double yield = pspLog.calculateYield();
        int statDamage = planned(STAT_BASE_DAMAGE + (int) Math.round(STAT_YIELD_BONUS * yield / 100.0));
        enemy.takeDamage(statDamage);
        logTime("Análisis cuantitativo", 10, "Control estadístico de subprocesos (CMMI 4)");
        listener.onBattleLog(String.format("📊 ¡Control Cuantitativo CMMI-4! Yield del proceso: %.0f%%.", yield));
        listener.onBattleLog("💥 Erradicas causas especiales de variación e infliges " + statDamage + " de daño certero.");

        checkBattleStatus();
        if (!battleOver) enemyTurn();
    }

    /**
     * Acción 6: Mejora Continua e Innovación CMMI-5
     */
    public void actionContinuousImprovement() {
        if (battleOver) return;

        if (hero.getCmmiLevel().getLevelNumber() < 5) {
            listener.onBattleLog("🔒 Requiere Nivel CMMI 5 (Optimización) para desplegar innovación y mejora continua.");
            return;
        }

        if (!hero.consumeMp(COST_CONTINUOUS)) {
            listener.onBattleLog("❌ Requiere " + COST_CONTINUOUS + " MP de disciplina.");
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

        int rawAtk = vary(enemy.getAttackPower());
        // Los defectos sin corregir agravan las fallas en producción
        int pending = pspLog.getPendingDefects();
        rawAtk += pending * PENDING_DEFECT_PENALTY;
        // Reducción por la Gerencia (GER) de MoProSoft: recursos e infraestructura
        int finalDamage = Math.max(3, (int) (rawAtk * (1.0 - org.getDamageReduction())));
        if (halvedEnemyAttacks > 0) {
            halvedEnemyAttacks--;
            finalDamage = Math.max(1, finalDamage / 2);
            listener.onBattleLog("📅 El calendario ajustado absorbe la mitad del ataque.");
        }

        hero.takeDamage(finalDamage);
        listener.onBattleLog("👾 " + enemy.getName() + " contraataca con [" + enemy.getSubtitle() + "] causando " + finalDamage + " de daño"
                + (pending > 0 ? " (agravado por " + pending + " defecto(s) pendiente(s))." : "."));

        checkBattleStatus();
    }

    /**
     * Registra en la bitácora de tiempos la actividad PSP del turno (interrupción de 0 a 3 min)
     * y cuenta el turno. Cada acción exitosa lo llama exactamente una vez.
     */
    private void logTime(String phase, int minutes, String comment) {
        turnCount++; // solo las acciones que sí se ejecutan cuentan como turno
        pspLog.logActivity(phase, minutes, random.nextInt(4), comment, false);
    }

    /** Variación aleatoria de ±15% para que ningún combate esté decidido de antemano. */
    private int vary(int value) {
        return (int) Math.round(value * (1.0 - DAMAGE_VARIANCE + random.nextDouble() * 2 * DAMAGE_VARIANCE));
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
        pspLog.updateProgress((double) (enemy.getMaxHp() - enemy.getHp()) / enemy.getMaxHp());
        if (enemy.isDefeated()) {
            battleOver = true;
            listener.onBattleLog("🎉 ¡VICTORIA! Has derrotado a " + enemy.getName() + ".");
            // Entregar sin defectos pendientes (yield 100%) paga un bono de calidad
            qualityBonus = pspLog.getPendingDefects() == 0;
            double goldFactor = rewardMultiplier * org.getGoldMultiplier() * (qualityBonus ? 1 + QUALITY_GOLD_BONUS : 1);
            xpEarned = (int) Math.round(enemy.getXpReward() * rewardMultiplier);
            goldEarned = (int) Math.round(enemy.getGoldReward() * goldFactor);
            listener.onBattleLog("⭐ Recompensas obtenidas: +" + xpEarned + " XP, +" + goldEarned + " Oro"
                    + (rewardMultiplier < 1 ? " (" + Math.round(rewardMultiplier * 100) + "% de la recompensa completa)." : "."));
            if (qualityBonus) {
                listener.onBattleLog("🏆 ¡Cero defectos pendientes (yield 100%)! Bono de calidad: +"
                        + Math.round(QUALITY_GOLD_BONUS * 100) + "% de oro.");
            }

            hero.addXp(xpEarned);
            hero.addGold(goldEarned);

            pspLog.logActivity("Post-Mortem", 10, 0, "Proyecto entregado: consolidación de métricas", true);
            pspLog.closeProject(true);
            listener.onBattleLog(String.format("📏 Post-Mortem: tamaño estimado %d LOC vs real %d LOC (error %+.0f%%); tiempo estimado %d min vs real %d min.",
                    pspLog.getEstimatedLOC(), pspLog.getActualLOC(), pspLog.getSizeEstimationErrorPercent(),
                    pspLog.getEstimatedMinutes(), pspLog.getTotalTimeMinutes()));

            listener.onBattleEnded(true);
        } else if (hero.getHp() <= 0) {
            battleOver = true;
            pspLog.logActivity("Post-Mortem", 10, 0, "Proyecto fallido: análisis de causas", true);
            pspLog.closeProject(false);
            listener.onBattleLog("💀 DERROTA: El proyecto ha colapsado ante la acumulación de defectos.");
            listener.onBattleLog("⚠️ Revisa tus bitácoras de tiempos y defectos en la pestaña PSP para identificar las fallas.");
            listener.onBattleEnded(false);
        }
    }
}

