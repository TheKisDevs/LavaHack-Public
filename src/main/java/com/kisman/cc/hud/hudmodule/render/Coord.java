package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.module.client.HUD;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import com.kisman.cc.hud.hudmodule.*;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class Coord extends HudModule {
    int posX, nPosX, posY, nPosY, posZ, nPosZ;

    public Coord() {
        super("Coords", "coord", HudCategory.RENDER);
    }

    public void update() {
        if(mc.player != null && mc.world != null) {
            if(mc.player.dimension == 0) {
                posX = (int) mc.player.posX;
                posY = (int) mc.player.posY;
                posZ = (int) mc.player.posZ;
                nPosX = (int) (mc.player.posX / 8);
                nPosY = (int) (mc.player.posY / 8);
                nPosZ = (int) (mc.player.posZ / 8);
            } else if(mc.player.dimension == -1) {
                posX = (int) mc.player.posX;
                posY = (int) mc.player.posY;
                posZ = (int) mc.player.posZ;
                nPosX = (int) (mc.player.posX * 8);
                nPosY = (int) (mc.player.posY * 8);
                nPosZ = (int) (mc.player.posZ * 8);
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        ScaledResolution sr = new ScaledResolution(mc);

        if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            int color = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : -1;
            String coordString = "X: " + "(" + posX + ")[" + nPosX + "]" + " Y: " + "(" + posY + ")[" + nPosY + "]" + " Z: " + "(" + posZ + ")[" + nPosZ + "]";
            String rotationString = "Yaw: " + ((int) mc.player.rotationYaw >= 360 ? 0 : (int) mc.player.rotationYaw) + " Pitch: " + (int) mc.player.rotationPitch;

            if(HUD.instance.background.getValBoolean()) {
                double offset = HUD.instance.offsets.getValDouble() / 2 + 1;
                Render2DUtil.drawRect(0, sr.getScaledHeight() - CustomFontUtil.getFontHeight() - 2 - offset, CustomFontUtil.getStringWidth(coordString) + 3 + offset, sr.getScaledHeight() + offset, ColorUtils.injectAlpha(Color.BLACK, HUD.instance.bgAlpha.getValInt()).getRGB());
                Render2DUtil.drawRect(0, sr.getScaledHeight() - CustomFontUtil.getFontHeight() * 2 - 4 - offset, CustomFontUtil.getStringWidth(rotationString) + 3 + offset, sr.getScaledHeight() - CustomFontUtil.getFontHeight() - 3 + offset, ColorUtils.injectAlpha(Color.BLACK, HUD.instance.bgAlpha.getValInt()).getRGB());
            }

            if(HUD.instance.glow.getValBoolean()) {
                double offset = HUD.instance.glowOffset.getValDouble() + HUD.instance.offsets.getValDouble() / 2;
                Render2DUtil.drawGlow(0, sr.getScaledHeight() - CustomFontUtil.getFontHeight() - 2 - offset, CustomFontUtil.getStringWidth(coordString) + 2, sr.getScaledHeight() + offset, ColorUtils.injectAlpha(color, HUD.instance.glowAlpha.getValInt()).getRGB());
                Render2DUtil.drawGlow(0, sr.getScaledHeight() - CustomFontUtil.getFontHeight() * 2 - 4 - offset, CustomFontUtil.getStringWidth(rotationString) + 2, sr.getScaledHeight() - CustomFontUtil.getFontHeight() - 3 + offset, ColorUtils.injectAlpha(color, HUD.instance.glowAlpha.getValInt()).getRGB());
            }

            CustomFontUtil.drawStringWithShadow(coordString, 1, sr.getScaledHeight() - CustomFontUtil.getFontHeight() - 1, color);
            CustomFontUtil.drawStringWithShadow(rotationString, 1, sr.getScaledHeight() - (CustomFontUtil.getFontHeight() * 2) - HUD.instance.offsets.getValInt(), color);
        }
    }
}
