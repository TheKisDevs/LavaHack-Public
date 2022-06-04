package com.kisman.cc.module.render.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.*;

public abstract class FramebufferShader extends Shader {
    public Minecraft mc;
    public static Framebuffer framebuffer;
    public boolean entityShadows;
    public int animationSpeed;

    public FramebufferShader(final String fragmentShader) {
        super(fragmentShader);
        this.mc = Minecraft.getMinecraft();
    }

    public void startDraw(final float partialTicks) {
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        (FramebufferShader.framebuffer = setupFrameBuffer(FramebufferShader.framebuffer)).framebufferClear();
        FramebufferShader.framebuffer.bindFramebuffer(true);
        entityShadows = mc.gameSettings.entityShadows;
        mc.gameSettings.entityShadows = false;
        mc.entityRenderer.setupCameraTransform(partialTicks, 0);
    }

    public void stopDraw() {
        mc.gameSettings.entityShadows = this.entityShadows;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        mc.getFramebuffer().bindFramebuffer(true);
        mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        startShader();
        mc.entityRenderer.setupOverlayRendering();
        drawFramebuffer(FramebufferShader.framebuffer);
        stopShader();
        mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public Framebuffer setupFrameBuffer(Framebuffer frameBuffer) {
        if (frameBuffer != null) frameBuffer.deleteFramebuffer();
        frameBuffer = new Framebuffer(this.mc.displayWidth, this.mc.displayHeight, true);
        return frameBuffer;
    }

    public void drawFramebuffer(final Framebuffer framebuffer) {
        final ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        GL11.glBindTexture(3553, framebuffer.framebufferTexture);
        GL11.glBegin(7);
        GL11.glTexCoord2d(Double.longBitsToDouble(Double.doubleToLongBits(1.7921236082576344E308) ^ 0x7FEFE69EB44D9FE1L), Double.longBitsToDouble(Double.doubleToLongBits(4.899133169559449) ^ 0x7FE398B65D9806D1L));
        GL11.glVertex2d(Double.longBitsToDouble(Double.doubleToLongBits(3.7307361562967813E307) ^ 0x7FCA9050299687CBL), Double.longBitsToDouble(Double.doubleToLongBits(7.56781900945177E307) ^ 0x7FDAF13C89C9BE29L));
        GL11.glTexCoord2d(Double.longBitsToDouble(Double.doubleToLongBits(1.0409447193540338E308) ^ 0x7FE28788CB57BFECL), Double.longBitsToDouble(Double.doubleToLongBits(4.140164300258766E307) ^ 0x7FCD7A9C5BA7C45BL));
        GL11.glVertex2d(Double.longBitsToDouble(Double.doubleToLongBits(1.3989301333159067E308) ^ 0x7FE8E6DB3F70C542L), scaledResolution.getScaledHeight());
        GL11.glTexCoord2d(Double.longBitsToDouble(Double.doubleToLongBits(52.314008345000495) ^ 0x7FBA28316CEA395FL), Double.longBitsToDouble(Double.doubleToLongBits(1.3534831910786353E308) ^ 0x7FE817C1C68E7C69L));
        GL11.glVertex2d(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        GL11.glTexCoord2d(Double.longBitsToDouble(Double.doubleToLongBits(4.557588341026122) ^ 0x7FE23AF870255A34L), Double.longBitsToDouble(Double.doubleToLongBits(23.337335758793085) ^ 0x7FC7565BA2E3C9A3L));
        GL11.glVertex2d(scaledResolution.getScaledWidth(), Double.longBitsToDouble(Double.doubleToLongBits(1.5123382114342209E308) ^ 0x7FEAEBA6CA1CFB74L));
        GL11.glEnd();
        GL20.glUseProgram(0);
    }
}

