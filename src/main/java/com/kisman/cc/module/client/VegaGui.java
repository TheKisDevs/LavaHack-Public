package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import net.minecraft.util.ResourceLocation;

public class VegaGui extends Module {
    public static VegaGui instance;

    public Setting test = new Setting("Test Gui Update", this, false);

    public VegaGui() {
        super("VegaGui", "gui", Category.CLIENT);

        instance = this;

        setmgr.rSetting(test);
    }

    public boolean isBeta() {return true;}

    public void onEnable() {
        mc.displayGuiScreen(Kisman.instance.gui);
        this.setToggled(false);

        if(Config.instance.guiBlur.getValBoolean()) mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
    }
}
