package com.softwarerpg.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.beans.PropertyChangeListener;

/**
 * Compatibilidad de emojis entre sistemas operativos.
 *
 * En Windows, Java no puede dibujar emojis con la fuente Segoe UI y aparecen como "▯".
 * Este instalador global revisa cada etiqueta, botón, menú y pestaña que se agrega a la
 * interfaz (incluidos los diálogos):
 *  - Si el texto EMPIEZA con un emoji conocido, lo cambia por un icono vectorial ({@link Icons}).
 *  - Cualquier otro emoji que la fuente no pueda mostrar se quita del texto.
 *
 * Con -Dsoftwarerpg.noEmoji=true se quitan todos los emojis (útil para probar en Linux
 * cómo se ve en Windows).
 */
public final class EmojiSupport {
    private static final String HOOKED = "softwarerpg.emojiHooked";
    private static final String OWN_ICON = "softwarerpg.emojiIcon";
    private static final boolean FORCE_STRIP = Boolean.getBoolean("softwarerpg.noEmoji");

    private static boolean installed = false;
    private static boolean applying = false; // evita reentrar al modificar el texto nosotros mismos

    private EmojiSupport() { }

    /** Instala el revisor global (idempotente). Llamar en el hilo de eventos de Swing. */
    public static synchronized void install() {
        if (installed) return;
        installed = true;
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (event.getID() != ContainerEvent.COMPONENT_ADDED) return;
            ContainerEvent ce = (ContainerEvent) event;
            process(ce.getChild());
            if (ce.getContainer() instanceof JTabbedPane) processTabs((JTabbedPane) ce.getContainer());
        }, AWTEvent.CONTAINER_EVENT_MASK);
        for (Window w : Window.getWindows()) process(w);
    }

    /** Revisa un componente y todos sus hijos. */
    public static void process(Component c) {
        if (c instanceof JLabel || c instanceof AbstractButton) hook((JComponent) c);
        if (c instanceof JTabbedPane) processTabs((JTabbedPane) c);
        if (c instanceof JMenu) {
            for (Component item : ((JMenu) c).getMenuComponents()) process(item);
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) process(child);
        }
    }

    /** Limpia un texto para mostrarlo con la fuente indicada (consola, tablas). */
    public static String clean(String text, Font font) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder out = new StringBuilder(text.length());
        int i = 0;
        while (i < text.length()) {
            int cp = text.codePointAt(i);
            int end = clusterEnd(text, i);
            if (isEmoji(cp) && (FORCE_STRIP || font == null || font.canDisplayUpTo(text.substring(i, end)) != -1)) {
                // Quitar el emoji y un espacio que lo siga
                if (end < text.length() && text.charAt(end) == ' ') end++;
            } else {
                out.append(text, i, end);
            }
            i = end;
        }
        return out.toString();
    }

    // ------------------------------------------------------------------ Implementación

    private static void hook(JComponent c) {
        // Cada componente se procesa una sola vez; después solo se reacciona a cambios de texto.
        // (Volver a procesarlo quitaría el icono, porque su texto ya no empieza con emoji.)
        if (c.getClientProperty(HOOKED) != null) return;
        c.putClientProperty(HOOKED, Boolean.TRUE);
        PropertyChangeListener l = e -> { if (!applying) apply(c); };
        c.addPropertyChangeListener("text", l);
        apply(c);
    }

    private static void apply(JComponent c) {
        String text = c instanceof JLabel ? ((JLabel) c).getText() : ((AbstractButton) c).getText();
        if (text == null || text.isEmpty()) return;
        Icon leading = null;
        String rest = text;
        if (!text.regionMatches(true, 0, "<html", 0, 5)) {
            int cp = text.codePointAt(0);
            if (isEmoji(cp)) {
                leading = Icons.forEmoji(cp);
                if (leading != null) rest = text.substring(clusterEnd(text, 0)).stripLeading();
            }
        }
        String cleaned = clean(rest, c.getFont());
        applying = true;
        try {
            if (leading != null) {
                setIcon(c, leading);
                c.putClientProperty(OWN_ICON, Boolean.TRUE);
            } else if (Boolean.TRUE.equals(c.getClientProperty(OWN_ICON))) {
                // El texto nuevo ya no empieza con emoji: quitar el icono que pusimos antes
                setIcon(c, null);
                c.putClientProperty(OWN_ICON, null);
            }
            if (!cleaned.equals(text)) {
                if (c instanceof JLabel) ((JLabel) c).setText(cleaned); else ((AbstractButton) c).setText(cleaned);
            }
            if (leading != null && c instanceof AbstractButton) ((AbstractButton) c).setIconTextGap(6);
        } finally {
            applying = false;
        }
    }

    private static void setIcon(JComponent c, Icon icon) {
        if (c instanceof JLabel) ((JLabel) c).setIcon(icon); else ((AbstractButton) c).setIcon(icon);
    }

    private static void processTabs(JTabbedPane tabs) {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            String title = tabs.getTitleAt(i);
            if (title == null || title.isEmpty()) continue;
            int cp = title.codePointAt(0);
            if (isEmoji(cp)) {
                Icon icon = Icons.forEmoji(cp);
                String rest = title.substring(clusterEnd(title, 0)).stripLeading();
                if (icon != null) tabs.setIconAt(i, icon);
                tabs.setTitleAt(i, clean(rest, tabs.getFont()));
            }
        }
    }

    /** Fin del grupo de un emoji: variaciones, tonos de piel, uniones ZWJ y banderas. */
    private static int clusterEnd(String s, int start) {
        int cp = s.codePointAt(start);
        int i = start + Character.charCount(cp);
        boolean regional = cp >= 0x1F1E6 && cp <= 0x1F1FF;
        if (regional && i < s.length()) {
            int next = s.codePointAt(i);
            if (next >= 0x1F1E6 && next <= 0x1F1FF) i += Character.charCount(next);
        }
        while (i < s.length()) {
            int next = s.codePointAt(i);
            if (next == 0xFE0F || next == 0x20E3 || (next >= 0x1F3FB && next <= 0x1F3FF)) {
                i += Character.charCount(next);
            } else if (next == 0x200D && i + 1 < s.length()) {
                i += 1;
                i += Character.charCount(s.codePointAt(i));
            } else {
                break;
            }
        }
        return i;
    }

    private static boolean isEmoji(int cp) {
        return cp >= 0x1F000 || (cp >= 0x2300 && cp <= 0x2BFF) || cp == 0x270D || cp == 0xFE0F;
    }
}
