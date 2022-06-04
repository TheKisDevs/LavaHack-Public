package com.kisman.cc.gui.csgo.components.visualpreview;

import com.kisman.cc.module.client.Config;
import com.kisman.cc.gui.csgo.Window;
import com.kisman.cc.util.*;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

import com.kisman.cc.gui.csgo.IRenderer;

public class VisualPreviewWindow implements Globals {
    private String title;
    public int x, y, width, height;
    private int headerHeight;

    private boolean beingDragged;
    private int dragX, dragY;
    private int oldMouseX, oldMouseY;

    public VisualPreviewWindow(String title, int x, int y, int width, int height) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void drawScreen(IRenderer renderer, int mouseX, int mouseY) {
        int fontHeight = renderer.getStringHeight(title);
        int headerFontOffset = fontHeight / 4;

        headerHeight = headerFontOffset * 2 + fontHeight;

        renderer.drawRect(x, y, width, height, Window.BACKGROUND);
        renderer.drawRect(x, y, width, headerHeight, Window.SECONDARY_FOREGROUND);

        if(Config.instance.guiGlow.getValBoolean()) Render2DUtil.drawRoundedRect(x / 2, y / 2, (x + width) / 2, (y + headerHeight) / 2, Window.SECONDARY_FOREGROUND, Config.instance.glowBoxSize.getValDouble());

        renderer.drawString(x + width / 2 - renderer.getStringWidth(title) / 2, y + headerFontOffset, title, Config.instance.guiAstolfo.getValBoolean() ? renderer.astolfoColorToObj() : Window.FOREGROUND);

        drawEntityOnScreen((x + width / 2) / 2, (y + height - 10) / 2, 30, (float)((x + width / 2) / 2) - this.oldMouseX, (float)((y + height - 10) / 2) - this.oldMouseY, mc.player);

        oldMouseX = mouseX;
        oldMouseY = mouseY;
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
        ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public void setTitle(String title) {this.title = title;}

    public void mousePressed(int button, int x, int y) {
        if (button == 0) {
            if (x >= this.x && y >= this.y && x <= this.x + this.width && y <= this.y + this.headerHeight) {
                beingDragged = true;

                dragX = this.x - x;
                dragY = this.y - y;
            }
        }
    }

    private void drag(int mouseX, int mouseY) {
        if (beingDragged) {
            this.x = mouseX + dragX;
            this.y = mouseY + dragY;
        }
    }

    public void mouseReleased(int button, int x, int y) {
        if (button == 0) {
            beingDragged = false;
        }
    }

    public void mouseMoved(int x, int y) {
        drag(x, y);
    }
}
