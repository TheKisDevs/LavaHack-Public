package com.kisman.cc.gui.halq.component.components.sub;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.settings.EventSettingChange;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.component.Component;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.kisman.cc.util.Render2DUtil.drawGradientRect;
import static com.kisman.cc.util.Render2DUtil.drawLeftGradientRect;

public class ColorButton extends Component {
    private final Setting setting;
    private Colour color;
    private int x, y, offset, pickerWidth, height, count;
    public boolean open = false;
    private boolean baseHover, hueHover, alphaHover, pickingBase, pickingHue, pickingAlpha;
    private int width = HalqGui.width;
    private int layer;

    public ColorButton(Setting setting, int x, int y, int offset, int count) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.color = setting.getColour();
        this.pickerWidth = width - HalqGui.height;
        this.count = count;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(HalqGui.shadowCheckBox) {
            Render2DUtil.drawRectWH(x, y + offset, width, getHeight(), HalqGui.backgroundColor.getRGB());
            Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x, y + offset}, new double[] {x + width / 2, y + offset}, new double[] {x + width / 2, y + offset + HalqGui.height}, new double[] {x, y + offset + HalqGui.height}), color.getColor(), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1)));
            Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x + width / 2, y + offset}, new double[] {x + width, y + offset}, new double[] {x + width, y + offset + HalqGui.height}, new double[] {x + width / 2, y + offset + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1), color.getColor()));
        } else Render2DUtil.drawRectWH(x, y + offset, width, getHeight(), color.getRGB());

        HalqGui.drawString(setting.getName(), x, y + offset, width, HalqGui.height);

        if(open) {
            int offsetY = HalqGui.height + 5;
            drawPickerBase(x + 2, y + offset + offsetY, pickerWidth, pickerWidth, color.r1, color.g1, color.b1, color.a1, mouseX, mouseY);
            offsetY += pickerWidth + 5;
            drawHueSlider(x + 2, y + offset + offsetY, pickerWidth, HalqGui.height - 3, color.getHue(), mouseX, mouseY);
            offsetY += HalqGui.height - 3 + 5;
            drawAlphaSlider(x + 2, y + offset + offsetY, pickerWidth, HalqGui.height - 3, color.r1, color.g1, color.b1, color.a1, mouseX, mouseY);
            height = offsetY + HalqGui.height - 3 + 5;

            updateValue(mouseX, mouseY, x + 2, y + offset + HalqGui.height + 5);

            {
                final int cursorX = (int) (x + 2 + color.RGBtoHSB()[1]*pickerWidth);
                final int cursorY = (int) ((y + offset + HalqGui.height + 5 + pickerWidth) - color.RGBtoHSB()[2]*pickerWidth);
                Gui.drawRect(cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2, -1);
            }
        }
    }

    private void updateValue(int mouseX, int mouseY, int x, int y) {
        if (pickingBase) {
            float restrictedX = (float) Math.min(Math.max(x, mouseX), x + pickerWidth);
            float restrictedY = (float) Math.min(Math.max(y, mouseY), y + pickerWidth);
            color.setSaturation((restrictedX - (float) x) / pickerWidth);
            this.color.setBrightness(1 - (restrictedY - (float) y) / pickerWidth);
        }
        if (pickingHue) {
            float restrictedX = (float) Math.min(Math.max(x, mouseX), x + pickerWidth);
            this.color.setHue((restrictedX - (float) x) / pickerWidth);
            System.out.println((restrictedX - (float) x) / pickerWidth);
        }
        if (pickingAlpha) {
            float restrictedX = (float) Math.min(Math.max(x, mouseX), x + pickerWidth);
            this.color.setAlpha(1 - (restrictedX - (float) x) / pickerWidth);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(button == 0) {
            if(isMouseOnButton(mouseX, mouseY)) open = !open;
            pickingBase = baseHover;
            pickingHue = hueHover;
            pickingAlpha = alphaHover;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        pickingBase = pickingAlpha = pickingHue = false;
        Kisman.EVENT_BUS.post(new EventSettingChange.ColorSetting(setting));

    }

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public int getHeight() {
        return HalqGui.height + (open ? height : 0);
    }

    @Override
    public boolean visible() {
        return setting.isVisible();
    }

    public void setCount(int count) {this.count = count;}
    public int getCount() {return count;}
    public void setWidth(int width) {this.width = width;}
    public void setX(int x) {this.x = x;}
    public int getX() {return x;}
    public void setLayer(int layer) {this.layer = layer;}
    public int getLayer() {return layer;}

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + width && y > this.y + offset && y < this.y + offset + HalqGui.height;
    }

    private void drawHueSlider(int x, int y, int width, int height, float hue, int mouseX, int mouseY) {
        hueHover = mouseX > x && mouseX < x + width && mouseY > y && mouseY <  y + height;
        int step = 0;
        if (height > width) {
            Gui.drawRect(x, y, x + width, y + 4, -1);
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

    public void drawAlphaSlider(int x, int y, int width, int height, float red, float green, float blue, float alpha, int mouseX, int mouseY) {
        alphaHover = mouseX > x && mouseX < x + width && mouseY > y && mouseY <  y + height;
        boolean left = true;
        int checkerBoardSquareSize = height / 2;

        for (int squareIndex = -checkerBoardSquareSize; squareIndex < width; squareIndex += checkerBoardSquareSize) {
            if (!left) {
                Render2DUtil.drawRect(x + squareIndex, y, x + squareIndex + checkerBoardSquareSize, y + height, 0xFFFFFFFF);
                Render2DUtil.drawRect(x + squareIndex, y + checkerBoardSquareSize, x + squareIndex + checkerBoardSquareSize, y + height, 0xFF909090);

                if (squareIndex < width - checkerBoardSquareSize) {
                    int minX = x + squareIndex + checkerBoardSquareSize;
                    int maxX = Math.min(x + width, x + squareIndex + checkerBoardSquareSize * 2);
                    Render2DUtil.drawRect(minX, y, maxX, y + height, 0xFF909090);
                    Render2DUtil.drawRect(minX, y + checkerBoardSquareSize, maxX, y + height, 0xFFFFFFFF);
                }
            }

            left = !left;
        }

        drawLeftGradientRect(x, y, x + width, y + height, new Color(red, green, blue, 1).getRGB(), 0);
        int sliderMinX = (int) (x + width - (width * alpha));
        Render2DUtil.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
    }

    private void drawPickerBase(int pickerX, int pickerY, int pickerWidth, int pickerHeight, float red, float green, float blue, float alpha, int mouseX, int mouseY) {
        baseHover = mouseX > pickerX && mouseX < pickerX + pickerWidth && mouseY > pickerY && mouseY <  pickerY + pickerHeight;
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

            GL11.glPushMatrix();
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
            GL11.glPopMatrix();
        } else drawGradientRect(minX, minY, maxX, maxY, startColor, endColor);
    }
}
