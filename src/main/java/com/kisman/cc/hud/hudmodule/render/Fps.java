package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Fps extends HudModule {
    public Fps() {
        super("Fps", HudCategory.RENDER, true);

        setX(1);
        setY(1);
    }

    public void update() {
        setW(CustomFontUtil.getStringWidth("Fps: " + Minecraft.getDebugFPS()));
        setH(CustomFontUtil.getFontHeight());
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            int color = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : -1;
            CustomFontUtil.drawStringWithShadow("Fps: " + TextFormatting.GRAY + Minecraft.getDebugFPS(), getX(), getY(), color);
        }
    }
}
