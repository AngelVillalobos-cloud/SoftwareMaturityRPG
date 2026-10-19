package com.softwarerpg.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UiSupportTest {

    @Test
    void losEmojisQueLaFuenteNoPuedeMostrarSeQuitan() {
        // Sin fuente se asume que no puede mostrarlos (como Segoe UI en Windows)
        assertEquals("¡VICTORIA! Ganaste", EmojiSupport.clean("🎉 ¡VICTORIA! Ganaste", null));
        assertEquals("Org. MoProSoft", EmojiSupport.clean("🇲🇽 Org. MoProSoft", null));
        assertEquals("Ingeniero", EmojiSupport.clean("👨‍💻 Ingeniero", null));
        assertEquals("Texto normal con acentos: ñ á → ok", EmojiSupport.clean("Texto normal con acentos: ñ á → ok", null));
    }

    @Test
    void cadaEmojiDeLaInterfazTieneIcono() {
        int[] usados = {0x1F4BB, 0x1F4CB, 0x1F4D0, 0x1F91D, 0x1F4CA, 0x1F680, 0x1F512, 0x1F5FA, 0x2694,
                        0x1F4D6, 0x1F6D2, 0x1F47E, 0x2615, 0x1F4BE, 0x1F4C2, 0x1F195, 0x1F50A};
        for (int cp : usados) assertNotNull(Icons.forEmoji(cp), String.format("sin icono para U+%X", cp));
    }

    @Test
    void losEfectosDeSonidoSeSintetizan() {
        for (Sound.Sfx sfx : Sound.Sfx.values()) {
            byte[] pcm = Sound.samples(sfx);
            assertTrue(pcm.length > 0 && pcm.length % 2 == 0, "PCM de 16 bits para " + sfx);
        }
    }
}
