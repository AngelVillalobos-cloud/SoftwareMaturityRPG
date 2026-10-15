package com.softwarerpg.engine;

import java.util.Random;

/**
 * Generador "aleatorio" con valores fijos para que los tests sean deterministas.
 * Con nextDouble = 0.5 la variación de daño (±15%) es exactamente 0 y no se
 * inyectan defectos con PSP 0 (probabilidad 0.40).
 */
class FixedRandom extends Random {
    private static final long serialVersionUID = 1L;

    private final double doubleValue;

    FixedRandom(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    @Override
    public double nextDouble() { return doubleValue; }

    @Override
    public int nextInt(int bound) { return 0; }

    @Override
    public synchronized double nextGaussian() { return 0.0; }
}
