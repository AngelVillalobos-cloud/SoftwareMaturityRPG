package com.softwarerpg.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Personaje dibujado con Java2D (sin imágenes externas) para la arena de combate:
 * el Ingeniero de Software (héroe) o el Cliente molesto (señor o señora).
 *
 * El "ánimo" va de 0.0 a 1.0:
 *  - Ingeniero: proporción de salud del proyecto (1 = fresco, 0 = derrotado).
 *  - Cliente: nivel de enojo según la vida restante del defecto (1 = furioso, 0 = satisfecho).
 */
public class CharacterSprite extends JComponent {
    private static final long serialVersionUID = 1L;

    public enum Kind { ENGINEER, CLIENT_MAN, CLIENT_WOMAN }

    private static final Color SKIN = new Color(241, 194, 150);
    private static final Color SKIN_ANGRY = new Color(232, 96, 80);
    private static final Color OUTLINE = new Color(30, 30, 36);
    private static final Color BUBBLE_BG = new Color(243, 244, 246);

    private Kind kind;
    private double mood = 1.0;
    private boolean finished = false; // combate terminado para este personaje
    private String speech = "";

    public CharacterSprite(Kind kind) {
        this.kind = kind;
        setPreferredSize(new Dimension(150, 175));
        setMinimumSize(new Dimension(150, 175));
        setOpaque(false);
    }

    public void setKind(Kind kind) { this.kind = kind; repaint(); }

    /** @param mood  0.0 a 1.0 (ver documentación de la clase)
     *  @param finished true si el personaje ya terminó (héroe sin HP / cliente con defecto vencido) */
    public void setState(double mood, boolean finished, String speech) {
        this.mood = Math.max(0, Math.min(1, mood));
        this.finished = finished;
        this.speech = speech == null ? "" : speech;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        // Centrar un lienzo lógico de 150x175
        g.translate(Math.max(0, (w - 150) / 2), Math.max(0, (getHeight() - 175) / 2));

        drawSpeechBubble(g);
        if (kind == Kind.ENGINEER) drawEngineer(g); else drawClient(g);
        g.dispose();
    }

    // ------------------------------------------------------------------ Globo de diálogo

    private void drawSpeechBubble(Graphics2D g) {
        if (speech.isEmpty()) return;
        g.setFont(Theme.FONT_BOLD.deriveFont(11f));
        FontMetrics fm = g.getFontMetrics();
        int tw = Math.min(146, fm.stringWidth(speech) + 14);
        int x = (150 - tw) / 2, y = 2, h = 22;
        g.setColor(BUBBLE_BG);
        g.fill(new RoundRectangle2D.Double(x, y, tw, h, 12, 12));
        g.fillPolygon(new int[]{70, 80, 75}, new int[]{y + h - 1, y + h - 1, y + h + 7}, 3);
        g.setColor(OUTLINE);
        g.draw(new RoundRectangle2D.Double(x, y, tw, h, 12, 12));
        String text = speech;
        while (fm.stringWidth(text) > tw - 10 && text.length() > 3) text = text.substring(0, text.length() - 2) + "…";
        g.drawString(text, x + (tw - fm.stringWidth(text)) / 2, y + 15);
    }

    // ------------------------------------------------------------------ Ingeniero

    private void drawEngineer(Graphics2D g) {
        int cx = 75, cy = 78; // centro de la cabeza

        // Sudadera
        g.setColor(new Color(37, 99, 235));
        g.fill(new RoundRectangle2D.Double(35, 108, 80, 64, 30, 30));
        g.setColor(new Color(29, 78, 216));
        g.fill(new Arc2D.Double(55, 100, 40, 24, 180, 180, Arc2D.CHORD)); // cuello/capucha

        drawHead(g, cx, cy, SKIN);

        // Cabello
        g.setColor(new Color(60, 40, 30));
        g.fill(new Arc2D.Double(cx - 27, cy - 30, 54, 40, 0, 180, Arc2D.CHORD));

        // Lentes
        g.setStroke(new BasicStroke(2f));
        g.setColor(OUTLINE);
        g.draw(new RoundRectangle2D.Double(cx - 21, cy - 6, 16, 12, 5, 5));
        g.draw(new RoundRectangle2D.Double(cx + 5, cy - 6, 16, 12, 5, 5));
        g.drawLine(cx - 5, cy - 1, cx + 5, cy - 1);

        // Ojos
        if (finished) {
            drawX(g, cx - 13, cy, 4);
            drawX(g, cx + 13, cy, 4);
        } else {
            g.fillOval(cx - 15, cy - 2, 4, 4);
            g.fillOval(cx + 11, cy - 2, 4, 4);
        }

        // Boca según ánimo
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (finished || mood < 0.3) drawFrown(g, cx, cy + 15, 14);
        else if (mood < 0.6) g.drawLine(cx - 6, cy + 14, cx + 6, cy + 14);
        else drawSmile(g, cx, cy + 9, 14);

        // Gota de sudor cuando la salud está baja
        if (!finished && mood < 0.45) {
            g.setColor(new Color(96, 165, 250));
            Path2D drop = new Path2D.Double();
            drop.moveTo(cx + 27, cy - 14);
            drop.curveTo(cx + 21, cy - 4, cx + 23, cy + 2, cx + 27, cy + 2);
            drop.curveTo(cx + 31, cy + 2, cx + 33, cy - 4, cx + 27, cy - 14);
            g.fill(drop);
        }

        // Laptop
        g.setColor(new Color(156, 163, 175));
        g.fill(new RoundRectangle2D.Double(42, 136, 66, 36, 6, 6));
        g.setColor(new Color(209, 213, 219));
        g.fillRect(36, 166, 78, 6);
        g.setColor(mood < 0.3 ? Theme.ACCENT_RED : Theme.ACCENT_GREEN);
        g.setFont(Theme.FONT_MONO_BOLD.deriveFont(11f));
        g.drawString(mood < 0.3 ? "ERR!" : "</>", 63, 158);
    }

