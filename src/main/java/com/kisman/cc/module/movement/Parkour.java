package com.kisman.cc.module.movement;

import com.kisman.cc.module.*;

public class Parkour extends Module {
    public Parkour() {
        super("Parkour", "555", Category.MOVEMENT);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        if (mc.player.onGround && !mc.player.isSneaking() && !mc.gameSettings.keyBindJump.isPressed() && mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0D, -0.5D, 0.0D).expand(-0.0000000000000000000000000001D, 0.0D, -0.00000000000000000000000000001D)).isEmpty()) mc.player.jump();
    }
}
