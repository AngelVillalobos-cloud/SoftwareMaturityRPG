package com.softwarerpg.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Paleta de colores, fuentes y utilidades visuales con estética RPG Cyber/Dev.
 */
public class Theme {
    public static final Color BG_DARK = new Color(22, 26, 34);
    public static final Color PANEL_BG = new Color(30, 36, 48);
    public static final Color PANEL_SECONDARY = new Color(38, 46, 62);
    public static final Color ACCENT_CYAN = new Color(56, 189, 248);
    public static final Color ACCENT_GREEN = new Color(74, 222, 128);
    public static final Color ACCENT_PURPLE = new Color(192, 132, 252);
    public static final Color ACCENT_AMBER = new Color(251, 191, 36);
    public static final Color ACCENT_RED = new Color(248, 113, 113);
    public static final Color TEXT_PRIMARY = new Color(243, 244, 246);
    public static final Color TEXT_MUTED = new Color(156, 163, 175);
    public static final Color BORDER_COLOR = new Color(55, 65, 81);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);
    public static final Font FONT_MONO_BOLD = new Font("Consolas", Font.BOLD, 12);

    public static void applyDarkTabbedPane(JTabbedPane tabbedPane) {
        tabbedPane.setBackground(BG_DARK);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setFont(FONT_HEADER);
    }

    public static JButton createStyledButton(String text, Color accent) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(accent.darker());
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(accent, 1, true),
            new EmptyBorder(8, 14, 8, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(accent);
                btn.setForeground(Color.BLACK);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(accent.darker());
                btn.setForeground(Color.WHITE);
            }
        });
        return btn;
    }

    public static JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(PANEL_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        if (title != null && !title.isEmpty()) {
            JLabel lbl = new JLabel(title);
            lbl.setFont(FONT_HEADER);
            lbl.setForeground(ACCENT_CYAN);
            lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
            card.add(lbl, BorderLayout.NORTH);
        }
        return card;
    }
}

