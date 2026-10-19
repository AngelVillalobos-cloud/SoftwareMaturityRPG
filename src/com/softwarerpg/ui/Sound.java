package com.softwarerpg.ui;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Efectos de sonido sintetizados en el momento (sin archivos ni dependencias externas).
 * Se reproducen en un hilo aparte para no congelar la interfaz. Si el equipo no tiene
 * salida de audio, el sonido se desactiva en silencio.
 */
public final class Sound {
    public enum Sfx { CLICK, COIN, ERROR, ATTACK, HEAL, POWER, VICTORY, DEFEAT }

    private static final float RATE = 22050f;
    private static final Map<Sfx, byte[]> CACHE = new EnumMap<>(Sfx.class);
    private static final ExecutorService PLAYER = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "sonido-rpg");
        t.setDaemon(true);
        return t;
    });

    private static volatile boolean enabled = true;
    private static volatile boolean available = true;

    private Sound() { }

    public static boolean isEnabled() { return enabled; }
    public static void setEnabled(boolean on) { enabled = on; }

    public static void play(Sfx sfx) {
        if (!enabled || !available) return;
        PLAYER.execute(() -> {
            try {
                byte[] pcm = samples(sfx);
                AudioFormat format = new AudioFormat(RATE, 16, 1, true, false);
                Clip clip = AudioSystem.getClip();
                clip.open(new AudioInputStream(new ByteArrayInputStream(pcm), format, pcm.length / 2));
                clip.addLineListener(e -> { if (e.getType() == LineEvent.Type.STOP) clip.close(); });
                clip.start();
            } catch (Exception | LinkageError e) {
                available = false; // sin dispositivo de audio: no volver a intentarlo
            }
        });
    }

    /** PCM de 16 bits del efecto (se genera una vez y se guarda). */
    static synchronized byte[] samples(Sfx sfx) {
        return CACHE.computeIfAbsent(sfx, Sound::synthesize);
    }

    private static byte[] synthesize(Sfx sfx) {
        Synth s = new Synth();
        switch (sfx) {
            case CLICK:   s.tone(1100, 1100, 25, Wave.SQUARE, 0.12); break;
            case COIN:    s.tone(988, 988, 70, Wave.SQUARE, 0.18); s.tone(1319, 1319, 160, Wave.SQUARE, 0.18); break;
            case ERROR:   s.tone(220, 220, 110, Wave.SQUARE, 0.18); s.tone(165, 165, 170, Wave.SQUARE, 0.18); break;
            case ATTACK:  s.noise(40, 0.20); s.tone(440, 180, 110, Wave.SQUARE, 0.16); break;
            case HEAL:    s.tone(523, 523, 70, Wave.SINE, 0.30); s.tone(659, 659, 70, Wave.SINE, 0.30); s.tone(784, 784, 120, Wave.SINE, 0.30); break;
            case POWER:   s.tone(350, 950, 180, Wave.SQUARE, 0.14); break;
            case VICTORY: s.tone(523, 523, 110, Wave.SQUARE, 0.16); s.tone(659, 659, 110, Wave.SQUARE, 0.16);
                          s.tone(784, 784, 110, Wave.SQUARE, 0.16); s.tone(1047, 1047, 320, Wave.SQUARE, 0.16); break;
            case DEFEAT:  s.tone(392, 392, 180, Wave.SQUARE, 0.16); s.tone(330, 330, 180, Wave.SQUARE, 0.16);
                          s.tone(262, 200, 450, Wave.SQUARE, 0.16); break;
            default: break;
        }
        return s.toBytes();
    }

    private enum Wave { SINE, SQUARE }

    /** Sintetizador mínimo: secuencia de tonos con barrido de frecuencia y envolvente. */
    private static final class Synth {
        private final java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        private final java.util.Random noise = new java.util.Random(7);

        void tone(double fromHz, double toHz, int ms, Wave wave, double volume) {
            int n = (int) (RATE * ms / 1000);
            double phase = 0;
            for (int i = 0; i < n; i++) {
                double t = (double) i / n;
                double hz = fromHz + (toHz - fromHz) * t;
                phase += 2 * Math.PI * hz / RATE;
                double v = wave == Wave.SINE ? Math.sin(phase) : (Math.sin(phase) >= 0 ? 1 : -1);
                write(v * volume * envelope(i, n));
            }
        }

        void noise(int ms, double volume) {
            int n = (int) (RATE * ms / 1000);
            for (int i = 0; i < n; i++) write((noise.nextDouble() * 2 - 1) * volume * envelope(i, n));
        }

        /** Entrada y salida suaves de 5 ms para evitar chasquidos. */
        private double envelope(int i, int n) {
            int fade = (int) (RATE * 0.005);
            return Math.min(1.0, Math.min((double) i / fade, (double) (n - i) / fade));
        }

        private void write(double v) {
            short sample = (short) Math.round(Math.max(-1, Math.min(1, v)) * Short.MAX_VALUE);
            out.write(sample & 0xFF);
            out.write((sample >> 8) & 0xFF);
        }

        byte[] toBytes() { return out.toByteArray(); }
    }
}
