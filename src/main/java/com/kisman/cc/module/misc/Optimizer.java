package com.kisman.cc.module.misc;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.optimization.aiimpr.MainAiImpr;

public class Optimizer extends Module {
    private final Setting removeLookAi = new Setting("Remove Entity AI Watch Closest", this, false);
    private final Setting removeLookIdle = new Setting("Remove Entity AI LookIdle", this, false);
    private final Setting replaceLookHelper = new Setting("Replace Look Helper", this, true);
    public final Setting tileEntityRenderRange = new Setting("TileEntity Render Range(Squared)", this, 4096, 0, 4096, true);
    public final Setting customEntityRenderRange = new Setting("Custom Entity Render Range", this, false);
    public final Setting entityRenderRange = new Setting("Entity Render Range", this, 50, 0, 50, true).setVisible(customEntityRenderRange::getValBoolean);

    public static Optimizer instance;

    public Optimizer() {
        super("Optimizer", Category.MISC);

        instance = this;

        setmgr.rSetting(removeLookAi);
        setmgr.rSetting(removeLookIdle);
        setmgr.rSetting(replaceLookHelper);

        setmgr.rSetting(tileEntityRenderRange);

        setmgr.rSetting(customEntityRenderRange);
        setmgr.rSetting(entityRenderRange);
    }

    public void onEnable() {
        MainAiImpr.ENABLED = true;
    }

    public void onDisable() {
        MainAiImpr.ENABLED = MainAiImpr.REMOVE_LOOK_AI = MainAiImpr.REMOVE_LOOK_IDLE = MainAiImpr.REPLACE_LOOK_HELPER = false;
    }

    public void update() {
        MainAiImpr.REMOVE_LOOK_AI = removeLookAi.getValBoolean();
        MainAiImpr.REMOVE_LOOK_IDLE = removeLookIdle.getValBoolean();
        MainAiImpr.REPLACE_LOOK_HELPER = replaceLookHelper.getValBoolean();
    }
}
