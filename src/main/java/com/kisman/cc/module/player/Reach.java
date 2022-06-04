package com.kisman.cc.module.player;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

public class Reach extends Module {
    public final Setting distance = new Setting("Distance", this, 5, 0, 10, true);

    public static Reach instance;

    public Reach() {
        super("Reach", Category.PLAYER);

        instance = this;

        setmgr.rSetting(distance);
    }
}
