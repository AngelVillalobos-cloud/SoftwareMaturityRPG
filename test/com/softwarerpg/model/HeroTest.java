package com.softwarerpg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeroTest {

    @Test
    void unaRecompensaGrandeSubeVariosNiveles() {
        Hero h = new Hero("x");

        h.addXp(1000);

        assertEquals(4, h.getLevel(), "1000 XP >= 840 (nivel 4) y < 1400 (nivel 5)");
        assertEquals(PSPLevel.PSP2_1, h.getPspLevel());
        assertEquals(h.getMaxHp(), h.getHp(), "subir de nivel restaura la salud");
    }

    @Test
    void elResultadoNoDependeDeComoSeRepartaLaXp() {
        Hero enUnaVez = new Hero("a");
        Hero enPartes = new Hero("b");

        enUnaVez.addXp(1000);
        for (int i = 0; i < 10; i++) enPartes.addXp(100);

        assertEquals(enUnaVez.getLevel(), enPartes.getLevel());
        assertEquals(enUnaVez.getPspLevel(), enPartes.getPspLevel());
        assertEquals(enUnaVez.getMaxHp(), enPartes.getMaxHp());
    }

    @Test
    void curvaDeExperiencia() {
        assertEquals(0, Hero.xpForLevel(1));
        assertEquals(140, Hero.xpForLevel(2));
        assertEquals(420, Hero.xpForLevel(3));
        assertEquals(840, Hero.xpForLevel(4));
        assertEquals(1400, Hero.xpForLevel(5));
    }
}
