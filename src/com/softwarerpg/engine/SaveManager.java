package com.softwarerpg.engine;

import com.softwarerpg.model.*;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * Guardado y carga de partidas en un archivo .properties de texto (sin dependencias externas).
 *
 * Se guarda el progreso entre proyectos: héroe, organización MoProSoft, equipo TSP,
 * misiones completadas, ideas del PIP e historial PSP de proyectos. No se guarda un
 * combate en curso: solo se puede guardar entre proyectos.
 */
public final class SaveManager {
    /** Versión del formato: si cambia la estructura, se incrementa para detectar archivos viejos. */
    public static final int FORMAT_VERSION = 1;

    private SaveManager() { }

    /** Ubicación por defecto: carpeta del usuario, para que funcione igual en Windows y Linux. */
    public static Path defaultSavePath() {
        return Paths.get(System.getProperty("user.home"), ".software-maturity-rpg", "partida.properties");
    }

    public static boolean hasSave(Path file) {
        return Files.isRegularFile(file);
    }

    public static void save(GameEngine engine, Path file) throws IOException {
        if (engine.isBattleInProgress()) {
            throw new IllegalStateException("No se puede guardar a mitad de un combate.");
        }
        Properties p = new Properties();
        p.setProperty("version", String.valueOf(FORMAT_VERSION));

        Hero h = engine.getHero();
        p.setProperty("hero.name", h.getName());
        p.setProperty("hero.hp", String.valueOf(h.getHp()));
        p.setProperty("hero.maxHp", String.valueOf(h.getMaxHp()));
        p.setProperty("hero.mp", String.valueOf(h.getMp()));
        p.setProperty("hero.maxMp", String.valueOf(h.getMaxMp()));
        p.setProperty("hero.level", String.valueOf(h.getLevel()));
        p.setProperty("hero.xp", String.valueOf(h.getXp()));
        p.setProperty("hero.gold", String.valueOf(h.getGold()));
        p.setProperty("hero.psp", h.getPspLevel().name());
        p.setProperty("hero.cmmi", h.getCmmiLevel().name());
        p.setProperty("hero.checklists", String.valueOf(h.getChecklists()));
        p.setProperty("hero.templates", String.valueOf(h.getDesignTemplates()));
        List<String> ideas = h.getPipIdeas();
        p.setProperty("pip.count", String.valueOf(ideas.size()));
        for (int i = 0; i < ideas.size(); i++) p.setProperty("pip." + i, ideas.get(i));

        MoProSoftOrg o = engine.getOrg();
        p.setProperty("org.name", o.getCompanyName());
        p.setProperty("org.capability", String.valueOf(o.getCapabilityLevel()));
        p.setProperty("org.budget", String.valueOf(o.getBudget()));
        p.setProperty("org.rh", String.valueOf(o.getHumanResourcesLevel()));
        p.setProperty("org.bsi", String.valueOf(o.getInfrastructureLevel()));
        p.setProperty("org.co", String.valueOf(o.getKnowledgeBaseLevel()));
        p.setProperty("org.points", String.valueOf(o.getProcessPoints()));

        List<TeamMember> team = engine.getTeam();
        for (int i = 0; i < team.size(); i++) {
            TeamMember m = team.get(i);
            p.setProperty("team." + i + ".psp", m.getPspTraining().name());
            p.setProperty("team." + i + ".energy", String.valueOf(m.getEnergy()));
            p.setProperty("team." + i + ".maxEnergy", String.valueOf(m.getMaxEnergy()));
        }

        List<Mission> missions = engine.getMissions();
        for (int i = 0; i < missions.size(); i++) {
            p.setProperty("mission." + missions.get(i).getId() + ".completed", String.valueOf(missions.get(i).isCompleted()));
        }

        List<ProjectSummary> history = engine.getPspLog().getHistory();
        p.setProperty("history.count", String.valueOf(history.size()));
        for (int i = 0; i < history.size(); i++) {
            ProjectSummary s = history.get(i);
            String k = "history." + i + ".";
            p.setProperty(k + "name", s.getProjectName());
            p.setProperty(k + "delivered", String.valueOf(s.isDelivered()));
            p.setProperty(k + "estLoc", String.valueOf(s.getEstimatedLOC()));
            p.setProperty(k + "actualLoc", String.valueOf(s.getActualLOC()));
            p.setProperty(k + "estMin", String.valueOf(s.getEstimatedMinutes()));
            p.setProperty(k + "actualMin", String.valueOf(s.getActualMinutes()));
            p.setProperty(k + "defects", String.valueOf(s.getTotalDefects()));
            p.setProperty(k + "pending", String.valueOf(s.getPendingDefects()));
            p.setProperty(k + "dd", String.valueOf(s.getDefectDensity()));
            p.setProperty(k + "yield", String.valueOf(s.getYield()));
        }

        Files.createDirectories(file.toAbsolutePath().getParent());
        Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
        try (Writer w = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            p.store(w, "Software Maturity RPG - partida guardada");
        }
        // Reemplazo al final: si algo falla a mitad de la escritura, la partida anterior sigue intacta
        Files.move(tmp, file, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    public static GameEngine load(Path file) throws IOException {
        Properties p = new Properties();
        try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            p.load(r);
        }
        int version = integer(p, "version");
        if (version != FORMAT_VERSION) {
            throw new IOException("Formato de partida no compatible (versión " + version + ").");
        }

        GameEngine engine = new GameEngine(text(p, "hero.name"), text(p, "org.name"));

        Hero h = engine.getHero();
        h.restoreState(integer(p, "hero.hp"), integer(p, "hero.maxHp"), integer(p, "hero.mp"), integer(p, "hero.maxMp"),
                integer(p, "hero.level"), integer(p, "hero.xp"), integer(p, "hero.gold"),
                integer(p, "hero.checklists"), integer(p, "hero.templates"));
        h.setPspLevel(PSPLevel.valueOf(text(p, "hero.psp")));
        h.setCmmiLevel(CMMILevel.valueOf(text(p, "hero.cmmi")));
        for (int i = 0; i < integer(p, "pip.count"); i++) h.addPipIdea(text(p, "pip." + i));

        engine.getOrg().restoreState(integer(p, "org.capability"), integer(p, "org.budget"), integer(p, "org.rh"),
                integer(p, "org.bsi"), integer(p, "org.co"), integer(p, "org.points"));

        List<TeamMember> team = engine.getTeam();
        for (int i = 0; i < team.size(); i++) {
            if (p.getProperty("team." + i + ".psp") == null) continue;
            team.get(i).restoreState(PSPLevel.valueOf(text(p, "team." + i + ".psp")),
                    integer(p, "team." + i + ".energy"), integer(p, "team." + i + ".maxEnergy"));
        }

        for (Mission m : engine.getMissions()) {
            m.setCompleted(Boolean.parseBoolean(p.getProperty("mission." + m.getId() + ".completed", "false")));
        }

        for (int i = 0; i < integer(p, "history.count"); i++) {
            String k = "history." + i + ".";
            engine.getPspLog().addToHistory(new ProjectSummary(text(p, k + "name"),
                    Boolean.parseBoolean(text(p, k + "delivered")),
                    integer(p, k + "estLoc"), integer(p, k + "actualLoc"),
                    integer(p, k + "estMin"), integer(p, k + "actualMin"),
                    integer(p, k + "defects"), integer(p, k + "pending"),
                    Double.parseDouble(text(p, k + "dd")), Double.parseDouble(text(p, k + "yield"))));
        }
        return engine;
    }

    private static String text(Properties p, String key) throws IOException {
        String v = p.getProperty(key);
        if (v == null) throw new IOException("Partida dañada: falta el dato '" + key + "'.");
        return v;
    }

    private static int integer(Properties p, String key) throws IOException {
        try {
            return Integer.parseInt(text(p, key).trim());
        } catch (NumberFormatException e) {
            throw new IOException("Partida dañada: '" + key + "' no es un número.", e);
        }
    }
}
