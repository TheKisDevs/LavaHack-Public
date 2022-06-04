package com.kisman.cc.module.movement;

import com.kisman.cc.module.*;
import com.kisman.cc.util.PlayerUtil;

public class FastSwim extends Module {
    private boolean isSprint = false;
    public FastSwim() {
        super("FastSwim", "swim", Category.MOVEMENT);
    }

    public void onDisable() {
        if(mc.player != null && isSprint && mc.player.isSprinting()) mc.player.setSprinting(false);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        if((mc.player.isInLava() || mc.player.isInWater()) && PlayerUtil.isMoving(mc.player)) {
            mc.player.setSprinting(true);
            isSprint = true;
            if(mc.gameSettings.keyBindJump.isKeyDown()) mc.player.motionY = 0.098;
        }
    }
}
