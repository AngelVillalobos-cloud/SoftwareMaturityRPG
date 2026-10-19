package com.softwarerpg.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Iconos vectoriales dibujados con Java2D.
 *
 * Java no puede dibujar emojis a color con las fuentes de Windows (salen como "▯"),
 * así que los emojis que encabezan textos de botones, pestañas y títulos se sustituyen
 * por estos iconos, que se ven igual en cualquier sistema operativo.
 * El icono toma el color del texto del componente y se atenúa si está deshabilitado.
 */
public final class Icons {
    private Icons() { }

    public enum Shape {
        CODE, CLIPBOARD, RULER, PEOPLE, PERSON, CHART, ROCKET, LOCK, MAP, SWORDS, BUILDING, BOOK,
        CUP, CART, BUG, REFRESH, STAR, CHECK, CROSS, PENCIL, CLOCK, CALENDAR, COIN, PLAY, DISK,
        FOLDER, DOC, BOLT, BULB, TROPHY, SPEAKER
    }

    private static final Map<Integer, Shape> BY_EMOJI = new HashMap<>();

    static {
        map(Shape.CODE, 0x1F4BB, 0x1F468);                       // 💻 👨‍💻
        map(Shape.CLIPBOARD, 0x1F4CB);                           // 📋
        map(Shape.RULER, 0x1F4D0, 0x1F4CF);                      // 📐 📏
        map(Shape.PEOPLE, 0x1F91D, 0x1F465);                     // 🤝 👥
        map(Shape.PERSON, 0x1F464);                              // 👤
        map(Shape.CHART, 0x1F4CA, 0x1F4C8);                      // 📊 📈
        map(Shape.ROCKET, 0x1F680);                              // 🚀
        map(Shape.LOCK, 0x1F512);                                // 🔒
        map(Shape.MAP, 0x1F5FA);                                 // 🗺️
        map(Shape.SWORDS, 0x2694);                               // ⚔️
        map(Shape.BUILDING, 0x1F3DB, 0x1F1F2);                   // 🏛️ 🇲🇽
        map(Shape.BOOK, 0x1F4D6, 0x1F4DA);                       // 📖 📚
        map(Shape.CUP, 0x2615);                                  // ☕
        map(Shape.CART, 0x1F6D2);                                // 🛒
        map(Shape.BUG, 0x1F47E, 0x1F41E, 0x1F41B);               // 👾 🐞 🐛
        map(Shape.REFRESH, 0x1F504, 0x1F501);                    // 🔄 🔁
        map(Shape.STAR, 0x2B50, 0x1F3C5, 0x1F393, 0x1F195, 0x1F31F, 0x1F389, 0x2728); // ⭐ 🏅 🎓 🆕 🌟 🎉 ✨
        map(Shape.CHECK, 0x2705, 0x2714);                        // ✅ ✔
        map(Shape.CROSS, 0x274C, 0x1F480);                       // ❌ 💀
        map(Shape.PENCIL, 0x270D, 0x1F4DD);                      // ✍️ 📝
        map(Shape.CLOCK, 0x23F1, 0x23F3);                        // ⏱️ ⏳
        map(Shape.CALENDAR, 0x1F5D3, 0x1F4C5);                   // 🗓️ 📅
        map(Shape.COIN, 0x1F4B0);                                // 💰
        map(Shape.PLAY, 0x25B6, 0x1F3AE);                        // ▶️ 🎮
        map(Shape.DISK, 0x1F4BE);                                // 💾
        map(Shape.FOLDER, 0x1F4C2);                              // 📂
        map(Shape.DOC, 0x1F4DC, 0x1F4D1);                        // 📜 📑
        map(Shape.BOLT, 0x26A1);                                 // ⚡
        map(Shape.BULB, 0x1F4A1);                                // 💡
        map(Shape.TROPHY, 0x1F3C6);                              // 🏆
        map(Shape.SPEAKER, 0x1F50A);                             // 🔊
    }

    private static void map(Shape shape, int... codePoints) {
        for (int cp : codePoints) BY_EMOJI.put(cp, shape);
    }

