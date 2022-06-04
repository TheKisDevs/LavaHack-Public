package com.kisman.cc.module.movement;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

public class LongJump extends Module {
    private final Setting boost = new Setting("Boost", this, 1, 1, 2.5f, false);

    public LongJump() {
        super("LongJump", Category.MOVEMENT);

        setmgr.rSetting(boost);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        if (mc.player.onGround) {
            if (mc.player.hurtTime <= 6) {
                mc.player.motionX *= 1.200054132 * boost.getValDouble();
                mc.player.motionZ *= 1.200054132 * boost.getValDouble();
                mc.player.motionY *= 1.500054132 * boost.getValDouble();
            }
            if (mc.player.hurtTime <= 5) {
                mc.player.motionX *= 1.203150645 * boost.getValDouble();
                mc.player.motionZ *= 1.203150645 * boost.getValDouble();
                mc.player.motionY *= 1.500054132 * boost.getValDouble();
            }
        }
        if (mc.player.hurtTime <= 10) {
            mc.player.motionX *= 1.2435001 * boost.getValDouble();
            mc.player.motionZ *= 1.2205001 * boost.getValDouble();
            mc.player.jumpMovementFactor = 0.0365f;
        }
    }
}
