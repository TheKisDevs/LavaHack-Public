package com.kisman.cc.hud.hudmodule.combat;

import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.kisman.cc.util.manager.Managers;

import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CrystalPerSecond extends HudModule {
    public CrystalPerSecond() {
        super("CrystalPerSecond", HudCategory.COMBAT, true);
        super.setX(1);
        super.setY(1);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        String text = "Crystal/Sec: " + TextFormatting.GRAY + Managers.instance.cpsManager.getCPS();
        CustomFontUtil.drawStringWithShadow(text, getX(), getY(), ColorUtils.astolfoColors(100, 100));

        setW(CustomFontUtil.getStringWidth(text));
        setH(CustomFontUtil.getFontHeight());
    }
}
