package com.kisman.cc.gui.csgo.components;

import com.kisman.cc.module.client.Config;
import com.kisman.cc.gui.csgo.*;
import com.kisman.cc.gui.csgo.Window;
import com.kisman.cc.util.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.kisman.cc.util.Render2DUtil.drawGradientRect;

public class ColorButton extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 24;

    private final int preferredWidth;
    private final int preferredHeight;
    private boolean hovered;
    private boolean opened;

    private Colour value;
    private boolean value2;

    private float[] color;
    private boolean pickingColor;
    private boolean pickingHue;
    private boolean pickingAlpha;
    private int pickerX, pickerY, pickerWidth, pickerHeight;
    private int hueSliderX, hueSliderY, hueSliderWidth, hueSliderHeight;
    private int alphaSliderX, alphaSliderY, alphaSliderWidth, alphaSliderHeight;
    private int rainbowX, rainbowY, rainbowWidth, rainbowHeight;
    private int selectedColorFinal;

    private ValueChangeListener<Colour> listener;
    private ValueChangeListener<Boolean> listener2;

    public ColorButton(IRenderer renderer, Colour colour, int preferredWidth, int preferredHeight) {
        super(renderer);
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        this.value = colour;
        float[] hsb = Color.RGBtoHSB(value.r, value.g, value.b, null);
        this.color = new float[] {hsb[0], hsb[1], hsb[2], value.a1};
        this.pickingColor = this.pickingAlpha = this.pickingHue = false;

        this.pickerWidth = 120;
        this.pickerHeight = 100;
        this.pickerX = x / 2;
        this.pickerY = y / 2 + preferredHeight / 2;
        this.hueSliderX = pickerX;
        this.hueSliderY = pickerY + pickerHeight + 6;
        this.hueSliderWidth = pickerWidth;
        this.hueSliderHeight = 10;
        this.alphaSliderX = pickerX + pickerWidth + 6;
        this.alphaSliderY = pickerY;
        this.alphaSliderWidth = 10;
        this.alphaSliderHeight = pickerHeight;
        this.rainbowX = pickerX;
        this.rainbowY = hueSliderY + hueSliderHeight + 6;
        this.rainbowHeight = CustomFontUtil.getFontHeight() + 10;
        this.rainbowWidth = rainbowHeight;

        updateWidth();
        updateHeight();
    }

    public ColorButton(IRenderer renderer, Colour colour) {
        this(renderer, colour, PREFERRED_WIDTH, PREFERRED_HEIGHT);
    }

    private void updateWidth() {
        if(opened) setWidth((pickerWidth + 6 + alphaSliderWidth) * 2);
        else setWidth(preferredWidth);
    }

    private void updateHeight() {
        if (opened) setHeight(preferredHeight + (pickerHeight + 6 * 2 + hueSliderHeight + rainbowHeight) * 2);
        else setHeight(preferredHeight);
    }

    @Override
    public void render() {
        this.pickerX = x / 2;
        this.pickerY = y / 2 + preferredHeight / 2;
        this.hueSliderX = pickerX;
        this.hueSliderY = pickerY + pickerHeight + 6;
        this.hueSliderWidth = pickerWidth;
        this.hueSliderHeight = 10;
        this.alphaSliderX = pickerX + pickerWidth + 6;
        this.alphaSliderY = pickerY;
        this.alphaSliderWidth = 10;
        this.alphaSliderHeight = pickerHeight;
        this.rainbowX = pickerX;
        this.rainbowY = hueSliderY + hueSliderHeight + 6;
        this.rainbowHeight = CustomFontUtil.getFontHeight() + 10;
        this.rainbowWidth = rainbowHeight;
        updateWidth();
        updateHeight();

        try {if(!opened) renderer.drawRect(x, y, getWidth(), getHeight(), value.getColor());} catch(Exception ignored) {}
        if(opened) {
            String text = "Red: " + value.r + " Green: " + value.g + " Blue: " + value.b;
            renderer.drawString(x + getWidth() / 2 - renderer.getStringWidth(text) / 2, y + preferredHeight / 2 - renderer.getStringHeight(text) / 2, text, Window.FOREGROUND);
        }
        renderer.drawOutline(x, y, getWidth(), getHeight(), 1.0f, (hovered) ? Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND);

        if(Config.instance.guiGlow.getValBoolean()) Render2DUtil.drawRoundedRect(x / 2, y / 2, (x + getWidth()) / 2, (y + preferredHeight) / 2, value.getColor(), Config.instance.glowBoxSize.getValDouble());

        if (opened) {
            int selectedX = pickerX + pickerWidth + 6;
            int selectedY = pickerY + pickerHeight + 6;
            int selectedWidth = 10;
            int selectedHeight = 10;
            int selectedColor = Color.HSBtoRGB(this.color[0], 1.0f, 1.0f);
            float selectedRed = (selectedColor >> 16 & 0xFF) / 255.0f;
            float selectedGreen = (selectedColor >> 8 & 0xFF) / 255.0f;
            float selectedBlue = (selectedColor & 0xFF) / 255.0f;
            this.drawPickerBase(pickerX, pickerY, pickerWidth, pickerHeight, selectedRed, selectedGreen, selectedBlue, this.color[3]);
            this.drawHueSlider(hueSliderX, hueSliderY, hueSliderWidth, hueSliderHeight, this.color[0]);
            this.drawAlphaSlider(alphaSliderX, alphaSliderY, alphaSliderWidth, alphaSliderHeight, selectedRed, selectedGreen, selectedBlue, this.color[3]);
//            this.drawButton(rainbowX, rainbowY, rainbowWidth, rainbowHeight, value2, "Rainbow");
            //final int
            this.selectedColorFinal = alpha(new Color(Color.HSBtoRGB(this.color[0], this.color[1], this.color[2])), this.color[3]);
            Gui.drawRect(selectedX, selectedY, selectedX + selectedWidth, selectedY + selectedHeight, this.selectedColorFinal);

            {
                final int cursorX = (int) (pickerX + color[1]*pickerWidth);
                final int cursorY = (int) ((pickerY + pickerHeight) - color[2]*pickerHeight);
                Gui.drawRect(cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2, -1);
            }

            value = new Colour(selectedColorFinal);
        }

        if(listener != null) listener.onValueChange(value);
        if(listener2 != null) listener2.onValueChange(value2);
    }

    @Override
    public void postRender() {}

    private void drawButton(int x, int y, int width, int height, boolean state, String title) {
        renderer.drawRect(x, y, width, height, hovered ? Window.SECONDARY_FOREGROUND : Window.TERTIARY_FOREGROUND);

        if (state) {
            Color color = hovered ? Config.instance.guiAstolfo.getValBoolean() ? renderer.astolfoColorToObj() : Window.TERTIARY_FOREGROUND : Window.SECONDARY_FOREGROUND;

            renderer.drawRect(x, y, width, height,color);
        }

        renderer.drawOutline(x, y, width, height, 1.0f, hovered ? Config.instance.guiAstolfo.getValBoolean() ? renderer.astolfoColorToObj() : Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND);
        if(Config.instance.guiGlow.getValBoolean() && state) Render2DUtil.drawRoundedRect(x / 2, y / 2, (x + width) / 2, (y + height) / 2, hovered ? Config.instance.guiAstolfo.getValBoolean() ? renderer.astolfoColorToObj() : Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND, Config.instance.glowBoxSize.getValDouble());
        renderer.drawString(x + width + height / 4, y + getHeight() / 2 - renderer.getStringHeight(title) / 2, title, Window.FOREGROUND);
    }

    final int alpha(Color color, float alpha) {
        return new Colour(color, alpha).getRGB();
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + getWidth() && y <= this.y + getHeight();
    }

    private boolean kismanontop(int x, int y, boolean offscreen) {
        return !offscreen && x >= this.x && y >= this.y && x <= this.x + getWidth() && y <= this.y + preferredHeight;
    }

    protected boolean check(int minX, int minY, int maxX, int maxY, int curX, int curY) {
        return curX >= minX && curY >= minY && curX < maxX && curY < maxY;
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        updateHovered(x, y, offscreen);
        updateValue(x / 2 , y / 2);
        return false;
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        if (button == 0) {
            updateHovered(x, y, offscreen);

            if(kismanontop(x, y, offscreen)) {
                opened = !opened;
                updateWidth();
                updateHeight();
                return true;
            }
            if(opened) {
                pickingColor = !offscreen && check(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, x / 2, y / 2);
                pickingHue = !offscreen && check(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, x / 2, y / 2);
                pickingAlpha = !offscreen && check(alphaSliderX, alphaSliderY, alphaSliderX + alphaSliderWidth, alphaSliderY + alphaSliderHeight, x / 2, y / 2);
                if(pickingAlpha || pickingColor || pickingHue) updateValue(x / 2, y / 2);
                return pickingColor || pickingHue || pickingAlpha;
            }
        }
        return false;
    }

    private void updateValue(int mouseX, int mouseY) {
        if (this.pickingHue) {
            if (this.hueSliderWidth > this.hueSliderHeight) {
                float restrictedX = (float) Math.min(Math.max(hueSliderX, mouseX), hueSliderX + hueSliderWidth);
                this.color[0] = (restrictedX - (float) hueSliderX) / hueSliderWidth;
            } else {
                float restrictedY = (float) Math.min(Math.max(hueSliderY, mouseY), hueSliderY + hueSliderHeight);
                this.color[0] = (restrictedY - (float) hueSliderY) / hueSliderHeight;
            }
        }
        if (this.pickingAlpha) {
            if (this.alphaSliderWidth > this.alphaSliderHeight) {
                float restrictedX = (float) Math.min(Math.max(alphaSliderX, mouseX), alphaSliderX + alphaSliderWidth);
                this.color[3] = 1 - (restrictedX - (float) alphaSliderX) / alphaSliderWidth;
            } else {
                float restrictedY = (float) Math.min(Math.max(alphaSliderY, mouseY), alphaSliderY + alphaSliderHeight);
                this.color[3] = 1 - (restrictedY - (float) alphaSliderY) / alphaSliderHeight;
            }
        }
        if (this.pickingColor) {
            float restrictedX = (float) Math.min(Math.max(pickerX, mouseX), pickerX + pickerWidth);
            float restrictedY = (float) Math.min(Math.max(pickerY, mouseY), pickerY + pickerHeight);
            this.color[1] = (restrictedX - (float) pickerX) / pickerWidth;
            this.color[2] = 1 - (restrictedY - (float) pickerY) / pickerHeight;
        }
    }

    @Override
    public boolean mouseReleased(int button, int x, int y, boolean offscreen) {
        updateHovered(x, y, offscreen);
        if((pickingColor || pickingHue || pickingAlpha) && button == 0) {
            updateValue(x / 2, y / 2);
            pickingColor = pickingHue = pickingAlpha = false;
            return true;
        }
        return false;
    }

    public Colour getValue() {
        return value;
    }

    public boolean getValue2() {
        return value2;
    }

    public void setValue(boolean value) {
        value2 = value;
    }

    public void setValue(Colour value) {
        this.value = value;
        float[] hsb = Color.RGBtoHSB(value.r, value.g, value.b, null);
        color = new float[] {hsb[0], hsb[1], hsb[2], value.a1};
    }

    public void setListener(ValueChangeListener<Colour> listener) {this.listener = listener;}
    public void setListener2(ValueChangeListener<Boolean> listener) {this.listener2 = listener;}

    private void drawHueSlider(int x, int y, int width, int height, float hue) {
        int step = 0;
        if (height > width) {
            Gui.drawRect(x, y, x + width, y + 4, 0xFFFF0000);
            y += 4;
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step/6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step+1)/6, 1.0f, 1.0f);
                drawGradientRect(x, y + step * (height/6), x + width, y + (step+1) * (height/6), previousStep, nextStep);
                step++;
            }
            final int sliderMinY = (int) (y + (height*hue)) - 4;
            Gui.drawRect(x, sliderMinY - 1, x+width, sliderMinY + 1, -1);
        } else {
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step/6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step+1)/6, 1.0f, 1.0f);
                this.gradient(x + step * (width/6), y, x + (step+1) * (width/6), y + height, previousStep, nextStep, true);
                step++;
            }
            final int sliderMinX = (int) (x + (width*hue));
            Gui.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
        }
    }

    private void drawAlphaSlider(int x, int y, int width, int height, float red, float green, float blue, float alpha) {
        boolean left = true;
        int checkerBoardSquareSize = width/2;
        for (int squareIndex = -checkerBoardSquareSize; squareIndex < height; squareIndex += checkerBoardSquareSize) {
            if (!left) {
                Gui.drawRect(x, y + squareIndex, x + width, y + squareIndex + checkerBoardSquareSize, 0xFFFFFFFF);
                Gui.drawRect(x + checkerBoardSquareSize, y + squareIndex, x + width, y + squareIndex + checkerBoardSquareSize, 0xFF909090);
                if (squareIndex < height - checkerBoardSquareSize) {
                    int minY = y + squareIndex + checkerBoardSquareSize;
                    int maxY = Math.min(y + height, y + squareIndex + checkerBoardSquareSize*2);
                    Gui.drawRect(x, minY, x + width, maxY, 0xFF909090);
                    Gui.drawRect(x + checkerBoardSquareSize, minY, x + width, maxY, 0xFFFFFFFF);
                }
            }
            left = !left;
        }
        this.gradient(x, y, x + width, y + height, new Color(red, green, blue, alpha).getRGB(), 0, false);
        final int sliderMinY = (int) (y + height - (height * alpha));
        Gui.drawRect(x, sliderMinY - 1, x+width, sliderMinY + 1, -1);
    }

    private void drawPickerBase(int pickerX, int pickerY, int pickerWidth, int pickerHeight, float red, float green, float blue, float alpha) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_POLYGON);
        {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glVertex2f(pickerX, pickerY);
            GL11.glVertex2f(pickerX, pickerY + pickerHeight);
            GL11.glColor4f(red, green, blue, alpha);
            GL11.glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
            GL11.glVertex2f(pickerX + pickerWidth, pickerY);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBegin(GL11.GL_POLYGON);
        {
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glVertex2f(pickerX, pickerY);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glVertex2f(pickerX, pickerY + pickerHeight);
            GL11.glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glVertex2f(pickerX + pickerWidth, pickerY);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    protected void gradient(int minX, int minY, int maxX, int maxY, int startColor, int endColor, boolean left) {
        if (left) {
            final float startA = (startColor >> 24 & 0xFF) / 255.0f;
            final float startR = (startColor >> 16 & 0xFF) / 255.0f;
            final float startG= (startColor >> 8 & 0xFF) / 255.0f;
            final float startB = (startColor & 0xFF) / 255.0f;

            final float endA = (endColor >> 24 & 0xFF) / 255.0f;
            final float endR = (endColor >> 16 & 0xFF) / 255.0f;
            final float endG = (endColor >> 8 & 0xFF) / 255.0f;
            final float endB = (endColor & 0xFF) / 255.0f;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glBegin(GL11.GL_POLYGON);
            {
                GL11.glColor4f(startR, startG, startB, startA);
                GL11.glVertex2f(minX, minY);
                GL11.glVertex2f(minX, maxY);
                GL11.glColor4f(endR, endG, endB, endA);
                GL11.glVertex2f(maxX, maxY);
                GL11.glVertex2f(maxX, minY);
            }
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        } else drawGradientRect(minX, minY, maxX, maxY, startColor, endColor);
    }
}