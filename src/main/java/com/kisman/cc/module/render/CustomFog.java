package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CustomFog extends Module {
    private Setting red = new Setting("Red", this, 1, 0, 1, false);
    private Setting green = new Setting("Green", this, 0, 0, 1, false);
    private Setting blue = new Setting("Blue", this, 0, 0, 1, false);
    private Setting rainbow = new Setting("Rainbow", this, true);
    private Setting saturatuon = new Setting("Saturation", this, 1, 0,1, false);
    private Setting bringhtness = new Setting("Bringhtness", this, 1, 0, 1,  false);
    private Setting delay = new Setting("Delay", this, 100, 1, 2000, true);

    public CustomFog() {
        super("CustomFog", Category.RENDER);

        setmgr.rSetting(red);
        setmgr.rSetting(green);
        setmgr.rSetting(blue);
        setmgr.rSetting(rainbow);
        setmgr.rSetting(saturatuon);
        setmgr.rSetting(bringhtness);
        setmgr.rSetting(delay);
    }

    @SubscribeEvent
    public void onRenderSky(EntityViewRenderEvent.FogColors event) {
        if(rainbow.getValBoolean()) {
            event.setRed(ColorUtils.rainbowRGB(delay.getValInt(), saturatuon.getValFloat(), bringhtness.getValFloat()).getRed() / 255f);
            event.setGreen(ColorUtils.rainbowRGB(delay.getValInt(), saturatuon.getValFloat(), bringhtness.getValFloat()).getGreen() / 255f);
            event.setBlue(ColorUtils.rainbowRGB(delay.getValInt(), saturatuon.getValFloat(), bringhtness.getValFloat()).getBlue() / 255f);
        } else {
            event.setRed(red.getValFloat());
            event.setGreen(green.getValFloat());
            event.setBlue(blue.getValFloat());
        }
    }
}
