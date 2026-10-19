package com.softwarerpg.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicProgressBarUI;

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

    /** Fondo y borde de los botones deshabilitados: oscuros para que el texto se lea. */
    public static final Color DISABLED_BG = new Color(45, 52, 66);

    /** Marca para botones que reproducen su propio sonido en vez del clic genérico. */
    public static final String CUSTOM_SOUND = "softwarerpg.customSound";

    public static JButton createStyledButton(String text, Color accent) {
        JButton btn = new JButton(text);
        // UI propio: respeta los colores del tema aunque el Look&Feel sea Nimbus
        // (Nimbus pinta los botones deshabilitados gris claro con texto blanco, ilegibles).
        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                g.setColor(c.isEnabled() ? c.getBackground() : DISABLED_BG);
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
                super.paint(g, c);
            }

            @Override
            protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String txt) {
                FontMetrics fm = g.getFontMetrics();
                g.setColor(b.isEnabled() ? b.getForeground() : TEXT_MUTED);
                BasicGraphicsUtils.drawStringUnderlineCharAt(b, (Graphics2D) g, txt, -1,
                        textRect.x, textRect.y + fm.getAscent());
            }
        });
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(accent.darker());
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(accent, 1, true),
            new EmptyBorder(8, 14, 8, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.isEnabled()) return;
                btn.setBackground(accent);
                btn.setForeground(Color.BLACK);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(accent.darker());
                btn.setForeground(Color.WHITE);
            }
        });
        btn.addActionListener(e -> {
            if (btn.getClientProperty(CUSTOM_SOUND) == null) Sound.play(Sound.Sfx.CLICK);
        });
        return btn;
    }

    /**
     * Barra de progreso que respeta los colores del tema. El Look&Feel Nimbus ignora
     * setForeground en las barras y las pinta naranjas con texto poco legible.
     */
    public static JProgressBar createProgressBar(int min, int max, Color fill) {
        JProgressBar bar = new JProgressBar(min, max);
        bar.setUI(new BasicProgressBarUI() {
            @Override protected Color getSelectionBackground() { return TEXT_PRIMARY; } // texto sobre la parte vacía
            @Override protected Color getSelectionForeground() { return BG_DARK; }      // texto sobre la parte llena
        });
        bar.setForeground(fill);
        bar.setBackground(PANEL_SECONDARY);
        bar.setOpaque(true);
        bar.setBorder(new LineBorder(BORDER_COLOR, 1));
        bar.setFont(FONT_BOLD);
        bar.setStringPainted(true);
        return bar;
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

