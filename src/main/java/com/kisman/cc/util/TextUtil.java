package com.kisman.cc.util;

import java.util.Random;
import java.util.regex.Pattern;

public class TextUtil {
    public static final String SECTIONSIGN = "\u00a7";
    public static final String BLACK = "\u00a70";
    public static final String DARK_BLUE = "\u00a71";
    public static final String DARK_GREEN = "\u00a72";
    public static final String DARK_AQUA = "\u00a73";
    public static final String DARK_RED = "\u00a74";
    public static final String DARK_PURPLE = "\u00a75";
    public static final String GOLD = "\u00a76";
    public static final String GRAY = "\u00a77";
    public static final String DARK_GRAY = "\u00a78";
    public static final String BLUE = "\u00a79";
    public static final String GREEN = "\u00a7a";
    public static final String AQUA = "\u00a7b";
    public static final String RED = "\u00a7c";
    public static final String LIGHT_PURPLE = "\u00a7d";
    public static final String YELLOW = "\u00a7e";
    public static final String WHITE = "\u00a7f";
    public static final String OBFUSCATED = "\u00a7k";
    public static final String BOLD = "\u00a7l";
    public static final String STRIKE = "\u00a7m";
    public static final String UNDERLINE = "\u00a7n";
    public static final String ITALIC = "\u00a7o";
    public static final String RESET = "\u00a7r";
    public static final String RAINBOW = "\u00a7+";
    public static final String blank = " \u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592";
    public static final String line1 = " \u2588\u2588\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588";
    public static final String line2 = " \u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2592";
    public static final String line3 = " \u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2588\u2588";
    public static final String line4 = " \u2588\u2592\u2592\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2592\u2592\u2588";
    public static final String line5 = " \u2588\u2592\u2592\u2592\u2588\u2592\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588";
    public static final String pword = "  \u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\n \u2588\u2588\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\n \u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2592\n \u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2588\u2588\n \u2588\u2592\u2592\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2592\u2592\u2588\n \u2588\u2592\u2592\u2592\u2588\u2592\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\n \u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592";
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + "\u00a7" + "[0-9A-FK-OR]");
    private static final Random rand = new Random();
    public static String shrug = "\u00af\\_(\u30c4)_/\u00af";

    public static String stripColor(String input) {
        if (input == null) return null;
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static String coloredString(String string, Color color) {
        String coloredString = string;
        switch (color) {
            case AQUA: {
                coloredString = AQUA + coloredString + RESET;
                break;
            }
            case WHITE: {
                coloredString = WHITE + coloredString + RESET;
                break;
            }
            case BLACK: {
                coloredString = BLACK + coloredString + RESET;
                break;
            }
            case DARK_BLUE: {
                coloredString = DARK_BLUE + coloredString + RESET;
                break;
            }
            case DARK_GREEN: {
                coloredString = DARK_GREEN + coloredString + RESET;
                break;
            }
            case DARK_AQUA: {
                coloredString = DARK_AQUA + coloredString + RESET;
                break;
            }
            case DARK_RED: {
                coloredString = DARK_RED + coloredString + RESET;
                break;
            }
            case DARK_PURPLE: {
                coloredString = DARK_PURPLE + coloredString + RESET;
                break;
            }
            case GOLD: {
                coloredString = GOLD + coloredString + RESET;
                break;
            }
            case DARK_GRAY: {
                coloredString = DARK_GRAY + coloredString + RESET;
                break;
            }
            case GRAY: {
                coloredString = GRAY + coloredString + RESET;
                break;
            }
            case BLUE: {
                coloredString = BLUE + coloredString + RESET;
                break;
            }
            case RED: {
                coloredString = RED + coloredString + RESET;
                break;
            }
            case GREEN: {
                coloredString = GREEN + coloredString + RESET;
                break;
            }
            case LIGHT_PURPLE: {
                coloredString = LIGHT_PURPLE + coloredString + RESET;
                break;
            }
            case YELLOW: {
                coloredString = YELLOW + coloredString + RESET;
                break;
            }
        }
        return coloredString;
    }

    /**
     * Returns the Integers value as a full 32bit hex string:
     * <p>
     * <p>get32BitString(-1) -> "FFFFFFFF"
     * <p>get32BitString(0) -> "00000000"
     * <p>get32BitString(128) -> "00000080"
     * <p>...
     *
     * @param value the integer to get the 32bit string from.
     * @return a 32bit string representing the integers value.
     */
    public static String get32BitString(int value) {
        StringBuilder r = new StringBuilder(Integer.toHexString(value));
        while (r.length() < 8) r.insert(0, 0);
        return r.toString().toUpperCase();
    }

    public static String cropMaxLengthMessage(String s, int i) {
        String output = "";
        if (s.length() >= 256 - i) output = s.substring(0, 256 - i);
        return output;
    }

    public enum Color {
        NONE,
        WHITE,
        BLACK,
        DARK_BLUE,
        DARK_GREEN,
        DARK_AQUA,
        DARK_RED,
        DARK_PURPLE,
        GOLD,
        GRAY,
        DARK_GRAY,
        BLUE,
        GREEN,
        AQUA,
        RED,
        LIGHT_PURPLE,
        YELLOW
    }
}

