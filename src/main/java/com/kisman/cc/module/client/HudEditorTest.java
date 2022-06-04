package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;

public class HudEditorTest extends Module {
    public HudEditorTest() {
        super("HudEditorTest", Category.CLIENT);
    }

    public void onEnable() {
        mc.displayGuiScreen(Kisman.instance.hudEditorGui);
        super.setToggled(false);
    }

    public boolean isBeta() {return true;}
}
