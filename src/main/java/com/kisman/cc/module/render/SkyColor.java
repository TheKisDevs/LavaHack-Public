package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkyColor extends Module {
    private final Setting color = new Setting("Color", this, "Color", new Colour(255, 0, 0));

    public SkyColor() {
        super("SkyColor", "You can change fog color", Category.RENDER);
        setmgr.rSetting(color);
    }

    @SubscribeEvent
    public void fogColor(EntityViewRenderEvent.FogColors event) {
        event.setRed(color.getColour().r);
        event.setGreen(color.getColour().g);
        event.setBlue(color.getColour().b);
    }

    @SubscribeEvent
    public void fog(EntityViewRenderEvent.FogDensity event) {
        event.setDensity(0);
        event.setCanceled(false);
    }
}
