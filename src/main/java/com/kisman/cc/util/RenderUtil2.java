package com.kisman.cc.util;

import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;

public class RenderUtil2 implements Globals {
    public static void drawNametag(String text, AxisAlignedBB interpolated, double scale, int color, boolean rectangle) {
        double x = (interpolated.minX + interpolated.maxX) / 2.0;
        double y = (interpolated.minY + interpolated.maxY) / 2.0;
        double z = (interpolated.minZ + interpolated.maxZ) / 2.0;

        drawNametag(text, x, y, z, scale, color, rectangle);
    }

    public static void drawFadeESP(Entity entity, Colour  color, Colour color2) {
        glPushMatrix ();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBegin(GL_LINE_STRIP);
        for (int i = 0; i <= 360; ++i){
            color.glColor();
            glVertex3d(entity.posX, entity.posY, entity.posZ);
            color2.getColor();
            glVertex3d(entity.posX - sin(toRadians(i)), entity.posY, entity.posZ + cos(toRadians(i)));
        }
        glEnd();
        glEnable(GL_TEXTURE_2D);
        glEnable (GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glColor4f (1,1,1,1);
        glPopMatrix ();
    }

    public static Entity getEntity() {
        return mc.getRenderViewEntity() == null ? mc.player : mc.getRenderViewEntity();
    }

    public static void drawNametag(String text, double x, double y, double z, double scale, int color, boolean rectangle) {
        //double dist = MathHelper.sqrt(x * x + y * y + z * z);
        double dist = getEntity().getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);

        int textWidth = CustomFontUtil.getStringWidth(text) / 2;
        double scaling = 0.0018 + scale * dist;

        if (dist <= 8.0) scaling = 0.0245;

        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0F, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate(x, y + 0.4f, z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        float xRot = mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f;
        GlStateManager.rotate(mc.getRenderManager().playerViewX, xRot, 0.0f, 0.0f);
        GlStateManager.scale(-scaling, -scaling, scaling);
        GlStateManager.disableDepth();

        if (rectangle)
        {
            GlStateManager.enableBlend();
            prepare( (float)( -textWidth - 1), (float) -CustomFontUtil.getFontHeight(), (float) (textWidth + 2), 1.0F, 1.8F, 0x55000400, 0x33000000);
            GlStateManager.disableBlend();
        }

        GlStateManager.enableBlend();
        CustomFontUtil.drawStringWithShadow(text, -textWidth, -(mc.fontRenderer.FONT_HEIGHT - 1), color);
        GlStateManager.disableBlend();

        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0F, 1500000.0f);
        GlStateManager.popMatrix();
    }


    public static void prepare(float x, float y, float x1, float y1, int color, int color1) {
        startRender();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);
        ColorUtils.glColor(new Color(color));
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        ColorUtils.glColor(new Color(color));
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        endRender();
    }

    public static void startRender() {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_FASTEST);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static void endRender() {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static void prepare(float x, float y, float x1, float y1, float lineWidth, int color, int color1, int color2) {
        startRender();
        prepare(x, y, x1, y1, color2, color1);
        ColorUtils.glColor(new Color(color));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        endRender();
    }

    public static void prepare(float x, float y, float x1, float y1, float lineWidth, int color, int color1) {
        startRender();
        prepare(x, y, x1, y1, color, color);
        ColorUtils.glColor(new Color(color));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        endRender();
    }
}
