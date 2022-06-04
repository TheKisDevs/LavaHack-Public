package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import net.minecraft.init.Blocks;

public class IceSpeed extends Module {
    private double speed;

    public IceSpeed() {
        super("IceSpeed", "IceSpeed", Category.MOVEMENT);

        Kisman.instance.settingsManager.rSetting(new Setting("Speed", this, 0.4f, 0.2f, 1.5f, false));
    }

    public void update() {
        this.speed = Kisman.instance.settingsManager.getSettingByName(this, "Speed").getValDouble();
        Blocks.ICE.slipperiness = (float) this.speed;
        Blocks.PACKED_ICE.slipperiness = (float) this.speed;
        Blocks.FROSTED_ICE.slipperiness = (float) this.speed;
    }

    public void onDisable() {
        Blocks.ICE.slipperiness = 0.98f;
        Blocks.PACKED_ICE.slipperiness = 0.98f;
        Blocks.FROSTED_ICE.slipperiness = 0.98f;
    }
}
