package com.softwarerpg;

import com.softwarerpg.engine.GameEngine;
import com.softwarerpg.ui.GameMainWindow;

import javax.swing.*;

/**
 * Punto de entrada del juego Software Maturity RPG.
 */
public class Main {
    public static void main(String[] args) {
        // Establecer Look & Feel
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

        SwingUtilities.invokeLater(() -> {
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

            GameEngine engine = new GameEngine(heroName.trim(), companyName.trim());
            GameMainWindow window = new GameMainWindow(engine);
            window.setVisible(true);
        });
    }
}

