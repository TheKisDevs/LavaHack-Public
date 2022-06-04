package com.kisman.cc.util.customfont;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.client.CustomFontModule;
import com.kisman.cc.util.customfont.norules.CFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.io.InputStream;

public class CustomFontUtil {
    private static final FontRenderer fontRenderer = (Minecraft.getMinecraft()).fontRenderer;

    public static CFontRenderer comfortaal20 = new CFontRenderer(getFontTTF("comfortaa-light", 22), true, true);
    public static CFontRenderer comfortaal18 = new CFontRenderer(getFontTTF("comfortaa-light", 18), true, true);
    public static CFontRenderer comfortaal15 = new CFontRenderer(getFontTTF("comfortaa-light", 15), true, true);
    public static CFontRenderer comfortaal16 = new CFontRenderer(getFontTTF("comfortaa-light", 16), true, true);

    public static CFontRenderer comfortaab72 = new CFontRenderer(getFontTTF("comfortaa-bold", 72), true, true);
    public static CFontRenderer comfortaab55 = new CFontRenderer(getFontTTF("comfortaa-bold", 55), true, true);
    public static CFontRenderer comfortaab20 = new CFontRenderer(getFontTTF("comfortaa-bold", 22), true, true);
    public static CFontRenderer comfortaab18 = new CFontRenderer(getFontTTF("comfortaa-bold", 18), true, true);
    public static CFontRenderer comfortaab16 = new CFontRenderer(getFontTTF("comfortaa-bold", 16), true, true);

    public static CFontRenderer comfortaa20 = new CFontRenderer(getFontTTF("comfortaa-regular", 22), true, true);
    public static CFontRenderer comfortaa18 = new CFontRenderer(getFontTTF("comfortaa-regular", 18), true, true);
    public static CFontRenderer comfortaa15 = new CFontRenderer(getFontTTF("comfortaa-regular", 15), true, true);

    public static CFontRenderer consolas18 = new CFontRenderer(getFontTTF("consolas", 18), true, true);
    public static CFontRenderer consolas16 = new CFontRenderer(getFontTTF("consolas", 16), true, true);
    public static CFontRenderer consolas15 = new CFontRenderer(getFontTTF("consolas", 15), true, true);

    public static CFontRenderer sfui19 = new CFontRenderer(getFontTTF("sf-ui", 19), true, true);
    public static CFontRenderer sfui18 = new CFontRenderer(getFontTTF("sf-ui", 18), true, true);

    public static CFontRenderer futura20 = new CFontRenderer(getFontTTF("futura-normal", 20), true, true);
    public static CFontRenderer futura18 = new CFontRenderer(getFontTTF("futura-normal", 18), true, true);

    public static CFontRenderer lexendDeca18 = new CFontRenderer(getFontTTF("LexendDeca-Regular", 18), true, true);

    public static CustomFontRenderer verdana18 = Kisman.instance.customFontRenderer;
    public static CustomFontRenderer verdana15 = Kisman.instance.customFontRenderer1;

    private static Font getFontTTF(String name, int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/" + name + ".ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    public static int getStringWidth(String text) {
        return CustomFontUtilKt.Companion.getStringWidth(getCustomFontName(), text);
    }

    public static int getStringWidth(String text, boolean gui) {
        return CustomFontUtilKt.Companion.getStringWidth(getCustomFontName(), text, gui);
    }

    public static void drawString(String text, double x, double y, int color, boolean gui) {
        if (customFont()) {
            y += 2;
            Object font = CustomFontUtilKt.Companion.getCustomFont(getCustomFontName(), gui);
            if(font instanceof CFontRenderer) ((CFontRenderer) font).drawString(getStringModofiers() + text, x, y - 1.0D, color, false);
            else if(font instanceof CustomFontRenderer) ((CustomFontRenderer) font).drawString(getStringModofiers() + text, x, y - 1.0D, color, false);
        } else fontRenderer.drawString(getStringModofiers() + text, (int)x, (int)y, color);
    }

    public static int drawString(String text, double x, double y, int color) {
        if (customFont()) {
            y += 2;
            Object font = CustomFontUtilKt.Companion.getCustomFont(getCustomFontName());
            if(font instanceof CFontRenderer) return (int) ((CFontRenderer) font).drawString(getStringModofiers() + text, x, y - 1.0D, color, false);
            else if(font instanceof CustomFontRenderer) return (int) ((CustomFontRenderer) font).drawString(getStringModofiers() + text, x, y - 1.0D, color, false);
        }
        return fontRenderer.drawString(getStringModofiers() + text, (int)x, (int)y, color);
    }

    public static int drawStringWithShadow(String text, double x, double y, int color) {
        if (customFont()) {
            y += 2;
            Object font = CustomFontUtilKt.Companion.getCustomFont(getCustomFontName());
            if(font instanceof CFontRenderer) return (int) ((CFontRenderer) font).drawStringWithShadow(getStringModofiers() + text, x, y - 1.0D, color);
            else if(font instanceof CustomFontRenderer) return (int) ((CustomFontRenderer) font).drawStringWithShadow(getStringModofiers() + text, x, y - 1.0D, color);
        }
        return fontRenderer.drawStringWithShadow(getStringModofiers() + text, (float)x, (float)y, color);
    }

    public static void drawCenteredStringWithShadow(String text, double x, double y, int color) {
        if (customFont()) {
            y += 2;
            Object font = CustomFontUtilKt.Companion.getCustomFont(getCustomFontName());
            if(font instanceof CFontRenderer) ((CFontRenderer) font).drawCenteredStringWithShadow(getStringModofiers() + text, x, y - 1, color);
            else if(font instanceof CustomFontRenderer) ((CustomFontRenderer) font).drawCenteredStringWithShadow(getStringModofiers() + text, (float) x, (float) y - 1, color);
        } else fontRenderer.drawStringWithShadow(getStringModofiers() + text, (float) x - fontRenderer.getStringWidth(getStringModofiers() + text) / 2.0F, (float) y, color);
    }

    public static int getFontHeight(boolean gui) {
        return CustomFontUtilKt.Companion.getHeight(getCustomFontName(), gui);
    }

    public static int getFontHeight(CFontRenderer customFont) {
        return (customFont.fontHeight - 8) / 2;
    }

    public static int getFontHeight() {
        return CustomFontUtilKt.Companion.getHeight(getCustomFontName());
    }

    private static boolean customFont() {
        return CustomFontModule.turnOn;
    }

    public static String getCustomFontName() {
        return CustomFontModule.instance == null ? null : CustomFontModule.instance.mode.getValString();
    }

    private static String getStringModofiers() {
        String str = "";
        if(CustomFontModule.instance != null) {
            if(CustomFontModule.instance.italic.getValBoolean()) str += TextFormatting.ITALIC;
            if(CustomFontModule.instance.bold.getValBoolean() && getCustomFontName().equalsIgnoreCase("Verdana")) str += TextFormatting.BOLD;
        }
        return str;
    }
}