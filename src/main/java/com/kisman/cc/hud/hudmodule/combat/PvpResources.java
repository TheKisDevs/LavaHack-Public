package com.kisman.cc.hud.hudmodule.combat;

import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class PvpResources extends HudModule {
    public static PvpResources instance;

    public PvpResources() {
        super("PvpResources", "PvpResources", HudCategory.COMBAT, true);
        instance = this;

        setX(100);
        setY(100);
        setW(20);
        setH(20 * 4);
    }

    public static int getItemCount(Item item) {
        return mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == item).mapToInt(ItemStack::getCount).sum();
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        final int x = (int) getX();
        final int y = (int) getY();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        int offset = 0;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(426), 1), x , y + offset);
        renderItemOverlayIntoGUI(x, y + offset , "" + getItemCount(Item.getItemById(426)));
        offset += 20;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(384), 1), x, y + offset);
        renderItemOverlayIntoGUI(x , y +  offset, "" + getItemCount(Item.getItemById(384)));
        offset += 20;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(322), 1), x, y + offset);
        renderItemOverlayIntoGUI(x, y + offset, "" + getItemCount(Item.getItemById(322)));
        offset += 20;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(449), 1), x, y + offset);
        renderItemOverlayIntoGUI(x , y + offset, "" + getItemCount(Item.getItemById(449)));

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public void renderItemOverlayIntoGUI(int xPosition, int yPosition, String s) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        CustomFontUtil.drawStringWithShadow(s, (float) (xPosition + 19 - 2 - CustomFontUtil.getStringWidth(s)), (float) (yPosition + 6 + 3), new Color(255, 255, 255, 255).hashCode());
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
    }
}