    // ------------------------------------------------------------------ Cliente

    private void drawClient(Graphics2D g) {
        int cx = 75, cy = 78;
        double anger = finished ? 0 : mood;
        boolean woman = kind == Kind.CLIENT_WOMAN;

        // Cabello largo de la señora (detrás de la cabeza)
        if (woman) {
            g.setColor(new Color(92, 51, 23));
            g.fill(new RoundRectangle2D.Double(cx - 31, cy - 26, 62, 66, 30, 30));
        }

        // Traje
        g.setColor(woman ? new Color(126, 34, 206) : new Color(55, 65, 81));
        g.fill(new RoundRectangle2D.Double(35, 108, 80, 64, 30, 30));
        g.setColor(Color.WHITE);
        g.fillPolygon(new int[]{cx - 12, cx + 12, cx}, new int[]{108, 108, 132}, 3);
        if (woman) {
            g.setColor(new Color(250, 204, 21)); // collar
            g.fillOval(cx - 3, 116, 6, 6);
        } else {
            g.setColor(new Color(220, 38, 38)); // corbata
            g.fillPolygon(new int[]{cx - 4, cx + 4, cx + 6, cx, cx - 6}, new int[]{110, 110, 132, 140, 132}, 5);
        }

        // Cara: se enrojece con el enojo
        drawHead(g, cx, cy, blend(SKIN, SKIN_ANGRY, anger));

        if (woman) {
            g.setColor(new Color(92, 51, 23));
            g.fill(new Arc2D.Double(cx - 28, cy - 30, 56, 36, 0, 180, Arc2D.CHORD)); // flequillo
            g.fillOval(cx - 12, cy - 46, 24, 20); // chongo
            g.setColor(new Color(250, 204, 21));
            g.fillOval(cx - 30, cy + 6, 5, 5); // aretes
            g.fillOval(cx + 25, cy + 6, 5, 5);
        } else {
            g.setColor(new Color(156, 163, 175)); // canas a los lados (calvo arriba)
            g.fill(new Arc2D.Double(cx - 29, cy - 18, 14, 26, 90, 180, Arc2D.CHORD));
            g.fill(new Arc2D.Double(cx + 15, cy - 18, 14, 26, 270, 180, Arc2D.CHORD));
        }

        // Cejas: más inclinadas cuanto más enojo
        g.setColor(OUTLINE);
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int tilt = (int) Math.round((anger - 0.3) * 9); // negativo = cejas relajadas
        g.drawLine(cx - 18, cy - 10 - tilt, cx - 6, cy - 8 + tilt);
        g.drawLine(cx + 18, cy - 10 - tilt, cx + 6, cy - 8 + tilt);

        // Ojos
        g.fillOval(cx - 14, cy - 2, 5, 5);
        g.fillOval(cx + 9, cy - 2, 5, 5);

        // Bigote del señor
        if (!woman) {
            g.setColor(new Color(107, 114, 128));
            g.fill(new RoundRectangle2D.Double(cx - 11, cy + 8, 22, 6, 6, 6));
        }

        // Boca
        g.setColor(OUTLINE);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (anger > 0.66) {
            g.setColor(new Color(127, 29, 29));
            g.fillOval(cx - 7, cy + 15, 14, 9); // gritando
        } else if (anger > 0.33) {
            drawFrown(g, cx, cy + 20, 14);
        } else {
            drawSmile(g, cx, cy + 11, 20);
        }

        // Símbolo de enojo y vapor
        if (anger > 0.5) {
            g.setColor(new Color(239, 68, 68));
            g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int ax = cx + 26, ay = cy - 30;
            g.drawArc(ax - 8, ay - 8, 8, 8, 270, 90);
            g.drawArc(ax, ay - 8, 8, 8, 180, 90);
            g.drawArc(ax - 8, ay, 8, 8, 0, 90);
            g.drawArc(ax, ay, 8, 8, 90, 90);
        }
        if (anger > 0.8) {
            g.setColor(new Color(229, 231, 235, 200));
            g.fillOval(cx - 44, cy - 34, 12, 10);
            g.fillOval(cx - 50, cy - 46, 9, 8);
        }
    }

    // ------------------------------------------------------------------ Utilidades

    private void drawHead(Graphics2D g, int cx, int cy, Color skin) {
        g.setColor(skin);
        g.fillOval(cx - 26, cy - 28, 52, 56);
        g.setColor(OUTLINE);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(cx - 26, cy - 28, 52, 56);
    }

    private void drawSmile(Graphics2D g, int cx, int y, int width) {
        g.draw(new Arc2D.Double(cx - width / 2.0, y, width, 10, 180, 180, Arc2D.OPEN));
    }

    private void drawFrown(Graphics2D g, int cx, int y, int width) {
        g.draw(new Arc2D.Double(cx - width / 2.0, y - 4, width, 8, 0, 180, Arc2D.OPEN));
    }

    private void drawX(Graphics2D g, int x, int y, int r) {
        g.drawLine(x - r, y - r, x + r, y + r);
        g.drawLine(x - r, y + r, x + r, y - r);
    }

    private static Color blend(Color a, Color b, double t) {
        return new Color(
            (int) (a.getRed() + (b.getRed() - a.getRed()) * t),
            (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
            (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }
}
