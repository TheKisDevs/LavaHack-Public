package com.kisman.cc.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.TextComponentString;

public class AutoLog extends Module {
    public AutoLog() {
        super("AutoLog", "5", Category.MISC);

        Kisman.instance.settingsManager.rSetting(new Setting("Health", this, 10, 1, 36, true));
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        int health = (int) Kisman.instance.settingsManager.getSettingByName(this, "Health").getValDouble();

        if(mc.player.getHealth() < health) {
            mc.player.connection.handleDisconnect(new SPacketDisconnect(new TextComponentString("your health < " + health)));
            setToggled(false);
        }
    }
}
