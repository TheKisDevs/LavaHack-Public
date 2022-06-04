package com.kisman.cc.util;

import java.awt.*;
import java.util.*;
import java.util.List;

import com.kisman.cc.settings.Setting;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.util.text.TextFormatting;

public class ColorUtil {
    public static List<String> colors = Arrays.asList("Black", "Dark Green", "Dark Red", "Gold", "Dark Gray", "Green", "Red", "Yellow", "Dark Blue", "Dark Aqua", "Dark Purple", "Gray", "Blue", "Aqua", "Light Purple", "White");
    public static ArrayList<String> colours = new ArrayList<>(Arrays.asList("Black", "Dark Green", "Dark Red", "Gold", "Dark Gray", "Green", "Red", "Yellow", "Dark Blue", "Dark Aqua", "Dark Purple", "Gray", "Blue", "Aqua", "Light Purple", "White"));

    public static float seconds = 2;
    public static float saturation = 1;
    public static float briqhtness = 1;

    public int r;
    public int g;
    public int b;
    public int a;

    public int color;

    public int getColor() {
        float hue = (System.currentTimeMillis() % (int)(seconds * 1000)) / (float)(seconds * 1000);
        int color = Color.HSBtoRGB(hue, saturation, briqhtness);
        this.color = color;
        return color;
    }

    public static Color injectAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color injectAlpha(int color, int alpha) {
        return new Color(ColorUtils.getRed(color), ColorUtils.getGreen(color), ColorUtils.getBlue(color), alpha);
    }

    public static Color getGradientOffset(Color one, Color two, double offset, final int alpha) {
        if(offset > 1){
            double left = offset % 1;
            int off = (int) offset;
            offset = off % 2 == 0 ? left : 1 - left;
        }

        double inverse_percent = 1 - offset;

        int redPart = (int) (one.getRed() * inverse_percent + two.getRed() * offset);
        int greenPart = (int) (one.getGreen() * inverse_percent + two.getGreen() * offset);
        int bluePart = (int) (one.getBlue() * inverse_percent + two.getBlue() * offset);

        return new Color(redPart, greenPart, bluePart, alpha);
    }

    public int alpha(Color color, float alpha) {
        final float red = (float) color.getRed() / 255;
        final float green = (float) color.getGreen() / 255;
        final float blue = (float) color.getBlue() / 255;
        r = (int) red;
        g = (int) green;
        b = (int) blue;
        a = (int) alpha;
        return new Color(red, green, blue, alpha).getRGB();
    }

    public static TextFormatting settingToTextFormatting(Setting setting) {
        if (setting.getValString().equalsIgnoreCase("Black")) {
            return TextFormatting.BLACK;
        }
        if (setting.getValString().equalsIgnoreCase("Dark Green")) {
            return TextFormatting.DARK_GREEN;
        }
        if (setting.getValString().equalsIgnoreCase("Dark Red")) {
            return TextFormatting.DARK_RED;
        }
        if (setting.getValString().equalsIgnoreCase("Gold")) {
            return TextFormatting.GOLD;
        }
        if (setting.getValString().equalsIgnoreCase("Dark Gray")) {
            return TextFormatting.DARK_GRAY;
        }
        if (setting.getValString().equalsIgnoreCase("Green")) {
            return TextFormatting.GREEN;
        }
        if (setting.getValString().equalsIgnoreCase("Red")) {
            return TextFormatting.RED;
        }
        if (setting.getValString().equalsIgnoreCase("Yellow")) {
            return TextFormatting.YELLOW;
        }
        if (setting.getValString().equalsIgnoreCase("Dark Blue")) {
            return TextFormatting.DARK_BLUE;
        }
        if (setting.getValString().equalsIgnoreCase("Dark Aqua")) {
            return TextFormatting.DARK_AQUA;
        }
        if (setting.getValString().equalsIgnoreCase("Dark Purple")) {
            return TextFormatting.DARK_PURPLE;
        }
        if (setting.getValString().equalsIgnoreCase("Gray")) {
            return TextFormatting.GRAY;
        }
        if (setting.getValString().equalsIgnoreCase("Blue")) {
            return TextFormatting.BLUE;
        }
        if (setting.getValString().equalsIgnoreCase("Light Purple")) {
            return TextFormatting.LIGHT_PURPLE;
        }
        if (setting.getValString().equalsIgnoreCase("White")) {
            return TextFormatting.WHITE;
        }
        if (setting.getValString().equalsIgnoreCase("Aqua")) {
            return TextFormatting.AQUA;
        }
        return null;
    }

    public static float getSeconds() {
        return seconds;
    }

    public static void setSeconds(float seconds) {
        ColorUtil.seconds = seconds;
    }

    public static float getSaturation() {
        return saturation;
    }

    public static void setSaturation(float saturation) {
        ColorUtil.saturation = saturation;
    }

    public static float getBriqhtness() {
        return briqhtness;
    }

    public static void setBriqhtness(float briqhtness) {
        ColorUtil.briqhtness = briqhtness;
    }
}
