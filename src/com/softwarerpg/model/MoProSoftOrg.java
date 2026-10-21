package com.softwarerpg.model;

/**
 * Modelo de la Organización según la estructura MoProSoft (NMX-I-059-NYCE).
 *
 * Se organiza en 3 Categorías, cada una con una sola mejora y un efecto claro en el juego:
 * 1) Alta Dirección (DIR) -> Gestión de Negocios: más oro por proyecto.
 * 2) Gerencia (GER) -> Gestión de Recursos (RH, BSI, CO): los bugs hacen menos daño.
 * 3) Operación (OPE) -> Desarrollo y Mantenimiento: más daño y menos defectos al codificar.
 *
 * El nivel de capacidad de la organización (1 Realizado ... 5 Optimizado) se calcula
 * con las mejoras compradas: no hay que "certificar" aparte.
 */
public class MoProSoftOrg {
    public enum Area { DIR, GER, OPE }

    /** Cada categoría va del nivel 1 al 3. */
    public static final int MAX_AREA_LEVEL = 3;

    private String companyName;
    private int dirLevel = 1;
    private int gerLevel = 1;
    private int opeLevel = 1;

    public MoProSoftOrg(String companyName) {
        this.companyName = companyName;
    }

    // ------------------------------------------------------------------ Mejoras

    public int getLevel(Area area) {
        switch (area) {
            case DIR: return dirLevel;
            case GER: return gerLevel;
            default:  return opeLevel;
        }
    }

    public boolean isMaxed(Area area) {
        return getLevel(area) >= MAX_AREA_LEVEL;
    }

    /** Costo en oro de subir la categoría al siguiente nivel (150 y luego 300). */
    public int upgradeCost(Area area) {
        return getLevel(area) * 150;
    }

    /** Sube un nivel la categoría (el pago lo hace el motor del juego). */
    public boolean upgrade(Area area) {
        if (isMaxed(area)) return false;
        switch (area) {
            case DIR: dirLevel++; break;
            case GER: gerLevel++; break;
            default:  opeLevel++; break;
        }
        return true;
    }

    // ------------------------------------------------------------------ Efectos

    /** DIR: +15% de oro por nivel extra. */
    public double getGoldMultiplier() {
        return 1.0 + (dirLevel - 1) * 0.15;
    }

    /** GER: los contraataques hacen 8% menos de daño por nivel extra. */
    public double getDamageReduction() {
        return (gerLevel - 1) * 0.08;
    }

    /** OPE: daño extra al codificar. */
    public int getCodingDamageBonus() {
        return (opeLevel - 1) * 5;
    }

    /** OPE: menor probabilidad de inyectar un defecto al codificar. */
    public double getDefectReduction() {
        return (opeLevel - 1) * 0.06;
    }

    // ------------------------------------------------------------------ Capacidad

    /** Nivel de capacidad MoProSoft (1 a 5) según las mejoras compradas (0 a 6). */
    public int getCapabilityLevel() {
        int upgrades = (dirLevel - 1) + (gerLevel - 1) + (opeLevel - 1);
        return 1 + upgrades * 4 / 6;
    }

    public String getCapabilityLevelName() {
        switch (getCapabilityLevel()) {
            case 1: return "Nivel 1 - Realizado";
            case 2: return "Nivel 2 - Administrado";
            case 3: return "Nivel 3 - Establecido";
            case 4: return "Nivel 4 - Predecible";
            default: return "Nivel 5 - Optimizado";
        }
    }

    /** Restaura el estado de la organización al cargar una partida guardada. */
    public void restoreState(int dirLevel, int gerLevel, int opeLevel) {
        this.dirLevel = clamp(dirLevel);
        this.gerLevel = clamp(gerLevel);
        this.opeLevel = clamp(opeLevel);
    }

    private static int clamp(int level) {
        return Math.max(1, Math.min(MAX_AREA_LEVEL, level));
    }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}
