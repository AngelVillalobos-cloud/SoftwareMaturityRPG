package com.softwarerpg;

import com.softwarerpg.engine.GameEngine;
import com.softwarerpg.engine.SaveManager;
import com.softwarerpg.ui.GameMainWindow;

import javax.swing.*;
import java.nio.file.Path;

/**
 * Punto de entrada del juego Software Maturity RPG.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            installLookAndFeel();
            GameEngine engine = askContinueSavedGame();
            if (engine == null) engine = askNewGame();
            GameMainWindow window = new GameMainWindow(engine);
            window.setVisible(true);
        });
    }

    /** Look & Feel Nimbus si está disponible (se configura en el hilo de eventos de Swing). */
    private static void installLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored2) {}
        }
    }

    /** Si hay una partida guardada, ofrece continuarla. Devuelve null para empezar una nueva. */
    private static GameEngine askContinueSavedGame() {
        Path save = SaveManager.defaultSavePath();
        if (!SaveManager.hasSave(save)) return null;
        Object[] options = {"▶️ Continuar partida guardada", "🆕 Nueva partida"};
        int choice = JOptionPane.showOptionDialog(null,
                "Se encontró una partida guardada.\n¿Quieres continuarla?",
                "Software Maturity RPG", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (choice != 0) return null;
        try {
            return SaveManager.load(save);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo cargar la partida guardada:\n" + e.getMessage() + "\n\nSe iniciará una partida nueva.",
                    "Error al cargar", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /** Pide el nombre del ingeniero y de la organización para iniciar una partida nueva. */
    public static GameEngine askNewGame() {
        String heroName = JOptionPane.showInputDialog(
            null,
            "¡Bienvenido a Software Maturity RPG!\n¿Cuál es tu nombre de Ingeniero de Software?",
            "Inicio de la Partida",
            JOptionPane.QUESTION_MESSAGE
        );

        if (heroName == null || heroName.trim().isEmpty()) {
            heroName = "Ing. Alan Turing";
        }

        String companyName = JOptionPane.showInputDialog(
            null,
            "¿Cuál es el nombre de tu organización / software house?",
            "Fundación de Empresa",
            JOptionPane.QUESTION_MESSAGE
        );

        if (companyName == null || companyName.trim().isEmpty()) {
            companyName = "Softwaria Solutions";
        }

        return new GameEngine(heroName.trim(), companyName.trim());
    }
}

