package com.kisman.cc.module.client;

import com.kisman.cc.hypixel.util.ConfigHandler;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraftforge.common.config.Configuration;

public class NotEnoughCoinsModule extends Module {
    public static NotEnoughCoinsModule instance;

    public Setting minProfit = new Setting("MinProfit", this, 50000, 1, Integer.MAX_VALUE, true);
    public Setting demand = new Setting("Demand", this, 3, 1, Integer.MAX_VALUE, true);
    public Setting minProfitPercent = new Setting("MinProfitPercent", this, 0, 0, 100, true);
    public Setting alertSound = new Setting("AlertSound", this, true);
    public Setting onlyHypixelWorking = new Setting("OnlyOnHypixelWorking", this, true);

    public NotEnoughCoinsModule() {
        super("NotEnoughCoins", "NotEnoughCoins", Category.CLIENT);

        setmgr.rSetting(minProfit);
        setmgr.rSetting(demand);
        setmgr.rSetting(minProfitPercent);
        setmgr.rSetting(alertSound);
        setmgr.rSetting(onlyHypixelWorking);
    }

    public void onEnable() {
        if(mc.player == null || mc.world == null) return;
        if(onlyHypixelWorking.getValBoolean() && (mc.getCurrentServerData() == null || !mc.getCurrentServerData().serverIP.equalsIgnoreCase("mc.hypixel.net"))) return;
        if(ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "Flip").equals("false")) {
            ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL,
                    "Flip",
                    "true"
            );
        }
    }

    public void onDisable() {
        if(mc.player == null || mc.world == null) return;

        if(ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "Flip").equals("true")) {
            ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL,
                    "Flip",
                    "false"
            );
        }
    }
}
