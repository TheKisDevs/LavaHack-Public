package com.kisman.cc.util.shaders.hotbar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.*;

public abstract class FramebufferShaderTex extends ShaderTex {
    public Minecraft mc = Minecraft.getMinecraft();
    private Framebuffer framebuffer;
    protected float red;
    protected float green;
    protected float blue;
    protected float alpha;
    protected float radius;
    protected float quality;
    private boolean entityShadows;
    
    public FramebufferShaderTex(final String fragmentShader) {
        super(fragmentShader);
        this.alpha = 1.0f;
        this.radius = 2.0f;
        this.quality = 1.0f;
    }
    
    public void startDraw(final float partialTicks) {
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        (this.framebuffer = this.setupFrameBuffer(this.framebuffer)).framebufferClear();
        this.framebuffer.bindFramebuffer(true);
        this.entityShadows = mc.gameSettings.entityShadows;
        mc.gameSettings.entityShadows = false;
        mc.entityRenderer.setupCameraTransform(partialTicks, 0);
    }
    
    public void stopDraw(final Color color, final float radius, final float quality) {
        mc.gameSettings.entityShadows = this.entityShadows;
        this.framebuffer.unbindFramebuffer();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        mc.getFramebuffer().bindFramebuffer(true);
        this.red = color.getRed() / 255.0f;
        this.green = color.getGreen() / 255.0f;
        this.blue = color.getBlue() / 255.0f;
        this.alpha = color.getAlpha() / 255.0f;
        this.radius = radius;
        this.quality = quality;
        mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        this.startShader();
        mc.entityRenderer.setupOverlayRendering();
        this.drawFramebuffer(this.framebuffer);
        this.stopShader();
        mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }
    
    public Framebuffer setupFrameBuffer(Framebuffer frameBuffer) {
        if (frameBuffer == null) {
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        if (frameBuffer.framebufferWidth !=mc.displayWidth || frameBuffer.framebufferHeight != mc.displayHeight) {
            frameBuffer.deleteFramebuffer();
            frameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return frameBuffer;
    }
    
    public void drawFramebuffer(final Framebuffer framebuffer) {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        GL11.glBindTexture(3553, framebuffer.framebufferTexture);
        GL11.glBegin(7);
        GL11.glTexCoord2d(0.0, 1.0);
        GL11.glVertex2d(0.0, 0.0);
        GL11.glTexCoord2d(0.0, 0.0);
        GL11.glVertex2d(0.0, (double)scaledResolution.getScaledHeight());
        GL11.glTexCoord2d(1.0, 0.0);
        GL11.glVertex2d((double)scaledResolution.getScaledWidth(), (double)scaledResolution.getScaledHeight());
        GL11.glTexCoord2d(1.0, 1.0);
        GL11.glVertex2d((double)scaledResolution.getScaledWidth(), 0.0);
        GL11.glEnd();
        GL20.glUseProgram(0);
    }
    
    public Framebuffer getFramebuffer() {
        return this.framebuffer;
    }
}
