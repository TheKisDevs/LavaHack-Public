package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;

public class Charms extends Module {
    public Setting polygonOffset = new Setting("PolygonOffset", this, true);
    public Setting targetRender = new Setting("TargetRender", this, true);
    public Setting customColor = new Setting("Use Color", this, false);
    public Setting color = new Setting("Color", this, "Color", new Colour(255, 0, 0));

    public static Charms instance;

    public Charms() {
        super("Charms", "Charms", Category.RENDER);

        instance = this;

        setmgr.rSetting(polygonOffset);

        Kisman.instance.settingsManager.rSetting(new Setting("Texture", this, false));
        setmgr.rSetting(targetRender);

        setmgr.rSetting(customColor);
        setmgr.rSetting(color);
    }
}
