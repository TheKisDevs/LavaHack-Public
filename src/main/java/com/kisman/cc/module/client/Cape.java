package com.kisman.cc.module.client;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

import java.util.Arrays;

public class Cape extends Module {
    public static Cape instance;

    public Setting mode = new Setting("Cape Mode", this, "Gif", Arrays.asList("Gif", "Xulu+", "GentleManMC", "Kuro", "Putin", "Gradient"));

    public Cape() {
        super("Cape", "Custom cape", Category.CLIENT);

        instance = this;

        setmgr.rSetting(mode);
    }

    public void update() {
        super.setDisplayInfo("[" + mode.getValString() + "]");
    }
}
