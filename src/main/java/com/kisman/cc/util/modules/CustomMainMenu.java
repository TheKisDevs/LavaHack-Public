package com.kisman.cc.util.modules;
import java.util.Random;

public class CustomMainMenu {
    public static boolean WATERMARK, CUSTOM_SPLASH_TEXT, CUSTOM_SPLASH_FONT, PARTICLES;
    public static final String[] splashes = new String[] {
            "TheKisDevs on tope",
            "meowubic",
            "kisman.cc",
            "kisman.cc+",
            "kidman.club",
            "kisman.cc b0.1.6.1",
            "All of the best client lmao",
            "TheKisDevs inc",
            "lava_hack",
            "water??",
            "kidman own everyone",
            "u got token logget))",
            "sus user",
            "kisman > you",
            "ddev moment",
            "made by _kisman_#5039",
            "Where XuluPlus shaders??",
            "Future? No."
    };

    public static void update() {
    }

    public static String getRandomCustomSplash() {
        Random rand = new Random();
        int i = (int) (splashes.length * rand.nextFloat());
        return splashes[i == splashes.length ? i - 1 : i];
    }
}
