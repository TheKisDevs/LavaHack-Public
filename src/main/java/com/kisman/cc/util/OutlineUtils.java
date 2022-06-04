package com.kisman.cc.util;

import java.awt.Color;

import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.*;

public class OutlineUtils{
    public static void renderOne(float f) {
        OutlineUtils.checkSetupFBO();
        GL11.glPushAttrib((int)1048575);
        GL11.glDisable((int)3008);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2896);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glLineWidth((float)f);
        GL11.glEnable((int)2848);
        GL11.glEnable((int)2960);
        GL11.glClear((int)1024);
        GL11.glClearStencil((int)15);
        GL11.glStencilFunc((int)512, (int)1, (int)15);
        GL11.glStencilOp((int)7681, (int)7681, (int)7681);
        GL11.glPolygonMode((int)1032, (int)6913);
    }

    public static void renderTwo() {
        GL11.glStencilFunc((int)512, (int)0, (int)15);
        GL11.glStencilOp((int)7681, (int)7681, (int)7681);
        GL11.glPolygonMode((int)1032, (int)6914);
    }

    public static void renderThree() {
        GL11.glStencilFunc((int)514, (int)1, (int)15);
        GL11.glStencilOp((int)7680, (int)7680, (int)7680);
        GL11.glPolygonMode((int)1032, (int)6913);
    }

    public static void renderFour() {
        OutlineUtils.setColor(new Color(255, 255, 255));
        GL11.glDepthMask((boolean)false);
        GL11.glDisable((int)2929);
        GL11.glEnable((int)10754);
        GL11.glPolygonOffset((float)Float.intBitsToFloat(Float.floatToIntBits(22.41226f) ^ 0x7E334C4F), (float)Float.intBitsToFloat(Float.floatToIntBits(-1.3566593E-5f) ^ 0x7E97B813));
        OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)Float.intBitsToFloat(Float.floatToIntBits(0.01000088f) ^ 0x7F53DABB), (float)Float.intBitsToFloat(Float.floatToIntBits(0.011808969f) ^ 0x7F317A68));
    }

    public static void renderFive() {
        GL11.glPolygonOffset((float)Float.intBitsToFloat(Float.floatToIntBits(12.714713f) ^ 0x7ECB6F77), (float)Float.intBitsToFloat(Float.floatToIntBits(1.3271895E-5f) ^ 0x7EAA8E5B));
        GL11.glDisable((int)10754);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)2960);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glEnable((int)3042);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)3008);
        GL11.glPopAttrib();
    }

    public static void setColor(Color c) {
        GL11.glColor4d((double)((float)c.getRed() / Float.intBitsToFloat(Float.floatToIntBits(0.0098663345f) ^ 0x7F5EA668)), (double)((float)c.getGreen() / Float.intBitsToFloat(Float.floatToIntBits(1.0011557f) ^ 0x7CFF25DF)), (double)((float)c.getBlue() / Float.intBitsToFloat(Float.floatToIntBits(0.114814304f) ^ 0x7E9423C3)), (double)((float)c.getAlpha() / Float.intBitsToFloat(Float.floatToIntBits(0.09551593f) ^ 0x7EBC9DDB)));
    }

    public static void setColor(Setting color) {
        color.getColour().glColor();
    }

    public static void checkSetupFBO() {
        Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
        if (fbo != null && fbo.depthBuffer > -1) {
            OutlineUtils.setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    public static void setupFBO(Framebuffer framebuffer) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT((int)framebuffer.depthBuffer);
        int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT((int)36161, (int)stencil_depth_buffer_ID);
        EXTFramebufferObject.glRenderbufferStorageEXT((int)36161, (int)34041, (int)Minecraft.getMinecraft().displayWidth, (int)Minecraft.getMinecraft().displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT((int)36160, (int)36128, (int)36161, (int)stencil_depth_buffer_ID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT((int)36160, (int)36096, (int)36161, (int)stencil_depth_buffer_ID);
    }
}

