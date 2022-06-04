package com.kisman.cc.util.render.objects;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.kisman.cc.util.RenderUtil.glColor;
import static org.lwjgl.opengl.GL11.*;

public class AbstractGradient extends Gui {
    public Vec4d vec;
    public Color start, end;
    public boolean vertical;
    public float width;

    public AbstractGradient(Vec4d vec, Color start, Color end, boolean vertical) {
        this.vec = vec;
        this.vertical = vertical;
        this.start = start;
        this.end = end;
    }

    public AbstractGradient(Vec4d vec, Color start, Color end) {
        this(vec, start, end, false);
    }

    public void render() {
        GL11.glPushMatrix();
        if(vertical) {
            float f = (float)(start.getRGB()  >> 24 & 255) / 255.0F;
            float f1 = (float)(start.getRGB()  >> 16 & 255) / 255.0F;
            float f2 = (float)(start.getRGB()  >> 8 & 255) / 255.0F;
            float f3 = (float)(start.getRGB()  & 255) / 255.0F;
            float f4 = (float)(end.getRGB()  >> 24 & 255) / 255.0F;
            float f5 = (float)(end.getRGB()  >> 16 & 255) / 255.0F;
            float f6 = (float)(end.getRGB()  >> 8 & 255) / 255.0F;
            float f7 = (float)(end.getRGB()  & 255) / 255.0F;
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.shadeModel(7425);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(vec.x2, vec.y2,  zLevel).color(f1, f2, f3, f).endVertex();
            bufferbuilder.pos(vec.x1, vec.y1, zLevel).color(f1, f2, f3, f).endVertex();
            bufferbuilder.pos(vec.x4, vec.y4, zLevel).color(f5, f6, f7, f4).endVertex();
            bufferbuilder.pos(vec.x3, vec.y3, zLevel).color(f5, f6, f7, f4).endVertex();
            tessellator.draw();
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
        } else {
            final float startA = (start.getRGB() >> 24 & 0xFF) / 255.0f;
            final float startR = (start.getRGB() >> 16 & 0xFF) / 255.0f;
            final float startG= (start.getRGB() >> 8 & 0xFF) / 255.0f;
            final float startB = (start.getRGB() & 0xFF) / 255.0f;

            final float endA = (end.getRGB() >> 24 & 0xFF) / 255.0f;
            final float endR = (end.getRGB() >> 16 & 0xFF) / 255.0f;
            final float endG = (end.getRGB() >> 8 & 0xFF) / 255.0f;
            final float endB = (end.getRGB() & 0xFF) / 255.0f;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL_SMOOTH);
            GL11.glBegin(GL11.GL_POLYGON);
            {
                GL11.glColor4f(startR, startG, startB, startA);
                GL11.glVertex2d(vec.x1, vec.y1);
                GL11.glVertex2d(vec.x4, vec.y4);
                GL11.glColor4f(endR, endG, endB, endA);
                GL11.glVertex2d(vec.x3, vec.y3);
                GL11.glVertex2d(vec.x2, vec.y2);
            }
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glPopMatrix();
    }

    public static void drawGradientRect(final double startX, final double startY, final double endX, final double endY, final boolean sideways, final boolean reversed, final int startColor, final int endColor)
    {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL_SMOOTH);

        glBegin(GL_QUADS);

        glColor(startColor);

        if (sideways) {
            if (reversed) {
                glVertex2d(endX, endY);
                glVertex2d(endX, startY);
                glColor(endColor);
                glVertex2d(startX, startY);
                glVertex2d(startX, endY);
            } else {
                glVertex2d(startX, startY);
                glVertex2d(startX, endY);
                glColor(endColor);
                glVertex2d(endX, endY);
                glVertex2d(endX, startY);
            }
        } else {
            if (reversed) {
                glVertex2d(endX, endY);
                glColor(endColor);
                glVertex2d(endX, startY);
                glVertex2d(startX, startY);
                glColor(startColor);
                glVertex2d(startX, endY);
            } else {
                glVertex2d(startX, startY);
                glColor(endColor);
                glVertex2d(startX, endY);
                glVertex2d(endX, endY);
                glColor(startColor);
                glVertex2d(endX, startY);
            }
        }

        glEnd();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(GL_FLAT);
    }
}
