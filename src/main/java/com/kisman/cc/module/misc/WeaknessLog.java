package com.kisman.cc.module.misc;

import com.kisman.cc.module.*;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.TextComponentString;

public class WeaknessLog extends Module {
    public WeaknessLog() {
        super("WeaknessLog", "WeaknessLog", Category.MISC);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        if(mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            mc.player.connection.handleDisconnect(new SPacketDisconnect(new TextComponentString("you got weakness effect")));
            toggle();
        }
    }
}
