package com.kisman.cc.util.render.objects;

import com.kisman.cc.util.glow.ShaderShell;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

import java.awt.Color;

public class ObjectWithGlow extends AbstractObject {
    public float radius;
    public Vec4d vec;

    public ObjectWithGlow(Vec4d vectors, Color color, float radius) {
        super(vectors.toArray(), color, false, 1);
        this.vec = vectors;
        this.radius = radius;
    }

    public void render() {
        super.render();
        
        GL11.glPushMatrix();
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glDisable(GL11.GL_ALPHA_TEST);
    ShaderShell.ROUNDED_RECT.attach();
    ShaderShell.ROUNDED_RECT.set4F("color", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    ShaderShell.ROUNDED_RECT.set2F("resolution", Minecraft.getMinecraft().displayWidth,
            Minecraft.getMinecraft().displayHeight);
    ShaderShell.ROUNDED_RECT.set2F("center", (vec.getMinX() + (vec.getMaxX() - vec.getMinX()) / 2) * 2,
            (vec.getMinY() + (vec.getMaxY() - vec.getMinY()) / 2) * 2);
    ShaderShell.ROUNDED_RECT.set2F("dst", (vec.getMaxX() - vec.getMinX() - radius) * 2, (vec.getMaxX() - vec.getMinX() - radius) * 2);
    ShaderShell.ROUNDED_RECT.set1F("radius", radius);
    GL11.glBegin(GL11.GL_QUADS);
    vec.setupVectors();
    GL11.glEnd();
    ShaderShell.ROUNDED_RECT.detach();
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glPopMatrix();
    }
}