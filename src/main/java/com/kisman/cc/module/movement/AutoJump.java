package com.kisman.cc.module.movement;

import com.kisman.cc.module.*;

public class AutoJump extends Module {
    public AutoJump() {
        super("AutoJump", "Automatic jump", Category.MOVEMENT);
    }

    public void onDisable() {
        if(mc.player == null && mc.world == null) return;

        mc.gameSettings.keyBindJump.pressed = false;
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        mc.gameSettings.keyBindJump.pressed = true;
    }
}
