package com.kisman.cc.hud.hudmodule.player;

import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerIp extends HudModule {
    public ServerIp() {
        super("ServerIP", "", HudCategory.PLAYER);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        ScaledResolution sr = new ScaledResolution(mc);
        String str = TextFormatting.WHITE + "Server: " + TextFormatting.GRAY + (mc.isSingleplayer() ? "SingePlayer" : mc.getCurrentServerData().serverIP.toLowerCase());
        CustomFontUtil.drawStringWithShadow(str, sr.getScaledWidth() - 1 - CustomFontUtil.getStringWidth(str), sr.getScaledHeight() - 5 - (CustomFontUtil.getFontHeight() * 3), -1);
    }
}
