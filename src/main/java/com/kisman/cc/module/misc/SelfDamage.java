package com.kisman.cc.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

public class SelfDamage extends Module {
    private final Setting jump;
    private final Setting timer;

    private int jumpCount;

    public SelfDamage() {
        super("SelfDamage", "SelfDamage", Category.MISC);

        Kisman.instance.settingsManager.rSetting(this.jump = new Setting("Jumps", this, 3, 3, 50, true));
        Kisman.instance.settingsManager.rSetting(this.timer = new Setting("JumpTimer", this, 3, 1, 1000, true));
    }

    public void onEnable() {
        this.jumpCount = 0;
    }

    public void onDisable() {
        mc.timer.tickLength = 1;
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        if(jumpCount < jump.getValDouble()) {
            mc.timer.tickLength = (float) timer.getValDouble();
            mc.player.onGround = false;
        }

        if(mc.player.onGround) {
            if(jumpCount < jump.getValDouble()) {
                mc.player.jump();
                jumpCount++;
            } else mc.timer.tickLength = 1;
        }
    }
}
