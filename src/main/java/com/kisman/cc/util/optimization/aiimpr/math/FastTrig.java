package com.kisman.cc.util.optimization.aiimpr.math;

public class FastTrig {
    private static final int ATAN2_BITS = 8;
    private static final int ATAN2_BITS2 = 16;
    private static final int ATAN2_MASK = 65535;
    private static final int ATAN2_COUNT = 65536;
    private static final int ATAN2_DIM;
    private static final float INV_ATAN2_DIM_MINUS_1;
    private static final float[] atan2;
    
    public static void init() {
        for (int i = 0; i < FastTrig.ATAN2_DIM; ++i) {
            for (int j = 0; j < FastTrig.ATAN2_DIM; ++j) {
                final float x0 = i / (float)FastTrig.ATAN2_DIM;
                final float y0 = j / (float)FastTrig.ATAN2_DIM;
                FastTrig.atan2[j * FastTrig.ATAN2_DIM + i] = (float)Math.atan2(y0, x0);
            }
        }
    }
    
    public static float atan2(double y, double x) {
        float mul;
        float add;
        if (x < 0.0) {
            if (y < 0.0) {
                x = -x;
                y = -y;
                mul = 1.0f;
            } else {
                x = -x;
                mul = -1.0f;
            }
            add = -3.1415927f;
        } else {
            if (y < 0.0) {
                y = -y;
                mul = -1.0f;
            } else mul = 1.0f;
            add = 0.0f;
        }
        final double invDiv = 1.0 / ((Math.max(x, y)) * FastTrig.INV_ATAN2_DIM_MINUS_1);
        final int xi = (int)(x * invDiv);
        final int yi = (int)(y * invDiv);
        return (FastTrig.atan2[yi * FastTrig.ATAN2_DIM + xi] + add) * mul;
    }
    
    static {
        ATAN2_DIM = (int)Math.sqrt(65536.0);
        INV_ATAN2_DIM_MINUS_1 = 1.0f / (FastTrig.ATAN2_DIM - 1);
        atan2 = new float[65536];
    }
}
