package com.kisman.cc.util;

import net.minecraft.client.Minecraft;

public class GCDUtil {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static float getFixedRotation(float rot) {
        return GCDUtil.getDeltaMouse(rot) * getGCDValue();
    }

    public static float getGCDValue() {
        return (float) ((double) getGCD() * 0.15);
    }

    public static float getGCD() {
        float f1 = (float) ((double) mc.gameSettings.mouseSensitivity * 0.6 + 0.2);
        return f1 * f1 * f1 * 8.0f;
    }

    public static float getDeltaMouse(float delta) {
        return Math.round(delta / getGCDValue());
    }
}
