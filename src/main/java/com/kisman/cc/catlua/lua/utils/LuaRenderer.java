package com.kisman.cc.catlua.lua.utils;

import com.kisman.cc.util.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL41.glClearDepthf;

public class LuaRenderer implements Globals {
    private static LuaRenderer instance;

    LuaRenderer() {
    }

    public void text(String text, LuaVec2d vec2d, Color color) {
        CustomFontUtil.drawStringWithShadow(text, vec2d.x, vec2d.y, color.getRGB());
    }

    public void rect(LuaVec2d from, LuaVec2d to, Color color) {
        Render2DUtil.drawRect(from.x, from.y, to.x, to.y, color.getRGB());
    }

    public void line(LuaVec2d from, LuaVec2d to, Color color) {
        line(from, to, color, 0.5f);
    }

    public void line(LuaVec2d from, LuaVec2d to, Color color, float width) {
        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        GL11.glLineWidth(width);
        GL11.glBegin(1);
        GL11.glVertex2d(from.x, from.y);
        GL11.glVertex2d(to.x, to.y);
        GL11.glEnd();
    }

    public void initMask() {
        glClearDepthf(1.0f);
        glClear(GL_DEPTH_BUFFER_BIT);
        glColorMask(false, false, false, false);
        glDepthFunc(GL_LESS);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
    }

    public void useMask() {
        glColorMask(true, true, true, true);
        glDepthMask(true);
        glDepthFunc(GL_EQUAL);
    }

    public void disableMask() {
        glClearDepthf(1.0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glDepthMask(false);
    }

    public int width(String text) {
        return CustomFontUtil.getStringWidth(text);
    }

    public double windowWidth() {
        return sr.getScaledWidth_double();
    }

    public double windowHeight() {
        return sr.getScaledHeight_double();
    }

    //3D

    public void drawBoxESP(LuaBox box, Color c) {
        RenderUtil.drawBoxESP(box.toAABB(), c, 1, true, true, 100, 255);
    }

    public void drawBlockESP(BlockPos pos, Color c) {
        Colour color = new Colour(c);
        RenderUtil.drawBlockESP(pos, color.r1, color.g1, color.b1);
    }

    public void setup() {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void setup3D() {
        setup();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();
    }

    public void clean() {
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public void clean3D() {
        clean();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
    }

    public static LuaRenderer getDefault() {
        if(instance == null) instance = new LuaRenderer();
        return instance;
    }

    public void pushMatrix() {
        GL11.glPushMatrix();
    }

    public void popMatrix() {
        GL11.glPopMatrix();
    }
}
