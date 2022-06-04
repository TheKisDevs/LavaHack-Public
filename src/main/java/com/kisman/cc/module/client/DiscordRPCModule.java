package com.kisman.cc.module.client;

import com.kisman.cc.RPC;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

public class DiscordRPCModule extends Module {
    public static DiscordRPCModule instance;

    public Setting impr = new Setting("Impr RPC", this, true);

    public DiscordRPCModule() {
        super("DiscordRPC", "", Category.CLIENT);
        instance = this;

        setmgr.rSetting(impr);

        super.setToggled(true);
    }

    public void onEnable() {RPC.startRPC();}
    public void onDisable() {RPC.stopRPC();}
}
