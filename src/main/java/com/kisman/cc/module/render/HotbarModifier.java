package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

import java.awt.*;

public class HotbarModifier extends Module {
    public Setting containerShadow = new Setting("Shadow", this, false);
    public Setting primaryAstolfo = new Setting("Primary Astolfo", this, true).setVisible(containerShadow::getValBoolean);
    public Setting offhand = new Setting("Offhand", this, true).setVisible(containerShadow::getValBoolean);
    public Setting offhandGradient = new Setting("Offhand Gradient", this, false).setVisible(() -> offhand.getValBoolean() && containerShadow.getValBoolean());

    public static HotbarModifier instance;

    public HotbarModifier() {
        super("HotbarModifier", Category.RENDER);

        instance = this;

        setmgr.rSetting(containerShadow);
        setmgr.rSetting(primaryAstolfo);
        setmgr.rSetting(offhand);
        setmgr.rSetting(offhandGradient);
    }

    public static Color getPrimaryColor() {
        return instance.primaryAstolfo.getValBoolean() ? ColorUtils.astolfoColorsToColorObj(100, 100) : new Color(255, 255, 255, 152);
    }
}
