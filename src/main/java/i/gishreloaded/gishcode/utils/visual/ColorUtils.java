package i.gishreloaded.gishcode.utils.visual;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.Random;

import com.kisman.cc.util.Colour;

public class ColorUtils {
    public static Colour getRandomColour() {
        return Colour.fromHSB(new float[] {new Random().nextFloat(), 1, 1}, 255);
    }

    public static Colour twoColorEffect(final Colour color, final Colour color2, double delay) {
        if (delay > 1.0) {
            final double n2 = delay % 1.0;
            delay = (((int)delay % 2 == 0) ? n2 : (1.0 - n2));
        }
        final double n3 = 1.0 - delay;
        return new Colour((color.r * n3 + color2.r * delay),  (color.g * n3 + color2.g * delay), (color.b * n3 + color2.b * delay), (color.a * n3 + color2.a * delay));
    }

    public static Colour healthColor(float hp, float maxHP) {
        int pct = (int) ((hp / maxHP) * 255F);
        return new Colour(Math.max(Math.min(255 - pct, 255), 0), Math.max(Math.min(pct, 255), 0), 0, 255);
    }

    public static int getAstolfoRainbow(int offset) {
        float speed = 2900F;
        float hue = (float) (System.currentTimeMillis() % (int)speed) + ((1000 - offset) * 9);
        while (hue > speed) hue -= speed;
        hue /= speed;
        if (hue > 0.5) hue = 0.5F - (hue - 0.5f);
        hue += 0.5F;
        return Color.HSBtoRGB(hue, 0.65f, 1f);
    }

	public static Color rainbow() {
		long offset = 999999999999L;
        float hue = (float) (System.nanoTime() + offset) / 1.0E10f % 1.0f;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)), 16);
        Color c = new Color((int) color);
        return new Color((float) c.getRed() / 255.0f, (float) c.getGreen() / 255.0f, (float) c.getBlue() / 255.0f, (float) c.getAlpha() / 255.0f);
    }

    public static int astolfoColors(int yOffset, int yTotal) {
        float hue;
        float speed = 2900.0f;
        for (hue = (float) (System.currentTimeMillis() % (long) ((int)speed)) + (float) ((yTotal - yOffset) * 9); hue > speed; hue -= speed) {}
        if ((double) (hue /= speed) > 0.5) {
            hue = 0.5f - (hue - 0.5f);
        }
        return Color.HSBtoRGB(hue += 0.5f, 0.5f, 1.0f);
    }

    public static Color astolfoColorsToColorObj(int yOffset, int yTotal, int alpha) {
        int color = astolfoColors(yOffset, yTotal);

        return new Color(getRed(color), getGreen(color), getBlue(color), alpha);
    }

    public static Color astolfoColorsToColorObj(int yOffset, int yTotal) { return astolfoColorsToColorObj(yOffset, yTotal, 255); }

    public static int rainbow(int delay, long index) {
        double rainbowState = Math.ceil(System.currentTimeMillis() + index + (long)delay) / 15.0;
        return Color.getHSBColor((float) ((rainbowState %= 360f) / 360f), 0.4f, 1.0f).getRGB();
    }

    public static int rainbowLT(int delay, long index) {
        double rainbowState = Math.ceil(System.currentTimeMillis() + index + (long)delay) / 3;
        return Color.getHSBColor((float) ((rainbowState %= 248.0) / 248.0), 0.5f, 0.6f).getRGB();
    }

    public static void glColor(final int hex, final int alpha) {
        final float red = (hex >> 16 & 0xFF) / 255F;
        final float green = (hex >> 8 & 0xFF) / 255F;
        final float blue = (hex & 0xFF) / 255F;

        GlStateManager.color(red, green, blue, alpha / 255F);
    }

    public static int color(int r, int g, int b, int a) {return new Color(r, g, b, a).getRGB();}
    public static int color(float r, float g, float b, float a) {return new Color(r, g, b, a).getRGB();}
    public static int getColor(int a, int r, int g, int b) {return a << 24 | r << 16 | g << 8 | b;}
    public static int getColor(int r, int g, int b) {return 255 << 24 | r << 16 | g << 8 | b;}
    public static int getColor(Color color) {return color.getRed() | color.getGreen() << 8 | color.getBlue() << 16 | color.getAlpha() << 24;}
    public static Color rainbow(final int delay, final float s, final float b) {return Color.getHSBColor((System.currentTimeMillis() + delay) % 11520L / 11520.0f, s, b);}
    public static int getRed(int color) {return new Color(color).getRed();}
    public static int getGreen(int color) {return new Color(color).getGreen();}
    public static int getBlue(int color) {return new Color(color).getBlue();}
    public static int getAlpha(int color) {return new Color(color).getAlpha();}
    public static Color rainbowRGB(int delay, float s, float b) {return new Color(getRed((Color.HSBtoRGB((System.currentTimeMillis() + delay) % 11520L / 11520.0f, s, b))), getGreen(Color.HSBtoRGB((System.currentTimeMillis() + delay) % 11520L / 11520.0f, s, b)), getBlue(Color.HSBtoRGB((System.currentTimeMillis() + delay) % 11520L / 11520.0f, s, b)));}
    public static int getColor(int brightness) {return ColorUtils.getColor(brightness, brightness, brightness, 255);}
    public static int getColor(int brightness, int alpha) {return ColorUtils.getColor(brightness, brightness, brightness, alpha);}
    public static Color injectAlpha(final Color color, final int alpha) {return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);}
    public static Color injectAlpha(int color, int alpha) {return new Color(ColorUtils.getRed(color), ColorUtils.getGreen(color), ColorUtils.getBlue(color), alpha);}
    public static void glColor(Color color) {GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);}
}
