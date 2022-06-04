package com.kisman.cc.module.player;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.movement.Sprint;
import com.kisman.cc.util.CheckUtil;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class TeleportBack extends Module {
    private double x;
    private double y;
    private double z;

    public TeleportBack() {
        super("TeleportBack", "TeleportBack", Category.PLAYER);
    }

    public void onEnable() {
        if(mc.player != null && mc.world != null) {
            CheckUtil.savecord();
            ChatUtils.complete("Position saved!");
        }
    }

    public void onDisable() {
        if(mc.player != null && mc.world != null) {
            CheckUtil.loadcord();
            ChatUtils.complete("Teleported!");
        }
    }

    public void update() {
        if(mc.player != null && mc.world != null) {
            x = mc.player.posX;
            y = mc.player.posY;
            z = mc.player.posZ;

            if(Sprint.instance.isToggled()) {
                Sprint.instance.setToggled(false);
            }

            if(mc.player.isSprinting()) {
                mc.player.setSprinting(false);
            }

            if(mc.gameSettings.keyBindSneak.isKeyDown()) {
                CheckUtil.loadcord();
            }

            mc.player.onGround = false;
        }
    }
}
