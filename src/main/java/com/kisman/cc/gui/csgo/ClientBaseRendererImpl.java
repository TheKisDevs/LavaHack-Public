package com.kisman.cc.gui.csgo;

import com.kisman.cc.module.client.Config;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL41.glClearDepthf;

public class ClientBaseRendererImpl implements IRenderer {
    @Override public void drawRect(double x, double y, double w, double h, Color c) {Render2DUtil.drawRect(GL_QUADS, (int) x / 2, (int) y / 2, (int) x / 2 + (int)  w / 2, (int) y / 2 + (int) h / 2, c.getRGB());}

@Override
public  void drawCheckMark(float x, float y, int width, int color) {
    float f = (color >> 24 & 255) / 255.0f;
    float f1 = (color >> 16 & 255) / 255.0f;
    float f2 = (color >> 8 & 255) / 255.0f;
    float f3 = (color & 255) / 255.0f;
    GL11.glPushMatrix();
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glDisable(3553);
    GL11.glEnable(2848);
    GL11.glBlendFunc(770, 771);
    GL11.glLineWidth(3f);
    GL11.glBegin(3);
    GL11.glColor4f(0, 0, 0, 1.f);
    GL11.glVertex2d(x + width - 6.25, y + 2.75f);
    GL11.glVertex2d(x + width - 11.5, y + 10.25f);
    GL11.glVertex2d(x + width - 13.75f, y + 7.75f);
    GL11.glEnd();
    GL11.glLineWidth(1.5f);
    GL11.glBegin(3);
    GL11.glColor4f(f1, f2, f3, f);
    GL11.glVertex2d(x + width - 6.5, y + 3);
    GL11.glVertex2d(x + width - 11.5, y + 10);
    GL11.glVertex2d(x + width - 13.5, y + 8);
    GL11.glEnd();
    GL11.glEnable(3553);
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glPopMatrix();
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
}

    @Override
    public void drawOutline(double x, double y, double w, double h, float lineWidth, Color c) {
        if(!Config.instance.guiOutline.getValBoolean()) return;
        glLineWidth(lineWidth);
        Render2DUtil.drawRect(GL_LINE_LOOP, (int) x / 2, (int) y / 2, (int) x / 2 + (int) w / 2, (int) y / 2 + (int) h / 2, ColorUtils.getColor(c));
    }

    @Override public void setColor(Color c) {ColorUtils.glColor(c);}
    @Override public void drawString(int x, int y, String text, Color color) {CustomFontUtil.drawString(text, x / 2f, y / 2f, ColorUtils.getColor(color), true);}
    @Override public int getStringWidth(String str) {return CustomFontUtil.getStringWidth(str) * 2;}
    @Override public int getStringHeight(String str) {return CustomFontUtil.getFontHeight(true) * 2;}
    @Override public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {}

    @Override
    public void initMask() {
        glClearDepthf(1.0f);
        glClear(GL_DEPTH_BUFFER_BIT);
        glColorMask(false, false, false, false);
        glDepthFunc(GL_LESS);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
    }

    @Override
    public void useMask() {
        glColorMask(true, true, true, true);
        glDepthMask(true);
        glDepthFunc(GL_EQUAL);
    }

    @Override
    public void disableMask() {
        glClearDepthf(1.0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glDepthMask(false);
    }

    @Override public int astolfoColor() {return ColorUtils.astolfoColors(100, 100);}
    @Override public Color astolfoColorToObj() {return ColorUtils.astolfoColorsToColorObj(100, 100);}
}