    /** Icono correspondiente a un emoji (por su primer code point), o null si no hay. */
    public static Icon forEmoji(int codePoint) {
        Shape s = BY_EMOJI.get(codePoint);
        return s == null ? null : of(s);
    }

    public static Icon of(Shape shape) {
        return new VectorIcon(shape, 16);
    }

    /** Icono de 16 px que se pinta con el color del texto del componente. */
    private static final class VectorIcon implements Icon {
        private final Shape shape;
        private final int size;

        VectorIcon(Shape shape, int size) {
            this.shape = shape;
            this.size = size;
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.translate(x, y);
            g.scale(size / 16.0, size / 16.0);
            Color color = c != null ? c.getForeground() : Color.WHITE;
            if (c != null && !c.isEnabled()) color = Theme.TEXT_MUTED;
            g.setColor(color);
            g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            draw(g, shape);
            g.dispose();
        }
    }

    // ------------------------------------------------------------------ Dibujos (lienzo de 16×16)

    private static void draw(Graphics2D g, Shape s) {
        switch (s) {
            case CODE:
                g.draw(line(5, 4, 1.5, 8)); g.draw(line(1.5, 8, 5, 12));
                g.draw(line(11, 4, 14.5, 8)); g.draw(line(14.5, 8, 11, 12));
                g.draw(line(9.5, 3, 6.5, 13));
                break;
            case CLIPBOARD:
                g.draw(new RoundRectangle2D.Double(3, 2.5, 10, 12, 2, 2));
                g.fill(new RoundRectangle2D.Double(5.5, 1, 5, 3, 1.5, 1.5));
                g.draw(line(5.5, 9, 7.5, 11)); g.draw(line(7.5, 11, 11, 6.5));
                break;
            case RULER: {
                Path2D p = new Path2D.Double();
                p.moveTo(2, 14); p.lineTo(2, 2); p.lineTo(14, 14); p.closePath();
                g.draw(p);
                g.draw(line(2, 6, 4, 6)); g.draw(line(2, 10, 4, 10)); g.draw(line(6, 14, 6, 12)); g.draw(line(10, 14, 10, 12));
                break;
            }
            case PEOPLE:
                g.fill(new Ellipse2D.Double(3, 2.5, 4.5, 4.5));
                g.fill(new Ellipse2D.Double(9, 3.5, 4, 4));
                g.fill(new Arc2D.Double(0.5, 8, 9.5, 9, 0, 180, Arc2D.CHORD));
                g.fill(new Arc2D.Double(8, 9, 7.5, 7.5, 0, 180, Arc2D.CHORD));
                break;
            case PERSON:
                g.fill(new Ellipse2D.Double(5, 1.5, 6, 6));
                g.fill(new Arc2D.Double(2, 8.5, 12, 11, 0, 180, Arc2D.CHORD));
                break;
            case CHART:
                g.fill(new Rectangle2D.Double(2, 9, 3, 5));
                g.fill(new Rectangle2D.Double(6.5, 5, 3, 9));
                g.fill(new Rectangle2D.Double(11, 2, 3, 12));
                break;
            case ROCKET: {
                Path2D p = new Path2D.Double();
                p.moveTo(8, 1); p.curveTo(12, 4, 12, 9, 10.5, 11.5); p.lineTo(5.5, 11.5);
                p.curveTo(4, 9, 4, 4, 8, 1); p.closePath();
                g.fill(p);
                g.fill(new Polygon(new int[]{5, 2, 5}, new int[]{8, 13, 11}, 3));
                g.fill(new Polygon(new int[]{11, 14, 11}, new int[]{8, 13, 11}, 3));
                g.draw(line(7, 13, 7, 15)); g.draw(line(9, 13, 9, 15));
                break;
            }
            case LOCK:
                g.draw(new Arc2D.Double(4.5, 1.5, 7, 9, 0, 180, Arc2D.OPEN));
                g.draw(line(4.5, 6, 4.5, 7.5)); g.draw(line(11.5, 6, 11.5, 7.5));
                g.fill(new RoundRectangle2D.Double(3, 7, 10, 8, 2, 2));
                break;
            case MAP: {
                Path2D p = new Path2D.Double();
                p.moveTo(8, 15); p.curveTo(3, 9, 3, 7, 3, 6); p.curveTo(3, 3, 5.5, 1, 8, 1);
                p.curveTo(10.5, 1, 13, 3, 13, 6); p.curveTo(13, 7, 13, 9, 8, 15); p.closePath();
                g.draw(p);
                g.fill(new Ellipse2D.Double(6, 4, 4, 4));
                break;
            }
            case SWORDS:
                g.draw(line(2, 2, 13, 13)); g.draw(line(14, 2, 3, 13));
                g.draw(line(10, 13, 13, 10)); g.draw(line(3, 10, 6, 13));
                break;
            case BUILDING:
                g.fill(new Polygon(new int[]{1, 8, 15}, new int[]{6, 1, 6}, 3));
                for (int i = 0; i < 4; i++) g.fill(new Rectangle2D.Double(2.5 + i * 3.2, 7, 1.6, 6));
                g.fill(new Rectangle2D.Double(1, 13.5, 14, 1.8));
                break;
            case BOOK:
                g.draw(new RoundRectangle2D.Double(2, 2.5, 12, 11, 1.5, 1.5));
                g.draw(line(8, 2.5, 8, 13.5));
                g.draw(line(4, 6, 6.5, 6)); g.draw(line(9.5, 6, 12, 6)); g.draw(line(4, 9, 6.5, 9)); g.draw(line(9.5, 9, 12, 9));
                break;
            case CUP:
                g.draw(new RoundRectangle2D.Double(2, 6, 9, 8, 3, 3));
                g.draw(new Arc2D.Double(9, 7.5, 5, 5, -90, 180, Arc2D.OPEN));
                g.draw(line(5, 1.5, 5, 4)); g.draw(line(8, 1.5, 8, 4));
                break;
            case CART:
                g.draw(line(1, 2, 3.5, 2)); g.draw(line(3.5, 2, 5, 10.5)); g.draw(line(5, 10.5, 13, 10.5));
                g.draw(line(4.3, 5, 14.5, 5)); g.draw(line(14.5, 5, 13, 10.5));
                g.fill(new Ellipse2D.Double(4.5, 12, 3, 3)); g.fill(new Ellipse2D.Double(10.5, 12, 3, 3));
                break;
            case BUG:
                g.fill(new Ellipse2D.Double(4.5, 4.5, 7, 10));
                g.fill(new Ellipse2D.Double(5.5, 1.5, 5, 4));
                g.draw(line(4.5, 8, 1.5, 7)); g.draw(line(11.5, 8, 14.5, 7));
                g.draw(line(4.5, 11, 1.5, 12.5)); g.draw(line(11.5, 11, 14.5, 12.5));
                break;
            case REFRESH:
                g.draw(new Arc2D.Double(2.5, 2.5, 11, 11, 40, 270, Arc2D.OPEN));
                g.fill(new Polygon(new int[]{14, 14, 10}, new int[]{1, 6, 5}, 3));
                break;
            case STAR:
            case TROPHY: {
                if (s == Shape.TROPHY) {
                    g.fill(new Arc2D.Double(4, -1, 8, 12, 180, 180, Arc2D.CHORD));
                    g.draw(new Arc2D.Double(1.5, 2, 4, 5, 90, 180, Arc2D.OPEN));
                    g.draw(new Arc2D.Double(10.5, 2, 4, 5, -90, 180, Arc2D.OPEN));
                    g.fill(new Rectangle2D.Double(7, 10, 2, 3));
                    g.fill(new Rectangle2D.Double(4.5, 13, 7, 2));
                } else {
                    Path2D p = new Path2D.Double();
                    for (int i = 0; i < 10; i++) {
                        double r = (i % 2 == 0) ? 7 : 3, a = Math.PI / 2 + i * Math.PI / 5;
                        double px = 8 + r * Math.cos(a), py = 8.5 - r * Math.sin(a);
                        if (i == 0) p.moveTo(px, py); else p.lineTo(px, py);
                    }
                    p.closePath();
                    g.fill(p);
                }
                break;
            }
            case CHECK:
                g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(line(2.5, 8.5, 6.5, 12.5)); g.draw(line(6.5, 12.5, 13.5, 3.5));
                break;
            case CROSS:
                g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(line(3.5, 3.5, 12.5, 12.5)); g.draw(line(12.5, 3.5, 3.5, 12.5));
                break;
            case PENCIL:
                g.draw(line(3, 13, 12, 4)); g.draw(line(12, 4, 14, 6)); g.draw(line(14, 6, 5, 15));
                g.draw(line(5, 15, 2, 15.5)); g.draw(line(2, 15.5, 3, 13));
                break;
            case CLOCK:
                g.draw(new Ellipse2D.Double(2, 2.5, 12, 12));
                g.draw(line(8, 8.5, 8, 5)); g.draw(line(8, 8.5, 10.5, 10));
                break;
            case CALENDAR:
                g.draw(new RoundRectangle2D.Double(2, 3, 12, 11.5, 2, 2));
                g.fill(new Rectangle2D.Double(2, 3, 12, 3));
                g.draw(line(5, 1.5, 5, 4)); g.draw(line(11, 1.5, 11, 4));
                g.fill(new Rectangle2D.Double(4.5, 8, 2, 2)); g.fill(new Rectangle2D.Double(9.5, 8, 2, 2));
                break;
            case COIN:
                g.draw(new Ellipse2D.Double(2, 2, 12, 12));
                g.draw(line(8, 4.5, 8, 11.5)); g.draw(line(10, 6, 6.5, 6)); g.draw(line(6, 10, 9.5, 10));
                break;
            case PLAY:
                g.fill(new Polygon(new int[]{4, 13, 4}, new int[]{2, 8, 14}, 3));
                break;
            case DISK:
                g.draw(new RoundRectangle2D.Double(2, 2, 12, 12, 2, 2));
                g.fill(new Rectangle2D.Double(4.5, 2, 6, 4));
                g.fill(new Rectangle2D.Double(4.5, 9, 7, 5));
                break;
            case FOLDER: {
                Path2D p = new Path2D.Double();
                p.moveTo(1.5, 4); p.lineTo(6, 4); p.lineTo(7.5, 5.5); p.lineTo(14.5, 5.5);
                p.lineTo(14.5, 13.5); p.lineTo(1.5, 13.5); p.closePath();
                g.draw(p);
                break;
            }
            case DOC:
                g.draw(new RoundRectangle2D.Double(3, 1.5, 10, 13, 1.5, 1.5));
                g.draw(line(5.5, 5, 10.5, 5)); g.draw(line(5.5, 8, 10.5, 8)); g.draw(line(5.5, 11, 9, 11));
                break;
            case BOLT:
                g.fill(new Polygon(new int[]{9, 3, 7, 6, 13, 9}, new int[]{1, 9, 9, 15, 6, 6}, 6));
                break;
            case BULB:
                g.draw(new Ellipse2D.Double(3.5, 1.5, 9, 9));
                g.draw(line(6, 10, 6, 12.5)); g.draw(line(10, 10, 10, 12.5));
                g.draw(line(6, 12.5, 10, 12.5)); g.draw(line(6.5, 14.5, 9.5, 14.5));
                break;
            case SPEAKER:
                g.fill(new Polygon(new int[]{2, 5, 9, 9, 5, 2}, new int[]{6, 6, 2, 14, 10, 10}, 6));
                g.draw(new Arc2D.Double(8, 4.5, 5, 7, -60, 120, Arc2D.OPEN));
                g.draw(new Arc2D.Double(8, 2, 7.5, 12, -60, 120, Arc2D.OPEN));
                break;
            default:
                break;
        }
    }

    private static Line2D line(double x1, double y1, double x2, double y2) {
        return new Line2D.Double(x1, y1, x2, y2);
    }
}
