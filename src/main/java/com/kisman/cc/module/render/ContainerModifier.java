package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

public class ContainerModifier extends Module {
    public Setting containerShadow = new Setting("Container Shadow", this, false);
    public Setting itemESP = new Setting("Item ESP", this, false);

    public static ContainerModifier instance;

    public ContainerModifier() {
        super("ContainerModifier", Category.RENDER);

        instance = this;

        setmgr.rSetting(containerShadow);
        setmgr.rSetting(itemESP);
    }
}
