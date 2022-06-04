package com.kisman.cc.module.movement;

import com.kisman.cc.module.*;

public class NoJumpDelay extends Module {
    public NoJumpDelay() {
        super("NoJumpDelay", "disable jump", Category.MOVEMENT);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        mc.player.jumpTicks = mc.player.nextStepDistance = 0;
    }
}
