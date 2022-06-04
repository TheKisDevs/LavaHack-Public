package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;

public class Animation extends Module {
    public static Animation instance;

    public Setting speed = new Setting("Speed", this, 13, 1, 20, Slider.NumberType.INTEGER);

    public Animation() {
        super("Animation", Category.RENDER);

        instance = this;

        setmgr.rSetting(speed);
    }
}
